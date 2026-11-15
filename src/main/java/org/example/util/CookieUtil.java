package org.example.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Tiện ích đọc, lưu và xóa Cookie chuẩn hóa theo context path.
 */
public final class CookieUtil {

    private CookieUtil() {
    }

    /**
     * Lấy giá trị Cookie theo tên từ request.
     *
     * @param req  HTTP request
     * @param name tên cookie
     * @return giá trị cookie nếu tồn tại và không rỗng, ngược lại trả về null
     */
    public static String getCookieValue(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null || name == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                String value = cookie.getValue();
                if (value != null && !value.trim().isEmpty()) {
                    return value.trim();
                }
            }
        }
        return null;
    }

    /**
     * Tạo hoặc cập nhật Cookie với thời hạn xác định.
     */
    public static void addCookie(HttpServletRequest req, HttpServletResponse resp,
                                 String name, String value, int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setPath(resolveCookiePath(req));
        cookie.setHttpOnly(true);
        resp.addCookie(cookie);
    }

    /**
     * Xóa Cookie khỏi trình duyệt bằng cách đặt maxAge = 0.
     */
    public static void deleteCookie(HttpServletRequest req, HttpServletResponse resp, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath(resolveCookiePath(req));
        cookie.setHttpOnly(true);
        resp.addCookie(cookie);
    }

    private static String resolveCookiePath(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        return (contextPath == null || contextPath.isEmpty()) ? "/" : contextPath;
    }
}
