package vn.iotstar.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;

/**
 * Lớp JPAConfig quản lý vòng đời EntityManagerFactory theo chuẩn singleton.
 */
@PersistenceContext
public class JPAConfig {

    private static final String PERSISTENCE_UNIT_NAME = "jpa-hibernate-mysql";
    private static EntityManagerFactory factory;

    static {
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Throwable ex) {
            System.err.println("Lỗi khởi tạo EntityManagerFactory: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Lấy EntityManager mới từ EntityManagerFactory dùng chung.
     */
    public static EntityManager getEntityManager() {
        if (factory == null || !factory.isOpen()) {
            synchronized (JPAConfig.class) {
                if (factory == null || !factory.isOpen()) {
                    factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
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
