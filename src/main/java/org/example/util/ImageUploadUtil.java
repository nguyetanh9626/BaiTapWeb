package org.example.util;

import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Tiện ích chuẩn hóa kiểm tra định dạng, dung lượng, lưu trữ và xóa an toàn file ảnh upload
 * dùng chung cho cả JDBC Category và JPA Category.
 */
public final class ImageUploadUtil {

    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB tối đa
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "webp");
    private static final Set<String> PROTECTED_IMAGES = Set.of(
            "default-category.png",
            "avatar.png",
            "avata.png",
            "ao-thun.png",
            "ao-so-mi.png",
            "quan-jean.png",
            "giay-the-thao.png",
            "phu-kien.png"
    );

    private ImageUploadUtil() {
    }

    /**
     * Kiểm tra một chuỗi URL có phải là URL HTTP/HTTPS hợp lệ hay không.
     */
    public static boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        String trimmed = url.trim();
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return false;
        }
        try {
            new URI(trimmed).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lưu ảnh upload từ multipart {@link Part} vào thư mục upload Category thống nhất.
     *
     * @param part part chứa file từ form
     * @return tên file đã đổi tên duy nhất để lưu vào CSDL, hoặc {@code null} nếu không chọn file
     * @throws IllegalArgumentException nếu file không đúng định dạng ảnh hoặc vượt quá dung lượng cho phép
     * @throws IOException              nếu không ghi được file lên đĩa
     */
    public static String saveCategoryImage(Part part) throws IOException {
        if (part == null || part.getSize() <= 0) {
            return null;
        }

        if (part.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Kích thước file ảnh vượt quá giới hạn cho phép (tối đa 5MB).");
        }

        String submitted = part.getSubmittedFileName();
        if (submitted == null || submitted.trim().isEmpty()) {
            return null;
        }

        // Chống Path Traversal: Chỉ lấy tên file gốc, loại bỏ đường dẫn
        String originalName = Paths.get(submitted).getFileName().toString();
        String ext = extractExtension(originalName);
        if (ext == null || !ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException(
                    "Định dạng ảnh không hợp lệ. Chỉ chấp nhận: png, jpg, jpeg, gif, webp.");
        }

        String contentType = part.getContentType();
        if (contentType != null && !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("Tệp tải lên không phải là hình ảnh hợp lệ.");
        }

        Path uploadDir = Constant.getCategoryUploadDir();
        Files.createDirectories(uploadDir);

        // Tạo tên file duy nhất kết hợp timestamp và UUID, không dùng trực tiếp tên người dùng tải lên
        String uniqueFileName = System.currentTimeMillis() + "_"
                + UUID.randomUUID().toString().substring(0, 8) + "." + ext;
        Path targetPath = uploadDir.resolve(uniqueFileName);

        try (InputStream in = part.getInputStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return uniqueFileName;
    }

    /**
     * Xóa file ảnh trong thư mục upload nếu tồn tại.
     * Bảo đảm an toàn: Tuyệt đối không xóa URL bên ngoài và các ảnh mặc định/hệ thống.
     */
    public static void deleteCategoryImageIfExists(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return;
        }
        String trimmed = fileName.trim();
        // Không xóa nếu là URL bên ngoài
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return;
        }

        String safeName = Paths.get(trimmed).getFileName().toString();
        if (PROTECTED_IMAGES.contains(safeName.toLowerCase(Locale.ROOT))) {
            return;
        }

        // Xóa trong thư mục category upload
        Path filePath = Constant.getCategoryUploadDir().resolve(safeName);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
        }

        // Đồng thời kiểm tra thư mục gốc Constant.DIR nếu file được lưu ở cấp ngoài
        Path legacyPath = Paths.get(Constant.DIR).resolve(safeName);
        if (!legacyPath.equals(filePath)) {
            try {
                Files.deleteIfExists(legacyPath);
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Xác định MIME type phù hợp dựa vào tên file ảnh.
     */
    public static String resolveContentType(String fileName) {
        String ext = extractExtension(fileName);
        if (ext == null) {
            return "application/octet-stream";
        }
        return switch (ext) {
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            default -> "image/jpeg";
        };
    }

    private static String extractExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
