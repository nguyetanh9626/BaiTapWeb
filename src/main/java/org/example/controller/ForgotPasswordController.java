package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;
import org.example.util.Constant;
import org.example.util.ValidationUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller xử lý luồng Quên mật khẩu qua mã OTP gửi tới Email:
 * 1. /forgot-password   : Nhập email gửi OTP
 * 2. /verify-reset-otp  : Nhập và xác thực mã OTP
 * 3. /reset-password    : Đặt mật khẩu mới an toàn (BCrypt)
 */
@WebServlet(urlPatterns = {"/forgot-password", "/verify-reset-otp", "/reset-password"})
public class ForgotPasswordController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String SESSION_RESET_EMAIL = "reset_email";
    private static final String SESSION_RESET_OTP = "reset_otp";

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String servletPath = req.getServletPath();

        if ("/forgot-password".equals(servletPath)) {
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
        } else if ("/verify-reset-otp".equals(servletPath)) {
            String email = req.getParameter("email");
            if (email == null || email.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/forgot-password");
                return;
            }
            req.setAttribute("email", email.trim());
            if ("true".equals(req.getParameter("sent"))) {
                req.setAttribute("infoMsg", "Mã xác thực OTP đã được gửi tới email của bạn. Vui lòng kiểm tra hộp thư (Hiệu lực: 5 phút).");
            }
            req.getRequestDispatcher(Constant.Path.VERIFY_RESET_OTP).forward(req, resp);
        } else if ("/reset-password".equals(servletPath)) {
            // Chống vượt bước qua URL trực tiếp
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute(SESSION_RESET_EMAIL) == null || session.getAttribute(SESSION_RESET_OTP) == null) {
                resp.sendRedirect(req.getContextPath() + "/forgot-password?invalidStep=true");
                return;
            }
            req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String servletPath = req.getServletPath();

        if ("/forgot-password".equals(servletPath)) {
            handleForgotPassword(req, resp);
        } else if ("/verify-reset-otp".equals(servletPath)) {
            handleVerifyResetOtp(req, resp);
        } else if ("/reset-password".equals(servletPath)) {
            handleResetPassword(req, resp);
        }
    }

    private void handleForgotPassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email == null || email.trim().isEmpty() || !ValidationUtil.isValidEmail(email)) {
            req.setAttribute("email", email != null ? email.trim() : "");
            req.setAttribute("alert", "Vui lòng nhập địa chỉ email đúng định dạng!");
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
            return;
        }

        String result = userService.sendForgotPasswordOtp(email.trim());
        if ("SUCCESS".equals(result)) {
            String encEmail = URLEncoder.encode(email.trim(), StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/verify-reset-otp?email=" + encEmail + "&sent=true");
        } else {
            req.setAttribute("email", email.trim());
            req.setAttribute("alert", result);
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
        }
    }

    private void handleVerifyResetOtp(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String otp = req.getParameter("otp");

        if (email == null || email.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }

        String result = userService.verifyResetPasswordOtp(email.trim(), otp != null ? otp.trim() : "");
        if ("SUCCESS".equals(result)) {
            HttpSession session = req.getSession(true);
            session.setAttribute(SESSION_RESET_EMAIL, email.trim());
            session.setAttribute(SESSION_RESET_OTP, otp.trim());
            resp.sendRedirect(req.getContextPath() + "/reset-password");
        } else {
            req.setAttribute("email", email.trim());
            req.setAttribute("alert", result);
            req.getRequestDispatcher(Constant.Path.VERIFY_RESET_OTP).forward(req, resp);
        }
    }

    private void handleResetPassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(SESSION_RESET_EMAIL) == null || session.getAttribute(SESSION_RESET_OTP) == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password?invalidStep=true");
            return;
        }

        String email = (String) session.getAttribute(SESSION_RESET_EMAIL);
        String otp = (String) session.getAttribute(SESSION_RESET_OTP);

        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (newPassword == null || newPassword.length() < 6) {
            req.setAttribute("alert", "Mật khẩu mới phải có ít nhất 6 ký tự!");
            req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("alert", "Mật khẩu nhập lại không khớp!");
            req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
            return;
        }

        String result = userService.resetPassword(email, otp, newPassword);
        if ("SUCCESS".equals(result)) {
            session.removeAttribute(SESSION_RESET_EMAIL);
            session.removeAttribute(SESSION_RESET_OTP);
            resp.sendRedirect(req.getContextPath() + "/login?resetSuccess=true");
        } else {
            req.setAttribute("alert", result);
            req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
        }
    }
}
