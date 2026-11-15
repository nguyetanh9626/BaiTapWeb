package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.dao.DataAccessException;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;
import org.example.util.Constant;

import java.io.IOException;

/**
 * Controller xử lý đăng ký tài khoản mới theo tài liệu 06_MVC_3tier.pdf.
 */
@WebServlet(urlPatterns = "/register")
public class RegisterController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email = trim(req.getParameter("email"));
        String username = trim(req.getParameter("username"));
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String fullname = trim(req.getParameter("fullname"));
        String phone = trim(req.getParameter("phone"));

        req.setAttribute("valEmail", email);
        req.setAttribute("valUsername", username);
        req.setAttribute("valFullname", fullname);
        req.setAttribute("valPhone", phone);

        if (email.isEmpty() || username.isEmpty() || fullname.isEmpty()
                || phone.isEmpty() || password == null || password.isEmpty()) {
            forwardWithError(req, resp, "Vui lòng nhập đầy đủ tất cả thông tin đăng ký!");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            forwardWithError(req, resp, "Email không đúng định dạng!");
            return;
        }

        if (!phone.matches("^[0-9+\\-\\s]{8,15}$")) {
            forwardWithError(req, resp, "Số điện thoại phải gồm 8–15 chữ số!");
            return;
        }

        if (confirmPassword != null && !password.equals(confirmPassword)) {
            forwardWithError(req, resp, "Mật khẩu nhập lại không khớp!");
            return;
        }

        try {
            if (userService.checkExistEmail(email)) {
                forwardWithError(req, resp, "Email đã tồn tại!");
                return;
            }
            if (userService.checkExistUsername(username)) {
                forwardWithError(req, resp, "Tài khoản đã tồn tại!");
                return;
            }
            if (userService.checkExistPhone(phone)) {
                forwardWithError(req, resp, "Số điện thoại đã tồn tại!");
                return;
            }

            boolean isSuccess = userService.register(email, password, username, fullname, phone);
            if (isSuccess) {
                resp.sendRedirect(req.getContextPath() + "/login?registered=true");
            } else {
                forwardWithError(req, resp, "System error! Không thể đăng ký tài khoản.");
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            forwardWithError(req, resp, "Lỗi cơ sở dữ liệu SQL Server: " + e.getMessage());
        }
    }

    private void forwardWithError(HttpServletRequest req, HttpServletResponse resp, String alertMsg)
            throws ServletException, IOException {
        req.setAttribute("alert", alertMsg);
        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
