package org.example.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.model.User;

import java.io.IOException;

/**
 * Tiện ích kiểm tra Session đăng nhập và quản lý thông báo flash giữa các redirect (PRG).
 */
public final class SessionUtil {

    public static final String FLASH_SUCCESS = "flash_success";
    public static final String FLASH_SUCCESS_ALT = "flashSuccess";
    public static final String FLASH_ERROR = "flash_error";
    public static final String FLASH_ERROR_ALT = "flashError";
    public static final String CSRF_TOKEN_SESSION = "csrf_token";
    public static final String CSRF_TOKEN_SESSION_ALT = "csrfToken";

    private SessionUtil() {
    }

    /**
     * Lấy hoặc tạo mới CSRF token lưu trong Session để chống tấn công Cross-Site Request Forgery.
     * Đồng bộ cả 2 định dạng đặt tên: csrf_token và csrfToken để tương thích 100% với Controller và JSP.
     */
    public static String getOrCreateCsrfToken(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        String token = (String) session.getAttribute(CSRF_TOKEN_SESSION);
        if (token == null || token.trim().isEmpty()) {
            token = (String) session.getAttribute(CSRF_TOKEN_SESSION_ALT);
        }
        if (token == null || token.trim().isEmpty()) {
            token = java.util.UUID.randomUUID().toString();
        }
        session.setAttribute(CSRF_TOKEN_SESSION, token);
        session.setAttribute(CSRF_TOKEN_SESSION_ALT, token);
        return token;
    }

    /**
     * Kiểm tra tính hợp lệ của CSRF token gửi lên từ request POST.
     * Hỗ trợ nhận token từ cả parameter csrf_token và csrfToken.
     */
    public static boolean validateCsrfToken(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }
        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_SESSION);
        if (sessionToken == null || sessionToken.trim().isEmpty()) {
            sessionToken = (String) session.getAttribute(CSRF_TOKEN_SESSION_ALT);
        }
        if (sessionToken == null || sessionToken.trim().isEmpty()) {
            return false;
        }
        String reqToken = req.getParameter("csrf_token");
        if (reqToken == null || reqToken.trim().isEmpty()) {
            reqToken = req.getParameter("csrfToken");
        }
        return sessionToken.equals(reqToken);
    }

    /**
     * Kiểm tra người dùng đã đăng nhập chưa; nếu chưa thì điều hướng về /login?authRequired=true.
     *
     * @return đối tượng {@link User} trong Session hoặc {@code null} nếu đã redirect về Login
     */
    public static User requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(Constant.SESSION_ACCOUNT) == null) {
            resp.sendRedirect(req.getContextPath() + "/login?authRequired=true");
            return null;
        }
        return (User) session.getAttribute(Constant.SESSION_ACCOUNT);
    }

    /**
     * Chỉ cho phép Admin (roleid = 1) hoặc Quản lý (roleid = 2) truy cập.
     * Nếu không đủ quyền, chuyển hướng về trang chủ kèm cờ forbidden=true.
     */
    public static User requireAdminOrManager(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User account = requireLogin(req, resp);
        if (account == null) {
            return null;
        }
        if (account.getRoleid() != Constant.ROLE_ADMIN && account.getRoleid() != Constant.ROLE_MANAGER) {
            resp.sendRedirect(req.getContextPath() + "/home?forbidden=true");
            return null;
        }
        return account;
    }

    /**
     * Chỉ cho phép Admin (roleid = 1) truy cập.
     */
    public static User requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User account = requireLogin(req, resp);
        if (account == null) {
            return null;
        }
        if (account.getRoleid() != Constant.ROLE_ADMIN) {
            resp.sendRedirect(req.getContextPath() + "/home?forbidden=true");
            return null;
        }
        return account;
    }

    public static void setFlashSuccess(HttpServletRequest req, String message) {
        HttpSession session = req.getSession(true);
        session.setAttribute(FLASH_SUCCESS, message);
        session.setAttribute(FLASH_SUCCESS_ALT, message);
    }

    public static void setFlashError(HttpServletRequest req, String message) {
        HttpSession session = req.getSession(true);
        session.setAttribute(FLASH_ERROR, message);
        session.setAttribute(FLASH_ERROR_ALT, message);
    }

    /**
     * Chuyển flash message từ Session sang Request attribute để hiển thị 1 lần sau redirect.
     */
    public static void exposeFlashMessages(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute(FLASH_SUCCESS);
        if (success == null) {
            success = session.getAttribute(FLASH_SUCCESS_ALT);
        }
        if (success != null) {
            req.setAttribute("successMsg", success.toString());
            session.removeAttribute(FLASH_SUCCESS);
            session.removeAttribute(FLASH_SUCCESS_ALT);
        }

        Object error = session.getAttribute(FLASH_ERROR);
        if (error == null) {
            error = session.getAttribute(FLASH_ERROR_ALT);
        }
        if (error != null) {
            req.setAttribute("alert", error.toString());
            session.removeAttribute(FLASH_ERROR);
            session.removeAttribute(FLASH_ERROR_ALT);
        }
    }
}
