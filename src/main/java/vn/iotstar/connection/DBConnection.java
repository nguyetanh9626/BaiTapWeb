package vn.iotstar.connection;

import java.sql.Connection;

/**
 * Lớp DBConnection trong package vn.iotstar.connection theo tài liệu giảng viên (Slide 8 & 10).
 */
public class DBConnection extends org.example.connection.DBConnection {

    public DBConnection() {
        super();
    }

    @Override
    public Connection getConnection() throws java.sql.SQLException {
        return super.getConnection();
    }
}
