package org.example.dao;

import org.example.model.User;

import java.sql.Timestamp;
import java.util.List;

/**
 * Interface UserDao theo tài liệu 06_MVC_3tier.pdf và mở rộng cho Bài tập 02.
 */
public interface UserDao {

    User get(String username);

    User findByUsername(String username);

    User checkLogin(String username, String password);

    void insert(User user);

    boolean checkExistEmail(String email);

    boolean checkExistUsername(String username);

    boolean checkExistPhone(String phone);

    List<User> getAll();

    List<User> search(String keyword);

    User findByEmail(String email);

    User findByUsernameOrEmail(String identifier);

    void updatePassword(int userId, String newHashedPassword);

    void updateOtp(int userId, String otpHash, Timestamp expiry, Timestamp lastSentAt);

    void incrementOtpAttempts(int userId);

    void activateUser(int userId);
}
