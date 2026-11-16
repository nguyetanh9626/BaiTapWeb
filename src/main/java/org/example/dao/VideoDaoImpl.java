package org.example.dao;

import org.example.connection.DBConnection;
import org.example.model.Video;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai VideoDao truy vấn cơ sở dữ liệu baitapweb qua JDBC PreparedStatement.
 */
public class VideoDaoImpl implements VideoDao {

    private final DBConnection dbConnection = new DBConnection();

    private static final String SELECT_JOIN_USER =
            "SELECT v.videoId, v.title, v.description, v.videoPath, v.thumbnail, v.uploadDate, v.views, v.status, v.userId, "
            + "u.fullname AS uploaderName, u.username AS uploaderUsername "
            + "FROM [dbo].[Videos] v "
            + "LEFT JOIN [dbo].[User] u ON v.userId = u.id ";

    @Override
    public void insert(Video video) {
        String sql = "INSERT INTO [dbo].[Videos] (title, description, videoPath, thumbnail, uploadDate, views, status, userId) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, video.getTitle());
            ps.setString(2, video.getDescription());
            ps.setString(3, video.getVideoPath());
            ps.setString(4, video.getThumbnail());
            ps.setTimestamp(5, video.getUploadDate() != null ? video.getUploadDate() : new Timestamp(System.currentTimeMillis()));
            ps.setInt(6, video.getViews());
            ps.setInt(7, video.getStatus());
            ps.setInt(8, video.getUserId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    video.setVideoId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi thêm video mới: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Video video) {
        String sql = "UPDATE [dbo].[Videos] SET title = ?, description = ?, videoPath = ?, thumbnail = ?, status = ? WHERE videoId = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, video.getTitle());
            ps.setString(2, video.getDescription());
            ps.setString(3, video.getVideoPath());
            ps.setString(4, video.getThumbnail());
            ps.setInt(5, video.getStatus());
            ps.setInt(6, video.getVideoId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật video ID #" + video.getVideoId() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int videoId) {
        String sql = "DELETE FROM [dbo].[Videos] WHERE videoId = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, videoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xóa video ID #" + videoId + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Video findById(int videoId) {
        String sql = SELECT_JOIN_USER + "WHERE v.videoId = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, videoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi truy vấn chi tiết video ID #" + videoId + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<Video> findTop10Newest() {
        String sql = "SELECT TOP 10 v.videoId, v.title, v.description, v.videoPath, v.thumbnail, v.uploadDate, v.views, v.status, v.userId, "
                + "u.fullname AS uploaderName, u.username AS uploaderUsername "
                + "FROM [dbo].[Videos] v "
                + "LEFT JOIN [dbo].[User] u ON v.userId = u.id "
                + "WHERE v.status = 1 "
                + "ORDER BY v.uploadDate DESC";
        List<Video> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi lấy danh sách 10 video mới nhất: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Video> findAll(int offset, int limit) {
        String sql = SELECT_JOIN_USER
                + "WHERE v.status = 1 "
                + "ORDER BY v.uploadDate DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        List<Video> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(0, offset));
            ps.setInt(2, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi phân trang danh sách video: " + e.getMessage(), e);
        }
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM [dbo].[Videos] WHERE status = 1";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi đếm tổng số video: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Video> findByUserId(int userId, int offset, int limit) {
        String sql = SELECT_JOIN_USER
                + "WHERE v.userId = ? "
                + "ORDER BY v.uploadDate DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        List<Video> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, Math.max(0, offset));
            ps.setInt(3, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi lấy video của người dùng ID #" + userId + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int countByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM [dbo].[Videos] WHERE userId = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi đếm số video của người dùng: " + e.getMessage(), e);
        }
    }

    @Override
    public void incrementViews(int videoId) {
        String sql = "UPDATE [dbo].[Videos] SET views = views + 1 WHERE videoId = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, videoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Không ngắt tiến trình xem video nếu tăng view count lỗi
            e.printStackTrace();
        }
    }

    @Override
    public List<Video> search(String keyword, int offset, int limit) {
        String sql = SELECT_JOIN_USER
                + "WHERE v.status = 1 AND (v.title LIKE ? OR v.description LIKE ?) "
                + "ORDER BY v.uploadDate DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        List<Video> list = new ArrayList<>();
        String pattern = "%" + (keyword != null ? keyword.trim() : "") + "%";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setInt(3, Math.max(0, offset));
            ps.setInt(4, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi tìm kiếm video: " + e.getMessage(), e);
        }
    }

    @Override
    public int countSearch(String keyword) {
        String sql = "SELECT COUNT(*) FROM [dbo].[Videos] WHERE status = 1 AND (title LIKE ? OR description LIKE ?)";
        String pattern = "%" + (keyword != null ? keyword.trim() : "") + "%";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi đếm kết quả tìm kiếm video: " + e.getMessage(), e);
        }
    }

    private Video mapRow(ResultSet rs) throws SQLException {
        Video video = new Video();
        video.setVideoId(rs.getInt("videoId"));
        video.setTitle(rs.getString("title"));
        video.setDescription(rs.getString("description"));
        video.setVideoPath(rs.getString("videoPath"));
        video.setThumbnail(rs.getString("thumbnail"));
        video.setUploadDate(rs.getTimestamp("uploadDate"));
        video.setViews(rs.getInt("views"));
        video.setStatus(rs.getInt("status"));
        video.setUserId(rs.getInt("userId"));
        video.setUploaderName(rs.getString("uploaderName"));
        video.setUploaderUsername(rs.getString("uploaderUsername"));
        return video;
    }
}
