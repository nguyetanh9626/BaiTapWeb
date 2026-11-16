package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.dao.DataAccessException;
import org.example.model.User;
import org.example.model.UserModel;
import org.example.service.IUserService;
import org.example.service.UserServiceImpl;
import org.example.util.Constant;
import org.example.util.CookieUtil;
import org.example.util.PasswordUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller xử lý đăng nhập với Session ("account") và Cookie Remember Me ("username").
 * Hỗ trợ xác thực mật khẩu BCrypt, tương thích tài khoản cũ, và kiểm tra trạng thái kích hoạt tài khoản.
 */
@WebServlet(urlPatterns = "/login")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        // Kiểm tra Cookie username để hỗ trợ ghi nhớ tài khoản (không lưu password trong Cookie)
        String rememberedUsername = CookieUtil.getCookieValue(req, Constant.COOKIE_REMEMBER);
        if (rememberedUsername != null) {
            req.setAttribute("rememberedUsername", rememberedUsername);
            req.setAttribute("rememberChecked", Boolean.TRUE);
            req.setAttribute("fromCookie", Boolean.TRUE);
        }

        if ("true".equals(req.getParameter("activated"))) {
            req.setAttribute("successMsg", "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
        } else if ("true".equals(req.getParameter("alreadyActive"))) {
            req.setAttribute("successMsg", "Tài khoản đã được kích hoạt từ trước. Vui lòng đăng nhập.");
        } else if ("true".equals(req.getParameter("resetSuccess"))) {
            req.setAttribute("successMsg", "Đặt lại mật khẩu mới thành công! Vui lòng đăng nhập với mật khẩu mới.");
        } else if ("true".equals(req.getParameter("registered"))) {
            req.setAttribute("successMsg", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
        } else if ("true".equals(req.getParameter("authRequired"))) {
            req.setAttribute("alert", "Vui lòng đăng nhập để truy cập trang yêu cầu xác thực.");
        } else if ("true".equals(req.getParameter("cookieCleared"))) {
            req.setAttribute("successMsg", "Đã xóa Cookie ghi nhớ tài khoản.");
        } else if ("true".equals(req.getParameter("logout"))) {
            req.setAttribute("successMsg", "Bạn đã đăng xuất thành công.");
        }

        req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        boolean isRememberMe = "on".equals(req.getParameter("remember"));

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            req.setAttribute("alert", "Tài khoản hoặc mật khẩu không được rỗng.");
            req.setAttribute("rememberedUsername", username != null ? username.trim() : "");
            req.setAttribute("rememberChecked", isRememberMe);
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
            return;
        }

        String trimmedUsername = username.trim();
        try {
            // Kiểm tra trước xem tài khoản có tồn tại và chưa kích hoạt không
            User existing = userService.findByUsernameOrEmail(trimmedUsername);
            if (existing != null && PasswordUtil.checkPassword(password, existing.getPassWord())) {
                if (!existing.isActive()) {
                    String encUser = URLEncoder.encode(existing.getUserName(), StandardCharsets.UTF_8);
                    req.setAttribute("notActive", Boolean.TRUE);
                    req.setAttribute("inactiveUsername", existing.getUserName());
                    req.setAttribute("alert", "Tài khoản của bạn chưa được kích hoạt qua mã OTP Email. Vui lòng kích hoạt tài khoản!");
                    req.setAttribute("rememberedUsername", trimmedUsername);
                    req.setAttribute("rememberChecked", isRememberMe);
                    req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
                    return;
                }
            }

            UserModel user = userService.login(trimmedUsername, password);
            if (user != null) {
                HttpSession session = req.getSession(true);
                try {
                    req.changeSessionId(); // Ngăn chặn tấn công Session Fixation theo chuẩn Jakarta Servlet
                } catch (IllegalStateException ignored) {
                }
                session.setAttribute(Constant.SESSION_ACCOUNT, user);
                session.setAttribute(Constant.SESSION_USER, user);
                session.setAttribute(Constant.SESSION_USERNAME, user.getUserName());

                if (isRememberMe) {
                    CookieUtil.addCookie(
                            req, resp,
                            Constant.COOKIE_REMEMBER,
                            user.getUserName(),
                            Constant.COOKIE_MAX_AGE_SECONDS
                    );
                } else {
                    CookieUtil.deleteCookie(req, resp, Constant.COOKIE_REMEMBER);
                }

                resp.sendRedirect(req.getContextPath() + "/waiting");
            } else {
                HttpSession session = req.getSession(true);
                session.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác!");
                resp.sendRedirect(req.getContextPath() + "/error");
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            req.setAttribute("alert", "Không thể kết nối SQL Server: " + e.getMessage());
            req.setAttribute("rememberedUsername", trimmedUsername);
            req.setAttribute("rememberChecked", isRememberMe);
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
        }
    }
}
