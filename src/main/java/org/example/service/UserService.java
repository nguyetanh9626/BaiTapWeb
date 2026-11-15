package org.example.service;

import org.example.model.User;

import java.util.List;

/**
 * Interface UserService theo tài liệu 06_MVC_3tier.pdf.
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
}
