package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.dao.DataAccessException;
import org.example.model.UserModel;
import org.example.service.IUserService;
import org.example.service.UserServiceImpl;
import org.example.util.Constant;
import org.example.util.CookieUtil;

import java.io.IOException;

/**
 * Controller xử lý đăng nhập với Session ("account") và Cookie Remember Me ("username").
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

        if ("true".equals(req.getParameter("registered"))) {
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
