package vn.iotstar.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Lớp JPAConfig quản lý vòng đời EntityManagerFactory theo chuẩn singleton.
 * Hỗ trợ nạp thông tin kết nối ghi đè từ biến môi trường (BAITAPWEB_DB_*).
 */
@PersistenceContext
public class JPAConfig {

    private static final String PERSISTENCE_UNIT_NAME = "jpa-hibernate-mysql";
    private static EntityManagerFactory factory;

    static {
        try {
            factory = createFactory();
        } catch (Throwable ex) {
            System.err.println("Lỗi khởi tạo EntityManagerFactory: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static EntityManagerFactory createFactory() {
        Map<String, Object> overrides = new HashMap<>();
        String envUser = System.getenv("BAITAPWEB_DB_USER");
        if (envUser != null && !envUser.trim().isEmpty()) {
            overrides.put("jakarta.persistence.jdbc.user", envUser.trim());
        }
        String envPass = System.getenv("BAITAPWEB_DB_PASSWORD");
        if (envPass != null) {
            overrides.put("jakarta.persistence.jdbc.password", envPass.trim());
        }
        String envUrl = System.getenv("BAITAPWEB_DB_JPA_URL");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            overrides.put("jakarta.persistence.jdbc.url", envUrl.trim());
        }
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, overrides);
    }

    /**
     * Lấy EntityManager mới từ EntityManagerFactory dùng chung.
     */
    public static EntityManager getEntityManager() {
        if (factory == null || !factory.isOpen()) {
            synchronized (JPAConfig.class) {
                if (factory == null || !factory.isOpen()) {
                    factory = createFactory();
                }
            }
        }
        return factory.createEntityManager();
    }

    /**
     * Đóng EntityManagerFactory khi dừng ứng dụng.
     */
    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
