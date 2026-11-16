package org.example.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Tiện ích mã hóa và xác thực mật khẩu sử dụng thuật toán BCrypt.
 * Hỗ trợ tương thích ngược (Backward Compatibility) cho các tài khoản cũ lưu mật khẩu dạng plaintext.
 */
public final class PasswordUtil {

    private static final int BCRYPT_LOG_ROUNDS = 12;

    private PasswordUtil() {
    }

    /**
     * Băm mật khẩu thuần bằng BCrypt với cost factor 12.
     *
     * @param plainPassword mật khẩu dạng văn bản thường
     * @return chuỗi băm BCrypt an toàn (độ dài 60 ký tự)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống!");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    /**
     * Kiểm tra mật khẩu người dùng nhập vào so với mật khẩu lưu trong cơ sở dữ liệu.
     * Nếu chuỗi trong DB là BCrypt ($2a$, $2b$, $2y$), dùng BCrypt.checkpw.
     * Nếu chuỗi trong DB là plaintext (tài khoản cũ), so sánh chuỗi trực tiếp.
     *
     * @param plainPassword  mật khẩu do người dùng nhập
     * @param storedPassword mật khẩu lưu trong CSDL (BCrypt hash hoặc plaintext cũ)
     * @return true nếu mật khẩu trùng khớp
     */
    public static boolean checkPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }
        if (isBCrypt(storedPassword)) {
            try {
                return BCrypt.checkpw(plainPassword, storedPassword);
            } catch (Exception e) {
                return false;
            }
        }
        // Tương thích tài khoản cũ lưu plaintext
        return plainPassword.equals(storedPassword);
    }

    /**
     * Kiểm tra xem chuỗi mật khẩu trong DB đã được băm BCrypt hay chưa.
     *
     * @param password chuỗi mật khẩu
     * @return true nếu là hash BCrypt chuẩn
     */
    public static boolean isBCrypt(String password) {
        if (password == null || password.length() != 60) {
            return false;
        }
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }
}
