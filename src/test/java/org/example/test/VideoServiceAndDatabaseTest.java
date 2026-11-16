package org.example.test;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.dao.VideoDao;
import org.example.dao.VideoDaoImpl;
import org.example.model.User;
import org.example.model.Video;
import org.example.service.VideoService;
import org.example.service.VideoServiceImpl;
import org.example.util.PasswordUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Video DAO, Service, Top 10 và Phân Trang 6 video/trang")
public class VideoServiceAndDatabaseTest {

    private static VideoDao videoDao;
    private static VideoService videoService;
    private static UserDao userDao;

    @BeforeAll
    static void setUp() {
        videoDao = new VideoDaoImpl();
        videoService = new VideoServiceImpl();
        userDao = new UserDaoImpl();

        // Tự động sinh ảnh thumbnail và video mẫu
        File webappDir = new File("src/main/webapp");
        if (webappDir.exists()) {
            SampleMediaGenerator.generateAllSampleMedia(webappDir.getAbsolutePath());
        }
    }

    @Test
    @DisplayName("Lấy Top 10 video mới nhất trên trang Home")
    void testFindTop10Newest() {
        List<Video> top10 = videoService.findTop10Newest();
        assertNotNull(top10);
        assertTrue(top10.size() >= 10, "Danh sách video mới nhất phải đạt tối thiểu 10 video");
        assertTrue(top10.size() <= 10, "Danh sách Top 10 không được vượt quá 10 video");

        // Kiểm tra thứ tự sắp xếp giảm dần theo uploadDate
        for (int i = 0; i < top10.size() - 1; i++) {
            Video current = top10.get(i);
            Video next = top10.get(i + 1);
            assertTrue(current.getUploadDate().compareTo(next.getUploadDate()) >= 0,
                    "Video phải được sắp xếp giảm dần theo thời gian đăng tải");
        }
    }

    @Test
    @DisplayName("Phân trang danh sách video đúng 6 video/trang theo yêu cầu đề bài")
    void testPagination6VideosPerPage() {
        int pageSize = 6;
        int totalVideos = videoService.countAll();
        assertTrue(totalVideos >= 12, "Cơ sở dữ liệu phải có ít nhất 12 video mẫu");

        // Trang 1: đúng 6 video
        List<Video> page1 = videoService.findAll(1, pageSize);
        assertNotNull(page1);
        assertEquals(6, page1.size(), "Trang 1 phải có đúng 6 video");

        // Trang 2: đúng 6 video
        List<Video> page2 = videoService.findAll(2, pageSize);
        assertNotNull(page2);
        assertEquals(6, page2.size(), "Trang 2 phải có đúng 6 video");

        // Đảm bảo không trùng ID giữa trang 1 và trang 2
        for (Video v1 : page1) {
            for (Video v2 : page2) {
                assertNotEquals(v1.getVideoId(), v2.getVideoId(),
                        "Video giữa trang 1 và trang 2 không được trùng lặp");
            }
        }
    }

    @Test
    @DisplayName("Kiểm thử tăng lượt xem video an toàn")
    void testIncrementViews() {
        List<Video> videos = videoService.findTop10Newest();
        assertFalse(videos.isEmpty());

        Video target = videos.get(0);
        int initialViews = target.getViews();

        videoService.incrementViews(target.getVideoId());
        Video updated = videoService.findById(target.getVideoId());

        assertNotNull(updated);
        assertEquals(initialViews + 1, updated.getViews(), "Lượt xem video phải tăng thêm 1");
    }

    @Test
    @DisplayName("Đăng nhập tài khoản cũ bằng plaintext thành công và tự động nâng cấp sang BCrypt")
    void testLoginAndAutoUpgradeToBCrypt() {
        // Tài khoản 'admin' mật khẩu '123456'
        User user = userDao.checkLogin("admin", "123456");
        assertNotNull(user, "Tài khoản admin/123456 phải đăng nhập được thành công");
        assertTrue(user.isActive(), "Tài khoản admin phải có trạng thái kích hoạt active = true");

        // Kiểm tra sau khi đăng nhập, mật khẩu trong DB đã được nâng cấp thành BCrypt hash 60 ký tự
        User refreshed = userDao.findByUsername("admin");
        assertNotNull(refreshed);
        assertTrue(PasswordUtil.isBCrypt(refreshed.getPassWord()),
                "Mật khẩu của tài khoản admin trong CSDL phải tự động được nâng cấp thành BCrypt hash");
        assertTrue(PasswordUtil.checkPassword("123456", refreshed.getPassWord()));
    }
}
