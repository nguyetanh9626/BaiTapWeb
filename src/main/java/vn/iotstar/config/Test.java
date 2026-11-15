package vn.iotstar.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Video;

/**
 * Class Test kiểm tra khởi tạo kết nối JPA Hibernate, chèn dữ liệu Category & Video vào SQL Server database webst2.
 * Được thiết kế để chạy nhiều lần mà không bị trùng videoId hay trùng categoryname.
 * Kiểm tra xác nhận dữ liệu đã lưu vào SQL Server và kiểm tra transaction rollback khi xảy ra lỗi.
 */
public class Test {
    public static void main(String[] args) {
        System.out.println("=== BAT DAU KIEM TRA KET NOI JPA & TAO BANG TREN SQL SERVER (webst2) ===");
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();

        // 1. Tạo Category với tên duy nhất theo timestamp
        long timestamp = System.currentTimeMillis();
        Category cate = new Category();
        cate.setCategoryname("Iphone " + (timestamp % 100000));
        cate.setImages("iphone_test.jpg");
        cate.setStatus(1);

        // 2. Tạo Video với videoId duy nhất theo timestamp (tránh trùng khóa chính khi chạy nhiều lần)
        String videoId = "v" + (timestamp % 10000000);
        Video video = new Video();
        video.setVideoId(videoId);
        video.setTitle("Video gioi thieu " + cate.getCategoryname());
        video.setActive(true);
        video.setDescription("Video test gioithieu san pham " + cate.getCategoryname());
        video.setViews(100);
        video.setCategory(cate);
        cate.getVideos().add(video);

        try {
            // Thực hiện thêm mới với EntityTransaction
            trans.begin();
            enma.persist(cate);
            enma.persist(video);
            trans.commit();

            System.out.println("=== CHEN THANH CONG CATEGORY (ID: " + cate.getCategoryid() 
                    + ") VA VIDEO (ID: " + video.getVideoId() + ") ===");

            // 3. Kiểm tra Category và Video có thực sự được lưu vào SQL Server không
            enma.clear(); // Xóa cache cấp 1 để buộc truy vấn trực tiếp từ CSDL
            Category savedCate = enma.find(Category.class, cate.getCategoryid());
            Video savedVideo = enma.find(Video.class, videoId);

            if (savedCate != null && savedVideo != null) {
                System.out.println("-> XAC NHAN: Category ('" + savedCate.getCategoryname() 
                        + "') va Video ('" + savedVideo.getVideoId() + "', Active=" + savedVideo.isActive() 
                        + ") DA DUOC LUU THANH CONG VAO SQL SERVER!");
            } else {
                System.err.println("-> CANH BAO: Khong tim thay thong tin trong SQL Server sau khi commit!");
            }

        } catch (Exception e) {
            System.err.println("=== CO LOI XAY RA KHI THUC HIEN TRANSACTION ===");
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
                System.err.println("-> Da rollback transaction thanh cong!");
            }
        }

        // 4. Kiểm tra đảm bảo transaction rollback khi xảy ra lỗi
        System.out.println("=== KIEM TRA TRANSACTION ROLLBACK KHI CO LOI ===");
        EntityTransaction errorTrans = enma.getTransaction();
        try {
            errorTrans.begin();
            Video invalidVideo = new Video();
            // Video không có VideoId (khóa chính null) chắc chắn gây ngoại lệ
            invalidVideo.setTitle("Invalid Video Null ID");
            enma.persist(invalidVideo);
            errorTrans.commit();
            System.err.println("-> Loi logic: Khong nem ngoai le khi videoId null!");
        } catch (Exception e) {
            if (errorTrans.isActive()) {
                errorTrans.rollback();
                System.out.println("-> XAC NHAN: Transaction da duoc rollback thanh cong, du lieu CSDL duoc bao toan!");
            }
        } finally {
            enma.close();
            JPAConfig.close();
        }

        System.out.println("=== KET THUC KIEM TRA TEST.JAVA HOAN TAT THANH CONG ===");
    }
}
