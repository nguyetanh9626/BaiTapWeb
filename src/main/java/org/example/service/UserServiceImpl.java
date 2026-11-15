package org.example.service;

import org.example.dao.IUserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.example.model.UserModel;
import org.example.util.Constant;

import java.sql.Date;
import java.util.List;

/**
 * Triển khai Business Logic Layer cho đăng nhập, đăng ký và kiểm tra trùng thông tin.
 */
public class UserServiceImpl implements IUserService {

    private final IUserDao userDao = new UserDaoImpl();

    @Override
    public UserModel login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return null;
        }
        return userDao.checkLogin(username.trim(), password);
    }

    @Override
    public User get(String username) {
        return findByUsername(username);
    }

    @Override
    public UserModel findByUsername(String username) {
        if (isBlank(username)) {
            return null;
        }
        return userDao.findByUsername(username.trim());
    }

    @Override
    public void insert(User user) {
        if (user != null) {
            userDao.insert(user);
        }
    }

    @Override
    public boolean register(String email, String password, String username, String fullname, String phone) {
        if (isBlank(email) || isBlank(password) || isBlank(username)
                || isBlank(fullname) || isBlank(phone)) {
            return false;
        }
        String trimmedEmail = email.trim();
        String trimmedUsername = username.trim();
        String trimmedFullname = fullname.trim();
        String trimmedPhone = phone.trim();

        if (userDao.checkExistEmail(trimmedEmail)
                || userDao.checkExistUsername(trimmedUsername)
                || userDao.checkExistPhone(trimmedPhone)) {
            return false;
        }

        Date createdDate = new Date(System.currentTimeMillis());
        User newUser = new User(
                trimmedEmail,
                trimmedUsername,
                trimmedFullname,
                password,
                null,
                Constant.ROLE_USER,
                trimmedPhone,
                createdDate
        );
        userDao.insert(newUser);
        return true;
    }

    @Override
    public boolean checkExistEmail(String email) {
        return !isBlank(email) && userDao.checkExistEmail(email.trim());
    }

    @Override
    public boolean checkExistUsername(String username) {
        return !isBlank(username) && userDao.checkExistUsername(username.trim());
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return !isBlank(phone) && userDao.checkExistPhone(phone.trim());
    }

    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public List<User> search(String keyword) {
        if (isBlank(keyword)) {
            return userDao.getAll();
        }
        return userDao.search(keyword.trim());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
