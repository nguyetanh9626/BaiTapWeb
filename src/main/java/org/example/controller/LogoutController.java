package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.util.Constant;
import org.example.util.CookieUtil;

import java.io.IOException;

/**
 * Controller xử lý đăng xuất (hủy Session) và xóa Cookie Remember Me khi cần thiết.
 */
@WebServlet(urlPatterns = {"/logout", "/forget-cookie"})
public class LogoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        String path = req.getServletPath();
        if ("/forget-cookie".equals(path)) {
            CookieUtil.deleteCookie(req, resp, Constant.COOKIE_REMEMBER);
            resp.sendRedirect(req.getContextPath() + "/login?cookieCleared=true");
            return;
        }

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.removeAttribute(Constant.SESSION_ACCOUNT);
            session.removeAttribute(Constant.SESSION_USER);
            session.removeAttribute(Constant.SESSION_USERNAME);
            session.invalidate();
        }

        if ("true".equals(req.getParameter("clearCookie"))) {
            CookieUtil.deleteCookie(req, resp, Constant.COOKIE_REMEMBER);
        }

        if ("login".equals(req.getParameter("redirect"))) {
            resp.sendRedirect(req.getContextPath() + "/login?logout=true");
        } else {
            resp.sendRedirect(req.getContextPath() + "/home?logout=true");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
