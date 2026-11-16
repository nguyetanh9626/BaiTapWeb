package org.example.test;

import org.example.util.OtpUtil;
import org.example.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử bảo mật mật khẩu BCrypt và xác thực mã OTP")
public class PasswordAndOtpSecurityTest {

    @Test
    @DisplayName("Băm mật khẩu bằng BCrypt tạo ra chuỗi hash hợp lệ")
    void testBCryptHash() {
        String plain = "MySecretPass@2026";
        String hashed = PasswordUtil.hashPassword(plain);

        assertNotNull(hashed);
        assertEquals(60, hashed.length());
        assertTrue(PasswordUtil.isBCrypt(hashed));
        assertTrue(PasswordUtil.checkPassword(plain, hashed));
        assertFalse(PasswordUtil.checkPassword("WrongPass", hashed));
    }

    @Test
    @DisplayName("Tương thích ngược (Backward Compatibility) với tài khoản cũ lưu plaintext")
    void testLegacyPlaintextCompatibility() {
        // Tài khoản cũ trong DB lưu plaintext "123456"
        String legacyDbPassword = "123456";

        assertFalse(PasswordUtil.isBCrypt(legacyDbPassword));
        // checkPassword vẫn phải đăng nhập được thành công
        assertTrue(PasswordUtil.checkPassword("123456", legacyDbPassword));
        assertFalse(PasswordUtil.checkPassword("wrong123", legacyDbPassword));
    }

    @Test
    @DisplayName("Sinh mã OTP 6 chữ số an toàn và băm SHA-256")
    void testOtpGenerationAndHashing() {
        for (int i = 0; i < 20; i++) {
            String otp = OtpUtil.generateOtp();
            assertNotNull(otp);
            assertEquals(6, otp.length());
            assertTrue(otp.matches("^[0-9]{6}$"), "OTP phải là 6 chữ số: " + otp);

            String hash = OtpUtil.hashOtp(otp);
            assertNotNull(hash);
            assertEquals(64, hash.length(), "SHA-256 hash hex phải dài 64 ký tự");

            assertTrue(OtpUtil.verifyOtp(otp, hash));
            assertFalse(OtpUtil.verifyOtp("000000", hash));
        }
    }
}
