package org.example.model;

/**
 * Lớp UserModel kế thừa {@link User} để giữ tương thích hoàn toàn với phần bài tập Slide 1.
 */
public class UserModel extends User {
    private static final long serialVersionUID = 1L;

    public UserModel() {
        super();
    }

    public UserModel(String username, String password, String fullname, String email) {
        super(username, password, fullname, email);
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + getId() +
                ", username='" + getUserName() + '\'' +
                ", fullname='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", roleid=" + getRoleid() +
                '}';
    }
}
