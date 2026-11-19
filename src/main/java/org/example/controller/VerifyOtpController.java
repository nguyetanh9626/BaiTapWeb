package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.User;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;
import org.example.util.Constant;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller xác thực mã OTP kích hoạt tài khoản và gửi lại mã OTP (/verify-otp, /resend-otp).
 */
@WebServlet(urlPatterns = {"/verify-otp", "/resend-otp"})
public class VerifyOtpController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String servletPath = req.getServletPath();
        String username = req.getParameter("username");

        if (servletPath.contains("resend-otp")) {
            handleResendOtp(req, resp, username);
            return;
        }

        if (username == null || username.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }

        User user = userService.get(username.trim());
        if (user != null && user.isActive()) {
            resp.sendRedirect(req.getContextPath() + "/login?alreadyActive=true");
            return;
        }

        req.setAttribute("username", username.trim());
        if ("true".equals(req.getParameter("sent"))) {
            req.setAttribute("infoMsg", "Mã xác thực OTP đã được gửi tới email đăng ký của bạn. Vui lòng kiểm tra hộp thư (Hiệu lực: 5 phút).");
        }
        if ("true".equals(req.getParameter("resent"))) {
            req.setAttribute("infoMsg", "Đã gửi lại mã OTP mới tới email của bạn. Vui lòng kiểm tra hộp thư (Hiệu lực: 5 phút).");
        }
        String resendError = req.getParameter("resendError");
        if (resendError != null && !resendError.isEmpty()) {
            req.setAttribute("errorMsg", resendError);
        }

        req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String servletPath = req.getServletPath();
        String username = req.getParameter("username");

        if (servletPath.contains("resend-otp")) {
            handleResendOtp(req, resp, username);
            return;
        }

        String otp = req.getParameter("otp");
        if (username == null || username.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }

        String result = userService.verifyOtpAndActivate(username.trim(), otp);
        if ("SUCCESS".equals(result)) {
            resp.sendRedirect(req.getContextPath() + "/login?activated=true");
        } else if ("ALREADY_ACTIVE".equals(result)) {
            resp.sendRedirect(req.getContextPath() + "/login?alreadyActive=true");
        } else {
            req.setAttribute("username", username.trim());
            req.setAttribute("errorMsg", result);
            req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
        }
    }

    private void handleResendOtp(HttpServletRequest req, HttpServletResponse resp, String username)
            throws IOException {
        if (username == null || username.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }
        String result = userService.resendActivationOtp(username.trim());
        String encodedUser = URLEncoder.encode(username.trim(), StandardCharsets.UTF_8);
        if ("SUCCESS".equals(result)) {
            resp.sendRedirect(req.getContextPath() + "/verify-otp?username=" + encodedUser + "&resent=true");
        } else {
            String encodedErr = URLEncoder.encode(result, StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/verify-otp?username=" + encodedUser + "&resendError=" + encodedErr);
        }
    }
}
