package org.example.util;

import jakarta.servlet.http.Part;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * Tiện ích kiểm tra tính hợp lệ dữ liệu (Validation) dùng chung cho toàn hệ thống.
 * Chống rỗng, sai định dạng, vượt quá độ dài, file giả mạo và XSS.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(0[3|5|7|8|9])[0-9]{8}$|^0[0-9]{9,10}$"
    );

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024L;   // 5MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024L; // 100MB

    private ValidationUtil() {
    }

    public static boolean isNotBlank(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static String clean(String text) {
        return text == null ? "" : text.trim();
    }

    public static boolean isValidEmail(String email) {
        if (isBlank(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (isBlank(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isLengthInRange(String text, int min, int max) {
        if (text == null) {
            return false;
        }
        int len = text.trim().length();
        return len >= min && len <= max;
    }

    public static boolean isValidImageFile(Part part) {
        if (part == null || part.getSize() <= 0) {
            return false;
        }
        if (part.getSize() > MAX_IMAGE_SIZE) {
            return false;
        }
        String mime = part.getContentType();
        if (mime == null) {
            return false;
        }
        String cleanMime = mime.toLowerCase();
        boolean validMime = cleanMime.equals("image/jpeg") || cleanMime.equals("image/jpg")
                || cleanMime.equals("image/png") || cleanMime.equals("image/webp");
        if (!validMime) {
            return false;
        }

        // Kiểm tra phần mở rộng file
        String submittedName = part.getSubmittedFileName();
        if (submittedName == null) {
            return false;
        }
        String ext = submittedName.toLowerCase();
        boolean validExt = ext.endsWith(".jpg") || ext.endsWith(".jpeg")
                || ext.endsWith(".png") || ext.endsWith(".webp");
        if (!validExt) {
            return false;
        }

        // Kiểm tra magic bytes an toàn (chống giả mạo file thực thi đổi đuôi .png/.jpg)
        try (InputStream is = part.getInputStream()) {
            byte[] header = new byte[8];
            int read = is.read(header);
            if (read < 4) {
                return false;
            }
            // JPEG: FF D8 FF
            if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF) {
                return true;
            }
            // PNG: 89 50 4E 47
            if ((header[0] & 0xFF) == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47) {
                return true;
            }
            // WEBP: RIFF....WEBP
            if (header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F') {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * Escape chuỗi đơn giản để chống XSS khi render.
     */
    public static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }
}
