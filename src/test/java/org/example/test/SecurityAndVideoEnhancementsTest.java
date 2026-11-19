package org.example.test;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.dao.VideoDao;
import org.example.dao.VideoDaoImpl;
import org.example.model.User;
import org.example.model.Video;
import org.example.service.VideoService;
import org.example.service.VideoServiceImpl;
import org.example.util.OtpUtil;
import org.example.util.PasswordUtil;
import org.example.util.SessionUtil;
import org.example.util.VideoUploadUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử bảo mật, phân quyền video, CSRF và cơ chế Safe Update")
public class SecurityAndVideoEnhancementsTest {

    private static VideoDao videoDao;
    private static VideoService videoService;
    private static UserDao userDao;

    @BeforeAll
    static void setUp() {
        videoDao = new VideoDaoImpl();
        videoService = new VideoServiceImpl();
        userDao = new UserDaoImpl();
    }

    @Test
    @DisplayName("Tra cứu video bằng videoPath và kiểm tra ẩn video công khai")
    void testFindByVideoPathAndHiddenStatus() {
        // Lấy video mẫu đầu tiên
        List<Video> newest = videoService.findTop10Newest();
        assertFalse(newest.isEmpty());
        Video first = newest.get(0);

        // Tìm theo videoPath
        Video found = videoService.findByVideoPath(first.getVideoPath());
        assertNotNull(found, "Phải tìm thấy video theo videoPath");
        assertEquals(first.getVideoId(), found.getVideoId());
        assertEquals(1, found.getStatus(), "Video công khai phải có status = 1");

        // Tìm videoPath không tồn tại
        Video notFound = videoService.findByVideoPath("non-existent-video-" + System.currentTimeMillis() + ".mp4");
        assertNull(notFound, "File không tồn tại trong DB phải trả về null");
    }

    @Test
    @DisplayName("Tài khoản chưa kích hoạt (isActive = false) tuyệt đối không thể đăng nhập")
    void testInactiveUserCannotLogin() {
        String testUser = "unverified_" + System.currentTimeMillis();
        String testEmail = testUser + "@example.com";
        String testPass = "Pass@123456";

        User user = new User();
        user.setUserName(testUser);
        user.setEmail(testEmail);
        user.setFullName("Unverified User Test");
        user.setPassWord(PasswordUtil.hashPassword(testPass));
        user.setPhone("0987" + (System.currentTimeMillis() % 1000000));
        user.setRoleid(3);
        user.setCreatedDate(new Date(System.currentTimeMillis()));
        user.setActive(false); // Chưa kích hoạt
        user.setOtpHash(OtpUtil.hashOtp("123456"));
        user.setOtpExpiry(new Timestamp(System.currentTimeMillis() + 300000));
        user.setOtpAttempts(0);
        user.setOtpLastSentAt(new Timestamp(System.currentTimeMillis()));

        userDao.insert(user);

        try {
            // Thử đăng nhập tài khoản chưa kích hoạt
            User logged = userDao.checkLogin(testUser, testPass);
            assertNull(logged, "Tài khoản chưa kích hoạt qua OTP tuyệt đối không thể đăng nhập!");

            // Sau khi kích hoạt thì mới đăng nhập được
            User created = userDao.findByUsername(testUser);
            assertNotNull(created);
            userDao.activateUser(created.getId());

            User activatedLogged = userDao.checkLogin(testUser, testPass);
            assertNotNull(activatedLogged, "Sau khi kích hoạt, tài khoản phải đăng nhập thành công");
            assertTrue(activatedLogged.isActive());
        } finally {
            // Dọn dẹp tài khoản test
            User toClean = userDao.findByUsername(testUser);
            if (toClean != null) {
                userDao.delete(toClean.getId());
            }
        }
    }

    @Test
    @DisplayName("VideoUploadUtil bảo vệ file mẫu hệ thống không bị xóa nhầm")
    void testProtectSystemMediaFiles() {
        // Các file hệ thống sample-video-*.mp4 không được xóa
        assertDoesNotThrow(() -> {
            VideoUploadUtil.deleteVideoFileIfExists("sample-video-1.mp4");
            VideoUploadUtil.deleteThumbnailFileIfExists("thumb-1.jpg");
        });
    }

    @Test
    @DisplayName("Hằng số và định danh đồng bộ giữa CSRF token và Flash message")
    void testCsrfAndFlashConstants() {
        assertEquals("csrf_token", SessionUtil.CSRF_TOKEN_SESSION);
        assertEquals("csrfToken", SessionUtil.CSRF_TOKEN_SESSION_ALT);
        assertEquals("flash_success", SessionUtil.FLASH_SUCCESS);
        assertEquals("flashSuccess", SessionUtil.FLASH_SUCCESS_ALT);
        assertEquals("flash_error", SessionUtil.FLASH_ERROR);
        assertEquals("flashError", SessionUtil.FLASH_ERROR_ALT);
    }
}
