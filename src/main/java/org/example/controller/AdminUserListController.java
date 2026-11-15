package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.DataAccessException;
import org.example.model.User;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;
import org.example.util.Constant;
import org.example.util.SessionUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Controller chỉ dành cho Admin (/admin/users) để xem danh sách các tài khoản có thể đăng nhập.
 */
@WebServlet(urlPatterns = "/admin/users")
public class AdminUserListController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User account = SessionUtil.requireAdmin(req, resp);
        if (account == null) {
            return;
        }

        String rawKeyword = req.getParameter("keyword");
        String keyword = rawKeyword == null ? "" : rawKeyword.trim();

        try {
            List<User> userList = keyword.isEmpty()
                    ? userService.getAll()
                    : userService.search(keyword);
            req.setAttribute("userList", userList);
            req.setAttribute("keyword", keyword);
        } catch (DataAccessException e) {
            e.printStackTrace();
            req.setAttribute("userList", Collections.emptyList());
            req.setAttribute("alert", "Không thể tải danh sách tài khoản: " + e.getMessage());
        }

        req.setAttribute("activeMenu", "users");
        req.getRequestDispatcher(Constant.Path.ADMIN_USER_LIST).forward(req, resp);
    }
}
