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
import org.example.util.ValidationUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller xử lý đăng ký tài khoản mới kèm sinh mã OTP kích hoạt gửi qua Email.
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

        if (fullname.length() < 2 || fullname.length() > 100) {
            forwardWithError(req, resp, "Họ và tên phải từ 2 đến 100 ký tự!");
            return;
        }

        if (username.length() < 3 || username.length() > 50 || username.contains(" ")) {
            forwardWithError(req, resp, "Tên đăng nhập phải từ 3 đến 50 ký tự và không chứa khoảng trắng!");
            return;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            forwardWithError(req, resp, "Số điện thoại không đúng định dạng (VD: 0901234567, 10 số bắt đầu bằng 0)!");
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            forwardWithError(req, resp, "Email không đúng định dạng!");
            return;
        }

        if (password.length() < 6) {
            forwardWithError(req, resp, "Mật khẩu phải chứa ít nhất 6 ký tự!");
            return;
        }

        if (confirmPassword != null && !password.equals(confirmPassword)) {
            forwardWithError(req, resp, "Mật khẩu nhập lại không khớp!");
            return;
        }

        try {
            String result = userService.registerWithOtp(email, password, username, fullname, phone);
            if ("SUCCESS".equals(result)) {
                String safeUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/verify-otp?username=" + safeUsername + "&sent=true");
            } else {
                forwardWithError(req, resp, result);
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
