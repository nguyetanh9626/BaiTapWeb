package org.example.dao;

import org.example.model.User;

import java.util.List;

/**
 * Interface UserDao theo tài liệu 06_MVC_3tier.pdf.
 */
public interface UserDao {

    User get(String username);

    void insert(User user);

    boolean checkExistEmail(String email);

    boolean checkExistUsername(String username);

    boolean checkExistPhone(String phone);

    List<User> getAll();

    List<User> search(String keyword);
}
