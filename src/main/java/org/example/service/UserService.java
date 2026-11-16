package org.example.service;

import org.example.model.User;

import java.util.List;

/**
 * Interface UserService theo tài liệu 06_MVC_3tier.pdf và mở rộng cho Bài tập 02.
 */
public interface UserService {

    User login(String username, String password);

    User get(String username);

    void insert(User user);

    boolean register(String email, String password, String username, String fullname, String phone);

    boolean checkExistEmail(String email);

    boolean checkExistUsername(String username);

    boolean checkExistPhone(String phone);

    List<User> getAll();

    List<User> search(String keyword);

    // Các phương thức mở rộng cho Bài tập 02
    User findByEmail(String email);

    User findByUsernameOrEmail(String identifier);

    String registerWithOtp(String email, String password, String username, String fullname, String phone);

    String verifyOtpAndActivate(String username, String enteredOtp);

    String resendActivationOtp(String username);

    String sendForgotPasswordOtp(String email);

    String verifyResetPasswordOtp(String email, String enteredOtp);

    String resetPassword(String email, String otp, String newPassword);
}
