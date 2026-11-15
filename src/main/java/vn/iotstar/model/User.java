package vn.iotstar.model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Model User theo Slide 10 & 22 tài liệu "Kiến trúc 3 tầng và mô hình MVC trong Java".
 */
public class User extends org.example.model.User implements Serializable {
    private static final long serialVersionUID = 1L;

    public User() {
        super();
    }

    public User(String email, String userName, String fullName, String passWord, String avatar, int roleid, String phone, Date createdDate) {
        super(email, userName, fullName, passWord, avatar, roleid, phone, createdDate);
    }
}
