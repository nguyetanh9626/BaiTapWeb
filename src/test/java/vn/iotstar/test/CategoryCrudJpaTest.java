package vn.iotstar.test;

import org.junit.jupiter.api.*;
import vn.iotstar.dao.CategoryDao;
import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Video;
import vn.iotstar.service.CategoryServiceImpl;
import vn.iotstar.service.ICategoryService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bộ kiểm thử tự động toàn diện cho JPA Category DAO và Service (Phần 10).
 * Bổ sung kiểm tra đầy đủ các trường hợp ngoại lệ, dữ liệu không hợp lệ và chống đệ quy toString/equals.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryCrudJpaTest {

    private static ICategoryDao cateDao;
    private static ICategoryService cateService;
    private static int createdCategoryId;

    @BeforeAll
    public static void setUp() {
        cateDao = new CategoryDao();
        cateService = new CategoryServiceImpl();
    }

    @Test
    @Order(1)
    @DisplayName("1 & 6. Thêm Category mới bằng Service (insert)")
    public void testInsertCategory() {
        Category category = new Category();
        category.setCategoryname("Laptop Gaming Test");
        category.setImages("https://cdn.example.com/laptop.jpg");
        category.setStatus(1);

        cateService.insert(category);

        assertTrue(category.getCategoryid() > 0, "ID danh mục phải được tự động tăng sau khi insert");
        createdCategoryId = category.getCategoryid();
        System.out.println("-> Insert thành công Category ID: " + createdCategoryId);
    }

    @Test
    @Order(2)
    @DisplayName("7. Hiển thị danh sách danh mục (findAll)")
    public void testFindAll() {
        List<Category> list = cateService.findAll();
        assertNotNull(list);
        assertFalse(list.isEmpty(), "Danh sách không được rỗng sau khi đã insert");
        System.out.println("-> Số lượng danh mục hiện có: " + list.size());
    }

    @Test
    @Order(3)
    @DisplayName("8. Tìm kiếm Category theo tên gần đúng (searchByName)")
    public void testSearchByName() {
        List<Category> results = cateService.searchByName("Gaming");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(c -> c.getCategoryname().contains("Gaming")));
        System.out.println("-> Tìm thấy " + results.size() + " danh mục khớp từ khóa 'Gaming'");
    }

    @Test
    @Order(4)
    @DisplayName("9. Xem chi tiết Category theo ID (findById)")
    public void testFindById() {
        Category found = cateService.findById(createdCategoryId);
        assertNotNull(found);
        assertEquals("Laptop Gaming Test", found.getCategoryname());
        assertEquals(1, found.getStatus());
        System.out.println("-> Lấy chi tiết thành công ID " + createdCategoryId + ": " + found.getCategoryname());
    }

    @Test
    @Order(5)
    @DisplayName("10. Sửa Category (update)")
    public void testUpdateCategory() {
        Category category = cateService.findById(createdCategoryId);
        assertNotNull(category);
        category.setCategoryname("Laptop Gaming Test Cap Nhat");
        category.setStatus(0); // Đổi sang trạng thái Khóa

        cateService.update(category);

        Category updated = cateService.findById(createdCategoryId);
        assertEquals("Laptop Gaming Test Cap Nhat", updated.getCategoryname());
        assertEquals(0, updated.getStatus());
        System.out.println("-> Cập nhật thành công Category ID: " + createdCategoryId);
    }

    @Test
    @Order(6)
    @DisplayName("12. Kiểm tra phân trang (findAll with page and pagesize)")
    public void testPagination() {
        List<Category> page1 = cateService.findAll(0, 2);
        assertNotNull(page1);
        assertTrue(page1.size() <= 2);
        System.out.println("-> Phân trang trang 1 (kích thước 2) lấy được: " + page1.size() + " mục");
    }

    @Test
    @Order(7)
    @DisplayName("13. Kiểm tra đếm số lượng Category (count)")
    public void testCount() {
        int total = cateService.count();
        assertTrue(total >= 1, "Tổng số danh mục phải >= 1");
        System.out.println("-> Tổng số danh mục đếm bằng JPQL COUNT: " + total);
    }

    @Test
    @Order(8)
    @DisplayName("15. Kiểm tra trường hợp tên Category bị bỏ trống khi thêm mới")
    public void testInsertEmptyNameThrowsException() {
        Category emptyNameCate = new Category();
        emptyNameCate.setCategoryname("   ");
        emptyNameCate.setStatus(1);

        assertThrows(IllegalArgumentException.class, () -> {
            cateService.insert(emptyNameCate);
        }, "Tên danh mục rỗng phải ném IllegalArgumentException");
        System.out.println("-> Bắt thành công ngoại lệ khi thêm danh mục có tên rỗng");
    }

    @Test
    @Order(9)
    @DisplayName("16. Kiểm tra trường hợp tên Category bị trùng")
    public void testInsertDuplicateNameThrowsException() {
        Category duplicateCate = new Category();
        duplicateCate.setCategoryname("Laptop Gaming Test Cap Nhat");
        duplicateCate.setStatus(1);

        assertThrows(IllegalArgumentException.class, () -> {
            cateService.insert(duplicateCate);
        }, "Tên danh mục trùng phải ném ngoại lệ");
        System.out.println("-> Bắt thành công ngoại lệ khi thêm danh mục trùng tên");
    }

    @Test
    @Order(10)
    @DisplayName("17. Kiểm tra trường hợp ID không tồn tại khi tìm kiếm và xóa")
    public void testNonExistentId() {
        Category notFound = cateService.findById(999999);
        assertNull(notFound, "ID không tồn tại phải trả về null");

        assertThrows(Exception.class, () -> {
            cateService.delete(999999);
        }, "Xóa ID không tồn tại phải ném ngoại lệ");
        System.out.println("-> Xử lý an toàn khi truy vấn và xóa ID không tồn tại");
    }

    @Test
    @Order(11)
    @DisplayName("Bổ sung 1: Cập nhật danh mục không tồn tại ném ngoại lệ")
    public void testUpdateNonExistentThrowsException() {
        Category nonExist = new Category();
        nonExist.setCategoryid(888888);
        nonExist.setCategoryname("Khong Ton Tai");
        assertThrows(IllegalArgumentException.class, () -> {
            cateService.update(nonExist);
        });
        System.out.println("-> Bắt thành công ngoại lệ khi cập nhật danh mục không tồn tại");
    }

    @Test
    @Order(12)
    @DisplayName("Bổ sung 2: Cập nhật danh mục với tên rỗng ném ngoại lệ")
    public void testUpdateEmptyNameThrowsException() {
        Category updateEmpty = new Category();
        updateEmpty.setCategoryid(createdCategoryId);
        updateEmpty.setCategoryname("   ");
        assertThrows(IllegalArgumentException.class, () -> {
            cateService.update(updateEmpty);
        });
        System.out.println("-> Bắt thành công ngoại lệ khi cập nhật danh mục với tên rỗng");
    }

    @Test
    @Order(13)
    @DisplayName("Bổ sung 3: Xóa với ID âm hoặc 0 ném ngoại lệ")
    public void testDeleteInvalidIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            cateService.delete(-5);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            cateService.delete(0);
        });
        System.out.println("-> Bắt thành công ngoại lệ khi xóa ID <= 0");
    }

    @Test
    @Order(14)
    @DisplayName("Bổ sung 4: Tìm kiếm theo ID âm trả về null an toàn")
    public void testFindByIdNegativeReturnsNull() {
        assertNull(cateService.findById(-1));
        assertNull(cateService.findById(0));
        System.out.println("-> findById(<= 0) trả về null an toàn, không ném ngoại lệ 500");
    }

    @Test
    @Order(15)
    @DisplayName("Bổ sung 5: Tìm kiếm theo tên rỗng trả về null an toàn")
    public void testFindByCategorynameNullOrEmpty() {
        assertNull(cateService.findByCategoryname(null));
        assertNull(cateService.findByCategoryname("   "));
        System.out.println("-> findByCategoryname(rỗng) trả về null an toàn");
    }

    @Test
    @Order(16)
    @DisplayName("Bổ sung 6: Tránh vòng lặp đệ quy trong toString, equals, hashCode giữa Category và Video")
    public void testNoRecursionInToStringAndEquals() {
        Category c = new Category();
        c.setCategoryid(100);
        c.setCategoryname("Test Recursion");
        c.setStatus(1);

        Video v = new Video();
        v.setVideoId("vid_rec_01");
        v.setTitle("Video Recursion Test");
        v.setCategory(c);

        List<Video> vList = new ArrayList<>();
        vList.add(v);
        c.setVideos(vList);

        // Kiểm tra toString() không gây StackOverflowError
        assertDoesNotThrow(() -> {
            String cStr = c.toString();
            String vStr = v.toString();
            assertNotNull(cStr);
            assertNotNull(vStr);
        }, "Gọi toString() giữa Category và Video không được ném StackOverflowError");

        // Kiểm tra hashCode() và equals() không gây StackOverflowError
        assertDoesNotThrow(() -> {
            int cHash = c.hashCode();
            int vHash = v.hashCode();
            assertTrue(c.equals(c));
            assertTrue(v.equals(v));
        }, "Gọi equals()/hashCode() không được ném StackOverflowError");

        System.out.println("-> Đã kiểm tra an toàn: Không bị vòng lặp đệ quy trong toString/equals/hashCode");
    }

    @Test
    @Order(17)
    @DisplayName("Bổ sung 7: Thêm Category và Video (Active=boolean, Description=NVARCHAR(MAX)) vào SQL Server")
    public void testPersistCategoryAndVideo() {
        jakarta.persistence.EntityManager em = vn.iotstar.config.JPAConfig.getEntityManager();
        jakarta.persistence.EntityTransaction tx = em.getTransaction();
        String testVidId = "junit_vid_" + (System.currentTimeMillis() % 1000000);
        int testCateId = 0;

        try {
            tx.begin();
            Category testCate = new Category();
            testCate.setCategoryname("JPA Video Test Cate " + (System.currentTimeMillis() % 100000));
            testCate.setImages("test_video_cate.png");
            testCate.setStatus(1);
            em.persist(testCate);

            Video testVideo = new Video();
            testVideo.setVideoId(testVidId);
            testVideo.setActive(true);
            testVideo.setTitle("JPA JUnit Video Title");
            testVideo.setDescription("Detailed description testing NVARCHAR(MAX) column storage in SQL Server");
            testVideo.setViews(999);
            testVideo.setCategory(testCate);
            testCate.getVideos().add(testVideo);
            em.persist(testVideo);

            tx.commit();
            testCateId = testCate.getCategoryid();
            assertTrue(testCateId > 0);

            // Kiểm tra truy vấn từ CSDL
            em.clear();
            Video queriedVideo = em.find(Video.class, testVidId);
            assertNotNull(queriedVideo, "Video phải được lưu thành công vào SQL Server");
            assertTrue(queriedVideo.isActive(), "Trường active kiểu boolean phải là true");
            assertEquals("Detailed description testing NVARCHAR(MAX) column storage in SQL Server", queriedVideo.getDescription());
            assertNotNull(queriedVideo.getCategory(), "Video phải liên kết với Category");
            assertEquals(testCateId, queriedVideo.getCategory().getCategoryid());
            System.out.println("-> Thêm và truy vấn thành công Category và Video với Active boolean và Description NVARCHAR(MAX)");

        } finally {
            // Dọn dẹp dữ liệu test
            if (testCateId > 0) {
                try {
                    jakarta.persistence.EntityTransaction cleanTx = em.getTransaction();
                    cleanTx.begin();
                    Video v = em.find(Video.class, testVidId);
                    if (v != null) em.remove(v);
                    Category c = em.find(Category.class, testCateId);
                    if (c != null) em.remove(c);
                    cleanTx.commit();
                } catch (Exception ignored) {
                }
            }
            em.close();
        }
    }

    @Test
    @Order(18)
    @DisplayName("Bổ sung 8: Đảm bảo transaction rollback khi xảy ra lỗi")
    public void testTransactionRollbackOnError() {
        jakarta.persistence.EntityManager em = vn.iotstar.config.JPAConfig.getEntityManager();
        jakarta.persistence.EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Video invalidVideo = new Video();
            // Khóa chính videoId bị null chắc chắn gây ngoại lệ
            em.persist(invalidVideo);
            tx.commit();
            fail("Phải ném ngoại lệ khi persist Video có khóa chính null");
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            assertFalse(tx.isActive(), "Transaction phải ở trạng thái không active sau khi rollback");
            System.out.println("-> Bắt ngoại lệ và rollback transaction thành công khi gặp lỗi");
        } finally {
            em.close();
        }
    }

    @Test
    @Order(19)
    @DisplayName("11. Xóa Category (delete)")
    public void testDeleteCategory() throws Exception {
        cateService.delete(createdCategoryId);
        Category deleted = cateService.findById(createdCategoryId);
        assertNull(deleted, "Sau khi xóa, findById phải trả về null");
        System.out.println("-> Xóa thành công Category ID: " + createdCategoryId);
    }

    @Test
    @Order(20)
    @DisplayName("Bổ sung 9: Xóa Category có chứa Video liên kết - ngắt khóa ngoại an toàn không văng lỗi FK")
    public void testDeleteCategoryWithVideosUnlinksSafely() throws Exception {
        jakarta.persistence.EntityManager em = vn.iotstar.config.JPAConfig.getEntityManager();
        jakarta.persistence.EntityTransaction tx = em.getTransaction();
        String vid1 = "fk_test_vid1_" + (System.currentTimeMillis() % 1000000);
        String vid2 = "fk_test_vid2_" + (System.currentTimeMillis() % 1000000);
        int parentCateId = 0;

        try {
            tx.begin();
            Category parentCate = new Category();
            parentCate.setCategoryname("Cate Có Video " + (System.currentTimeMillis() % 100000));
            parentCate.setStatus(1);
            em.persist(parentCate);

            Video v1 = new Video();
            v1.setVideoId(vid1);
            v1.setTitle("Video 1 Test FK");
            v1.setActive(true);
            v1.setCategory(parentCate);
            em.persist(v1);

            Video v2 = new Video();
            v2.setVideoId(vid2);
            v2.setTitle("Video 2 Test FK");
            v2.setActive(true);
            v2.setCategory(parentCate);
            em.persist(v2);

            tx.commit();
            parentCateId = parentCate.getCategoryid();
            assertTrue(parentCateId > 0);
        } finally {
            em.close();
        }

        final int deleteTargetId = parentCateId;
        // Thực hiện xóa Category bằng Service (mô phỏng thao tác Admin bấm Xóa trên giao diện web)
        assertDoesNotThrow(() -> {
            cateService.delete(deleteTargetId);
        }, "Xóa Category có chứa Video không được gây lỗi khóa ngoại FK conflict");

        // Kiểm tra Category đã bị xóa khỏi CSDL
        assertNull(cateService.findById(parentCateId), "Category phải không còn tồn tại");

        // Kiểm tra 2 Video vẫn còn nguyên nhưng CategoryId đã được gán NULL an toàn
        jakarta.persistence.EntityManager checkEm = vn.iotstar.config.JPAConfig.getEntityManager();
        try {
            Video queriedV1 = checkEm.find(Video.class, vid1);
            Video queriedV2 = checkEm.find(Video.class, vid2);
            assertNotNull(queriedV1, "Video 1 vẫn tồn tại trong CSDL");
            assertNotNull(queriedV2, "Video 2 vẫn tồn tại trong CSDL");
            assertNull(queriedV1.getCategory(), "CategoryId của Video 1 phải được set null");
            assertNull(queriedV2.getCategory(), "CategoryId của Video 2 phải được set null");

            // Dọn dẹp 2 video test
            jakarta.persistence.EntityTransaction cleanTx = checkEm.getTransaction();
            cleanTx.begin();
            checkEm.remove(queriedV1);
            checkEm.remove(queriedV2);
            cleanTx.commit();
        } finally {
            checkEm.close();
        }
        System.out.println("-> Kiểm tra thành công 100%: Xóa Category có chứa Video ngắt khóa ngoại an toàn, không bị lỗi FK!");
    }
}
