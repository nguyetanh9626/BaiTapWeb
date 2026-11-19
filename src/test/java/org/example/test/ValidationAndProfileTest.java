package org.example.test;

import org.example.dao.IUserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.example.service.CloudinaryService;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;
import org.example.util.OtpUtil;
import org.example.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Validation và chức năng User Profile & Cloudinary")
public class ValidationAndProfileTest {

    private final IUserDao userDao = new UserDaoImpl();
    private final UserService userService = new UserServiceImpl();

    @Test
    @DisplayName("Validation định dạng Email chính xác")
    void testEmailValidation() {
        assertTrue(ValidationUtil.isValidEmail("admin@hcmute.edu.vn"));
        assertTrue(ValidationUtil.isValidEmail("student.24110004@cnkt.edu.vn"));
        assertTrue(ValidationUtil.isValidEmail("user_123@gmail.com"));

        assertFalse(ValidationUtil.isValidEmail(null));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail("   "));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail("@missingusername.com"));
        assertFalse(ValidationUtil.isValidEmail("username@.com"));
    }

    @Test
    @DisplayName("Validation định dạng Số điện thoại Việt Nam")
    void testPhoneValidation() {
        assertTrue(ValidationUtil.isValidPhone("0901234567"));
        assertTrue(ValidationUtil.isValidPhone("0987654321"));
        assertTrue(ValidationUtil.isValidPhone("0389998888"));
        assertTrue(ValidationUtil.isValidPhone("0701112233"));

        assertFalse(ValidationUtil.isValidPhone(null));
        assertFalse(ValidationUtil.isValidPhone(""));
        assertFalse(ValidationUtil.isValidPhone("123456789")); // Không bắt đầu bằng 0
        assertFalse(ValidationUtil.isValidPhone("09012345"));   // Quá ngắn
        assertFalse(ValidationUtil.isValidPhone("0901234567890")); // Quá dài
        assertFalse(ValidationUtil.isValidPhone("0901abc567")); // Chứa chữ cái
    }

    @Test
    @DisplayName("Validation mật khẩu tối thiểu 6 ký tự")
    void testPasswordValidation() {
        assertTrue(ValidationUtil.isValidPassword("123456"));
        assertTrue(ValidationUtil.isValidPassword("StrongPass@2026"));

        assertFalse(ValidationUtil.isValidPassword(null));
        assertFalse(ValidationUtil.isValidPassword(""));
        assertFalse(ValidationUtil.isValidPassword("12345"));
    }

    @Test
    @DisplayName("Validation độ dài chuỗi ký tự")
    void testLengthValidation() {
        assertTrue(ValidationUtil.isLengthInRange("Nguyễn Văn A", 2, 100));
        assertFalse(ValidationUtil.isLengthInRange("A", 2, 100));
        assertFalse(ValidationUtil.isLengthInRange(null, 2, 100));
    }

    @Test
    @DisplayName("Model User hỗ trợ đầy đủ các trường Cloudinary Avatar")
    void testUserAvatarFields() {
        User user = new User();
        user.setId(999);
        user.setFullName("Nguyen Van Test");
        user.setAvatar("https://res.cloudinary.com/demo/image/upload/v1234567/sample.jpg");
        user.setAvatarPublicId("sample_avatar_public_id");

        assertEquals("https://res.cloudinary.com/demo/image/upload/v1234567/sample.jpg", user.getAvatar());
        assertEquals("https://res.cloudinary.com/demo/image/upload/v1234567/sample.jpg", user.getAvatarUrl());
        assertEquals("sample_avatar_public_id", user.getAvatarPublicId());
    }

    @Test
    @DisplayName("Khởi tạo CloudinaryService an toàn khi chưa có biến môi trường")
    void testCloudinaryServiceInitialization() {
        CloudinaryService service = new CloudinaryService();
        assertNotNull(service);
        // Khi chưa cấu hình biến môi trường, isConfigured() trả về false mà không gây crash hệ thống
        if (!service.isConfigured()) {
            assertThrows(IllegalStateException.class, () -> service.uploadAvatar(null, 1));
        }
    }

    @Test
    @DisplayName("Quy trình kiểm tra OTP đổi Email trong User Profile")
    void testEmailChangeOtpFlow() {
        String plainOtp = OtpUtil.generateOtp();
        String hash = OtpUtil.hashOtp(plainOtp);

        assertNotNull(plainOtp);
        assertEquals(6, plainOtp.length());
        assertTrue(OtpUtil.verifyOtp(plainOtp, hash));
        assertFalse(OtpUtil.verifyOtp("999999", hash));
    }

    @Test
    @DisplayName("Cập nhật thông tin cá nhân và kiểm tra trùng lặp trong CSDL")
    void testProfileDatabaseIntegration() {
        User admin = userDao.findByUsername("admin");
        assertNotNull(admin, "Tài khoản admin phải tồn tại trong CSDL");

        int adminId = admin.getId();
        // Kiểm tra số điện thoại của admin với chính admin
        assertFalse(userDao.checkExistPhoneExceptUser(admin.getPhone(), adminId),
                "Số điện thoại hiện tại của chính người dùng không được tính là bị trùng");

        // Kiểm tra số điện thoại của admin với user khác
        assertTrue(userDao.checkExistPhoneExceptUser(admin.getPhone(), adminId + 9999),
                "Số điện thoại của admin phải được báo là đã tồn tại đối với user ID khác");

        // Kiểm tra email của admin với user khác
        assertTrue(userDao.checkExistEmailExceptUser(admin.getEmail(), adminId + 9999),
                "Email của admin phải được báo là đã tồn tại đối với user ID khác");
    }

    @Test
    @DisplayName("TomcatDispatchFilter tương thích SiteMesh 3 trên Tomcat 11")
    void testTomcatDispatchFilter() {
        org.example.filter.TomcatDispatchFilter filter = new org.example.filter.TomcatDispatchFilter();
        assertNotNull(filter);
        assertDoesNotThrow(() -> filter.init(null));
        assertDoesNotThrow(() -> filter.destroy());
    }
}
