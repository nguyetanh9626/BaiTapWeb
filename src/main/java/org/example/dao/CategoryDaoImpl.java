package org.example.dao;

import org.example.connection.DBConnection;
import org.example.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai CategoryDao bằng JDBC PreparedStatement và try-with-resources.
 */
public class CategoryDaoImpl implements CategoryDao {

    private static final String SQL_INSERT = "INSERT INTO Category(cate_name, icons) VALUES (?, ?)";
    private static final String SQL_UPDATE = "UPDATE Category SET cate_name = ?, icons = ? WHERE cate_id = ?";
    private static final String SQL_DELETE = "DELETE FROM Category WHERE cate_id = ?";
    private static final String SQL_GET_BY_ID = "SELECT * FROM Category WHERE cate_id = ?";
    private static final String SQL_GET_BY_NAME = "SELECT * FROM Category WHERE cate_name = ?";
    private static final String SQL_GET_ALL = "SELECT * FROM Category";
    private static final String SQL_SEARCH = "SELECT * FROM Category WHERE cate_name LIKE ?";
    private static final String SQL_COUNT_ICON_OTHER =
            "SELECT COUNT(*) FROM Category WHERE icons = ? AND cate_id <> ?";

    private final DBConnection dbConnection = new DBConnection();

    @Override
    public void insert(Category category) {
        if (category == null) {
            return;
        }
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setString(1, category.getCatename());
            ps.setString(2, category.getIcon());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi thêm danh mục vào SQL Server: " + e.getMessage(), e);
        }
    }

    @Override
    public void edit(Category category) {
        if (category == null) {
            return;
        }
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, category.getCatename());
            ps.setString(2, category.getIcon());
            ps.setInt(3, category.getCateid());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi cập nhật danh mục trong SQL Server: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi xóa danh mục trong SQL Server: " + e.getMessage(), e);
        }
    }

    @Override
    public Category get(int id) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi tìm danh mục theo mã ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Category get(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_BY_NAME)) {
            ps.setString(1, name.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi tìm danh mục theo tên: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(mapRow(rs));
            }
            return categories;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi lấy danh sách danh mục: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Category> search(String keyword) {
        List<Category> categories = new ArrayList<>();
        String pattern = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SEARCH)) {
            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapRow(rs));
                }
            }
            return categories;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi tìm kiếm danh mục: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isIconUsedByOtherCategory(String icon, int excludeCateId) {
        if (icon == null || icon.trim().isEmpty()) {
            return false;
        }
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_COUNT_ICON_OTHER)) {
            ps.setString(1, icon.trim());
            ps.setInt(2, excludeCateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi kiểm tra file ảnh danh mục: " + e.getMessage(), e);
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCateid(rs.getInt("cate_id"));
        category.setCatename(rs.getString("cate_name"));
        category.setIcon(rs.getString("icons"));
        return category;
    }
}
