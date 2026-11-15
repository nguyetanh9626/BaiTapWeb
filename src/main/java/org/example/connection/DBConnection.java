package org.example.connection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Lớp quản lý kết nối tới Microsoft SQL Server (Data Access Layer).
 * Đọc cấu hình từ {@code db.properties} trong classpath hoặc biến môi trường.
 */
public class DBConnection {

    private static final String PROPERTIES_FILE = "db.properties";
    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final Properties CONFIG = loadConfig();

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Không thể đọc file cấu hình " + PROPERTIES_FILE + ": " + e.getMessage());
        }
        return props;
    }

    /**
     * Mở một kết nối mới tới cơ sở dữ liệu SQL Server.
     *
     * @return đối tượng {@link Connection} đã kết nối
     * @throws SQLException nếu cấu hình thiếu hoặc không thể kết nối tới SQL Server
     */
    public Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy SQL Server JDBC Driver (" + DRIVER_CLASS + ")", e);
        }

        String envUrl = trimOrNull(System.getenv("BAITAPWEB_DB_URL"));
        if (envUrl != null) {
            String envUser = trimOrEmpty(System.getenv("BAITAPWEB_DB_USER"));
            String envPass = trimOrEmpty(System.getenv("BAITAPWEB_DB_PASSWORD"));
            return envUser.isEmpty()
                    ? DriverManager.getConnection(envUrl)
                    : DriverManager.getConnection(envUrl, envUser, envPass);
        }

        String server = getSetting("BAITAPWEB_DB_SERVER", "db.server", "localhost");
        String port = getSetting("BAITAPWEB_DB_PORT", "db.port", "1433");
        String database = getSetting("BAITAPWEB_DB_NAME", "db.database", "baitapweb");
        boolean integrated = Boolean.parseBoolean(getSetting("BAITAPWEB_DB_INTEGRATED", "db.integratedSecurity", "false"));

        StringBuilder url = new StringBuilder();
        url.append("jdbc:sqlserver://").append(server).append(":").append(port)
           .append(";databaseName=").append(database)
           .append(";encrypt=true;trustServerCertificate=true;loginTimeout=5;");

        if (integrated) {
            try {
                String integratedUrl = url.toString() + "integratedSecurity=true;";
                return DriverManager.getConnection(integratedUrl);
            } catch (SQLException ex) {
                // Tự động fallback sang SQL authentication nếu integrated security thiếu DLL native
                System.err.println("Integrated security failed, falling back to SQL Server authentication: " + ex.getMessage());
            }
        }

        String user = getSetting("BAITAPWEB_DB_USER", "db.user", "sa");
        String password = getSetting("BAITAPWEB_DB_PASSWORD", "db.password", "1234567@a$");

        if (user.isEmpty() || password.isEmpty()) {
            throw new SQLException(
                    "Chưa cấu hình tài khoản/mật khẩu SQL Server trong src/main/resources/db.properties "
                    + "(hoặc biến môi trường BAITAPWEB_DB_USER / BAITAPWEB_DB_PASSWORD).");
        }

        return DriverManager.getConnection(url.toString(), user, password);
    }

    private static String getSetting(String envKey, String propKey, String defaultValue) {
        String envVal = trimOrNull(System.getenv(envKey));
        if (envVal != null) {
            return envVal;
        }
        String propVal = trimOrNull(CONFIG.getProperty(propKey));
        return propVal != null ? propVal : defaultValue;
    }

    private static String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String trimOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
