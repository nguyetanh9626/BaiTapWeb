package org.example.dao;

/**
 * Ngoại lệ tầng Data Access dùng để truyền lỗi SQL Server rõ ràng lên Service/Controller.
 */
public class DataAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
