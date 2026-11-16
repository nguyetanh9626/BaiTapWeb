package org.example.dao;

import org.example.model.UserModel;

/**
 * Interface IUserDao kế thừa {@link UserDao} để giữ tương thích hoàn toàn với phần Slide 1 và mở rộng.
 */
public interface IUserDao extends UserDao {

    /**
     * Kiểm tra thông tin đăng nhập của người dùng.
     *
     * @param username Tên đăng nhập hoặc Email
     * @param password Mật khẩu
     * @return UserModel nếu hợp lệ, ngược lại trả về null
     */
    UserModel checkLogin(String username, String password);

    /**
     * Tìm người dùng theo tên đăng nhập.
     *
     * @param username Tên đăng nhập
     * @return UserModel nếu tìm thấy, ngược lại trả về null
     */
    UserModel findByUsername(String username);

    /**
     * Thêm người dùng mới vào hệ thống.
     *
     * @param user Đối tượng người dùng
     */
    void insert(UserModel user);
}
