package org.example.service;

import org.example.model.UserModel;

/**
 * Interface IUserService kế thừa {@link UserService} để giữ tương thích với phần Slide 1.
 */
public interface IUserService extends UserService {

    /**
     * Xác thực thông tin đăng nhập.
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return UserModel nếu đăng nhập thành công, null nếu thất bại
     */
    @Override
    UserModel login(String username, String password);

    /**
     * Tìm thông tin người dùng theo username.
     *
     * @param username Tên đăng nhập
     * @return UserModel
     */
    UserModel findByUsername(String username);
}
