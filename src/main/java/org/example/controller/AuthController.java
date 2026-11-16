package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.util.Constant;

import java.io.IOException;

/**
 * Controller trang chủ (/home) và trang thông báo lỗi đăng nhập (/error) từ Slide 1.
 */
@WebServlet(urlPatterns = {"/home", "/error"})
public class AuthController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getServletPath();
        if ("/error".equals(path)) {
            req.getRequestDispatcher(Constant.Path.ERROR).forward(req, resp);
            return;
        }

        if ("true".equals(req.getParameter("forbidden"))) {
            req.setAttribute("alert", "Tài khoản của bạn không có quyền truy cập khu vực quản trị này.");
        } else if ("true".equals(req.getParameter("logout"))) {
            req.setAttribute("successMsg", "Bạn đã đăng xuất khỏi hệ thống.");
        }

        // Lấy 10 video mới nhất để hiển thị trên trang chủ
        try {
            org.example.service.VideoService videoService = new org.example.service.VideoServiceImpl();
            req.setAttribute("top10Videos", videoService.findTop10Newest());
        } catch (Exception e) {
            e.printStackTrace();
        }

        req.setAttribute("activeMenu", "home");
        req.getRequestDispatcher(Constant.Path.INDEX).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
