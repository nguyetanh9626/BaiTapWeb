package org.example.dao;

import org.example.connection.DBConnection;
import org.example.model.User;
import org.example.model.UserModel;
import org.example.util.Constant;
import org.example.util.PasswordUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai Data Access Layer truy vấn SQL Server qua JDBC {@link PreparedStatement}.
 * Hỗ trợ băm mật khẩu BCrypt, tương thích tài khoản cũ, và quản lý xác thực OTP.
 */
public class UserDaoImpl implements IUserDao {

    private static final String SELECT_COLUMNS =
            "id, email, username, fullname, password, avatar, roleid, phone, createddate, "
            + "isActive, otpHash, otpExpiry, otpAttempts, otpLastSentAt ";

    private static final String SQL_SELECT_BY_USERNAME =
            "SELECT " + SELECT_COLUMNS + "FROM [User] WHERE username = ?";

    private static final String SQL_SELECT_BY_EMAIL =
            "SELECT " + SELECT_COLUMNS + "FROM [User] WHERE email = ?";

    private static final String SQL_SELECT_BY_USER_OR_EMAIL =
            "SELECT " + SELECT_COLUMNS + "FROM [User] WHERE username = ? OR email = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT " + SELECT_COLUMNS + "FROM [User] ORDER BY roleid ASC, id ASC";

    private static final String SQL_SEARCH_USERS =
            "SELECT " + SELECT_COLUMNS + "FROM [User] WHERE username LIKE ? OR fullname LIKE ? OR email LIKE ? "
            + "ORDER BY roleid ASC, id ASC";

    private static final String SQL_INSERT_USER =
            "INSERT INTO [User] (email, username, fullname, password, avatar, roleid, phone, createddate, isActive, otpHash, otpExpiry, otpAttempts, otpLastSentAt) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE_PASSWORD =
            "UPDATE [User] SET password = ? WHERE id = ?";

    private static final String SQL_UPDATE_OTP =
            "UPDATE [User] SET otpHash = ?, otpExpiry = ?, otpAttempts = 0, otpLastSentAt = ? WHERE id = ?";

    private static final String SQL_INCREMENT_OTP_ATTEMPTS =
            "UPDATE [User] SET otpAttempts = otpAttempts + 1 WHERE id = ?";

    private static final String SQL_ACTIVATE_USER =
            "UPDATE [User] SET isActive = 1, otpHash = NULL, otpExpiry = NULL, otpAttempts = 0 WHERE id = ?";

    private static final String SQL_EXISTS_EMAIL = "SELECT 1 FROM [User] WHERE email = ?";
    private static final String SQL_EXISTS_USERNAME = "SELECT 1 FROM [User] WHERE username = ?";
    private static final String SQL_EXISTS_PHONE = "SELECT 1 FROM [User] WHERE phone = ?";

    private final DBConnection dbConnection = new DBConnection();

    @Override
    public User get(String username) {
        return findByUsername(username);
    }

    @Override
    public UserModel findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_USERNAME)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi truy vấn người dùng theo username: " + e.getMessage(), e);
        }
    }

    @Override
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_EMAIL)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi truy vấn người dùng theo email: " + e.getMessage(), e);
        }
    }

    @Override
    public User findByUsernameOrEmail(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }
        String clean = identifier.trim();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_USER_OR_EMAIL)) {
            ps.setString(1, clean);
            ps.setString(2, clean);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi truy vấn người dùng theo username hoặc email: " + e.getMessage(), e);
        }
    }

    @Override
    public UserModel checkLogin(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || password == null) {
            return null;
        }
        User user = findByUsernameOrEmail(usernameOrEmail);
        if (user == null) {
            return null;
        }

        // Kiểm tra mật khẩu (hỗ trợ cả BCrypt và plaintext tài khoản cũ)
        if (PasswordUtil.checkPassword(password, user.getPassWord())) {
            // Tự động nâng cấp mật khẩu plaintext sang BCrypt hash trong database
            if (!PasswordUtil.isBCrypt(user.getPassWord())) {
                try {
                    String newHash = PasswordUtil.hashPassword(password);
                    updatePassword(user.getId(), newHash);
                    user.setPassWord(newHash);
                } catch (Exception ex) {
                    // Không chặn đăng nhập nếu nâng cấp lỗi
                    ex.printStackTrace();
                }
            }
            return (UserModel) user;
        }
        return null;
    }

    @Override
    public void insert(UserModel user) {
        insert((User) user);
    }

    @Override
    public void insert(User user) {
        if (user == null) {
            return;
        }
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_USER)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getUserName());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getPassWord());
            ps.setString(5, user.getAvatar());
            ps.setInt(6, user.getRoleid() > 0 ? user.getRoleid() : Constant.ROLE_USER);
            ps.setString(7, user.getPhone());
            ps.setDate(8, user.getCreatedDate() != null
                    ? user.getCreatedDate()
                    : new Date(System.currentTimeMillis()));
            ps.setBoolean(9, user.isActive());
            ps.setString(10, user.getOtpHash());
            ps.setTimestamp(11, user.getOtpExpiry());
            ps.setInt(12, user.getOtpAttempts());
            ps.setTimestamp(13, user.getOtpLastSentAt());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi thêm tài khoản vào SQL Server: " + e.getMessage(), e);
        }
    }

    @Override
    public void updatePassword(int userId, String newHashedPassword) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_PASSWORD)) {
            ps.setString(1, newHashedPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi cập nhật mật khẩu người dùng: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateOtp(int userId, String otpHash, Timestamp expiry, Timestamp lastSentAt) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_OTP)) {
            ps.setString(1, otpHash);
            ps.setTimestamp(2, expiry);
            ps.setTimestamp(3, lastSentAt);
            ps.setInt(4, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi cập nhật mã OTP người dùng: " + e.getMessage(), e);
        }
    }

    @Override
    public void incrementOtpAttempts(int userId) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INCREMENT_OTP_ATTEMPTS)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi cập nhật số lần nhập OTP sai: " + e.getMessage(), e);
        }
    }

    @Override
    public void activateUser(int userId) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_ACTIVATE_USER)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi kích hoạt tài khoản: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean checkExistEmail(String email) {
        return existsByQuery(SQL_EXISTS_EMAIL, email);
    }

    @Override
    public boolean checkExistUsername(String username) {
        return existsByQuery(SQL_EXISTS_USERNAME, username);
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return existsByQuery(SQL_EXISTS_PHONE, phone);
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi lấy danh sách tài khoản từ SQL Server: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        List<User> users = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SEARCH_USERS)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }
            return users;
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi tìm kiếm tài khoản trong SQL Server: " + e.getMessage(), e);
        }
    }

    private boolean existsByQuery(String sql, String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi kiểm tra trùng dữ liệu trong SQL Server: " + e.getMessage(), e);
        }
    }

    private UserModel mapRow(ResultSet rs) throws SQLException {
        UserModel user = new UserModel();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setUserName(rs.getString("username"));
        user.setFullName(rs.getString("fullname"));
        user.setPassWord(rs.getString("password"));
        user.setAvatar(rs.getString("avatar"));
        user.setRoleid(rs.getInt("roleid"));
        user.setPhone(rs.getString("phone"));
        user.setCreatedDate(rs.getDate("createddate"));

        // Các cột mới bổ sung
        try {
            user.setActive(rs.getBoolean("isActive"));
            user.setOtpHash(rs.getString("otpHash"));
            user.setOtpExpiry(rs.getTimestamp("otpExpiry"));
            user.setOtpAttempts(rs.getInt("otpAttempts"));
            user.setOtpLastSentAt(rs.getTimestamp("otpLastSentAt"));
        } catch (SQLException ignored) {
            user.setActive(true);
        }

        return user;
    }
}
