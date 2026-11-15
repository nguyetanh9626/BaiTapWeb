package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.model.User;
import org.example.util.Constant;

import java.io.IOException;

/**
 * Controller điều hướng theo vai trò (roleid) sau khi có Session "account" theo tài liệu 06_MVC_3tier.pdf.
 */
@WebServlet(urlPatterns = "/waiting")
public class WaitingController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            User user = (User) session.getAttribute(Constant.SESSION_ACCOUNT);
            req.setAttribute("username", user.getUserName());
            if (user.getRoleid() == Constant.ROLE_ADMIN) {
                resp.sendRedirect(req.getContextPath() + "/admin/home");
            } else if (user.getRoleid() == Constant.ROLE_MANAGER) {
                resp.sendRedirect(req.getContextPath() + "/manager/home");
            } else {
                resp.sendRedirect(req.getContextPath() + "/home");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/login?authRequired=true");
        }
    }
}
