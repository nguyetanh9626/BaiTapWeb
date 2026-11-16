package org.example.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Tiện ích sinh mã OTP bảo mật và băm mã OTP theo chuẩn SHA-256.
 */
public final class OtpUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public static final int OTP_EXPIRY_MINUTES = 5;
    public static final int OTP_RESEND_COOLDOWN_SECONDS = 60;
    public static final int MAX_OTP_ATTEMPTS = 5;

    private OtpUtil() {
    }

    /**
     * Sinh mã OTP ngẫu nhiên gồm 6 chữ số an toàn với SecureRandom.
     *
     * @return chuỗi OTP 6 số (ví dụ: "748291")
     */
    public static String generateOtp() {
        int number = SECURE_RANDOM.nextInt(900000) + 100000;
        return String.valueOf(number);
    }

    /**
     * Băm mã OTP bằng SHA-256 trước khi lưu vào cơ sở dữ liệu.
     *
     * @param otp mã OTP dạng text
     * @return chuỗi hash hex 64 ký tự
     */
    public static String hashOtp(String otp) {
        if (otp == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(otp.trim().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Không tìm thấy thuật toán SHA-256", e);
        }
    }

    /**
     * Kiểm tra mã OTP người dùng nhập vào so với hash trong CSDL.
     *
     * @param plainOtp  OTP người dùng nhập
     * @param hashedOtp OTP hash trong CSDL
     * @return true nếu khớp
     */
    public static boolean verifyOtp(String plainOtp, String hashedOtp) {
        if (plainOtp == null || hashedOtp == null) {
            return false;
        }
        String computedHash = hashOtp(plainOtp);
        return computedHash.equalsIgnoreCase(hashedOtp);
    }
}
