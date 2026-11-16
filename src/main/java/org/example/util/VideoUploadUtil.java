package org.example.util;

import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Tiện ích xử lý upload và quản lý file Video, Thumbnail bảo mật.
 * Kiểm tra định dạng MIME, kích thước, tên file an toàn (UUID) và chống tấn công Path Traversal.
 */
public final class VideoUploadUtil {

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(
            Arrays.asList(".png", ".jpg", ".jpeg", ".webp", ".gif")
    );

    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS = new HashSet<>(
            Arrays.asList(".mp4", ".webm", ".mkv", ".mov", ".avi")
    );

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;    // 5MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024;  // 100MB

    private VideoUploadUtil() {
    }

    /**
     * Lưu file video từ Part upload.
     *
     * @param part đối tượng Part
     * @return tên file lưu trữ trên đĩa (UUID.ext)
     */
    public static String saveVideo(Part part) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        if (part.getSize() > MAX_VIDEO_SIZE) {
            throw new IllegalArgumentException("Dung lượng video không được vượt quá 100MB!");
        }

        String submittedName = part.getSubmittedFileName();
        if (submittedName == null || submittedName.trim().isEmpty()) {
            return null;
        }

        String ext = getFileExtension(submittedName).toLowerCase(Locale.ROOT);
        if (!ALLOWED_VIDEO_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("Định dạng video không được hỗ trợ! Chỉ chấp nhận: mp4, webm, mkv, mov, avi");
        }

        Path dir = Constant.getVideoUploadDir();
        Files.createDirectories(dir);

        String savedFilename = UUID.randomUUID() + ext;
        Path targetPath = dir.resolve(savedFilename).normalize();

        // Chống path traversal
        if (!targetPath.startsWith(dir)) {
            throw new SecurityException("Đường dẫn file không hợp lệ!");
        }

        try (InputStream in = part.getInputStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        return savedFilename;
    }

    /**
     * Lưu file thumbnail từ Part upload.
     *
     * @param part đối tượng Part
     * @return tên file lưu trữ trên đĩa (UUID.ext)
     */
    public static String saveThumbnail(Part part) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        if (part.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Dung lượng ảnh thumbnail không được vượt quá 5MB!");
        }

        String submittedName = part.getSubmittedFileName();
        if (submittedName == null || submittedName.trim().isEmpty()) {
            return null;
        }

        String ext = getFileExtension(submittedName).toLowerCase(Locale.ROOT);
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("Định dạng ảnh thumbnail không hợp lệ! Chỉ chấp nhận: png, jpg, jpeg, webp, gif");
        }

        Path dir = Constant.getThumbnailUploadDir();
        Files.createDirectories(dir);

        String savedFilename = UUID.randomUUID() + ext;
        Path targetPath = dir.resolve(savedFilename).normalize();

        if (!targetPath.startsWith(dir)) {
            throw new SecurityException("Đường dẫn file không hợp lệ!");
        }

        try (InputStream in = part.getInputStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        return savedFilename;
    }

    /**
     * Xóa file video vật lý trên đĩa.
     */
    public static void deleteVideoFileIfExists(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return;
        }
        // Không xóa các video mẫu hệ thống
        if (filename.startsWith("sample-video")) {
            return;
        }
        try {
            Path target = Constant.getVideoUploadDir().resolve(filename).normalize();
            if (target.startsWith(Constant.getVideoUploadDir()) && Files.exists(target)) {
                Files.delete(target);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xóa file thumbnail vật lý trên đĩa.
     */
    public static void deleteThumbnailFileIfExists(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return;
        }
        // Không xóa các thumbnail mẫu hệ thống
        if (filename.startsWith("thumb-")) {
            return;
        }
        try {
            Path target = Constant.getThumbnailUploadDir().resolve(filename).normalize();
            if (target.startsWith(Constant.getThumbnailUploadDir()) && Files.exists(target)) {
                Files.delete(target);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDot);
    }
}
