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
 * Controller Protected bảo vệ các trang /member/myaccount, /admin/home, /manager/home.
 */
@WebServlet(urlPatterns = {"/member/myaccount", "/admin/home", "/manager/home"})
public class ProtectedController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User account = SessionUtil.requireLogin(req, resp);
        if (account == null) {
            return;
        }

        String path = req.getServletPath();
        switch (path) {
            case "/admin/home":
                if (account.getRoleid() != Constant.ROLE_ADMIN) {
                    resp.sendRedirect(req.getContextPath() + "/home?forbidden=true");
                    return;
                }
                try {
                    List<User> userList = userService.getAll();
                    req.setAttribute("userList", userList);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                    req.setAttribute("userList", Collections.emptyList());
                    req.setAttribute("alert", "Không thể tải danh sách tài khoản: " + e.getMessage());
                }
                req.getRequestDispatcher(Constant.Path.ADMIN_HOME).forward(req, resp);
                break;

            case "/manager/home":
                if (account.getRoleid() != Constant.ROLE_ADMIN
                        && account.getRoleid() != Constant.ROLE_MANAGER) {
                    resp.sendRedirect(req.getContextPath() + "/home?forbidden=true");
                    return;
                }
                req.getRequestDispatcher(Constant.Path.MANAGER_HOME).forward(req, resp);
                break;

            case "/member/myaccount":
            default:
                req.getRequestDispatcher(Constant.Path.MY_ACCOUNT).forward(req, resp);
                break;
        }
    }
}
