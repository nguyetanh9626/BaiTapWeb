package org.example.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.http.Part;
import org.example.util.ValidationUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dịch vụ tải lên và quản lý ảnh đại diện User trên Cloudinary bằng Java SDK (HTTP5).
 * Nạp thông tin cấu hình an toàn từ biến môi trường hoặc file db.properties.
 * Tuyệt đối không log hoặc để lộ API Secret.
 */
public class CloudinaryService {

    private static final Logger LOGGER = Logger.getLogger(CloudinaryService.class.getName());

    private final Cloudinary cloudinary;
    private final boolean configured;

    public static class UploadResult {
        private final String secureUrl;
        private final String publicId;

        public UploadResult(String secureUrl, String publicId) {
            this.secureUrl = secureUrl;
            this.publicId = publicId;
        }

        public String getSecureUrl() {
            return secureUrl;
        }

        public String getPublicId() {
            return publicId;
        }
    }

    public CloudinaryService() {
        String cloudName = getSetting("CLOUDINARY_CLOUD_NAME", "cloudinary.cloud_name");
        String apiKey = getSetting("CLOUDINARY_API_KEY", "cloudinary.api_key");
        String apiSecret = getSetting("CLOUDINARY_API_SECRET", "cloudinary.api_secret");

        if (ValidationUtil.isNotBlank(cloudName)
                && ValidationUtil.isNotBlank(apiKey)
                && ValidationUtil.isNotBlank(apiSecret)) {
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", cloudName.trim());
            config.put("api_key", apiKey.trim());
            config.put("api_secret", apiSecret.trim());
            config.put("secure", true);
            this.cloudinary = new Cloudinary(config);
            this.configured = true;
        } else {
            this.cloudinary = null;
            this.configured = false;
            LOGGER.log(Level.INFO, "[CloudinaryService] Chưa cấu hình đầy đủ biến môi trường CLOUDINARY_CLOUD_NAME / API_KEY / API_SECRET.");
        }
    }

    public boolean isConfigured() {
        return configured;
    }

    /**
     * Upload ảnh avatar từ Multipart Part lên Cloudinary.
     *
     * @param part Part avatar nhận được từ HTTP request
     * @param userId ID người dùng để định danh
     * @return UploadResult chứa secure_url và public_id
     * @throws Exception khi upload thất bại hoặc chưa cấu hình
     */
    public UploadResult uploadAvatar(Part part, int userId) throws Exception {
        if (!configured || cloudinary == null) {
            throw new IllegalStateException("Hệ thống chưa được cấu hình Cloudinary (CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET).");
        }

        if (part == null || part.getSize() <= 0) {
            throw new IllegalArgumentException("Không tìm thấy file ảnh tải lên!");
        }

        if (!ValidationUtil.isValidImageFile(part)) {
            throw new IllegalArgumentException("File tải lên không phải ảnh hợp lệ (chỉ hỗ trợ JPG, JPEG, PNG, WEBP tối đa 5MB)!");
        }

        byte[] bytes;
        try (InputStream is = part.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            bytes = baos.toByteArray();
        }

        Map<String, Object> params = ObjectUtils.asMap(
                "folder", "baitapweb/avatars",
                "public_id", "user_" + userId + "_" + System.currentTimeMillis(),
                "overwrite", true,
                "resource_type", "image"
        );

        @SuppressWarnings("rawtypes")
        Map result = cloudinary.uploader().upload(bytes, params);

        if (result == null) {
            throw new IOException("Không nhận được phản hồi từ dịch vụ Cloudinary.");
        }

        String secureUrl = (String) result.get("secure_url");
        String publicId = (String) result.get("public_id");

        if (secureUrl == null || publicId == null) {
            throw new IOException("Phản hồi từ Cloudinary thiếu secure_url hoặc public_id.");
        }

        return new UploadResult(secureUrl, publicId);
    }

    /**
     * Xóa ảnh cũ trên Cloudinary khi thay ảnh mới thành công hoặc khi cần dọn dẹp ảnh lỗi.
     *
     * @param publicId public_id ảnh trên Cloudinary
     */
    public boolean deleteImage(String publicId) {
        if (!configured || cloudinary == null || ValidationUtil.isBlank(publicId)) {
            return false;
        }
        try {
            @SuppressWarnings("rawtypes")
            Map result = cloudinary.uploader().destroy(publicId.trim(), ObjectUtils.emptyMap());
            return result != null && "ok".equals(result.get("result"));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể xóa ảnh cũ trên Cloudinary: {0}", e.getMessage());
            return false;
        }
    }

    private static String getSetting(String envKey, String propKey) {
        String val = System.getenv(envKey);
        if (ValidationUtil.isNotBlank(val)) {
            return val.trim();
        }
        try (InputStream in = CloudinaryService.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                String propVal = props.getProperty(propKey);
                if (ValidationUtil.isNotBlank(propVal)) {
                    return propVal.trim();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
