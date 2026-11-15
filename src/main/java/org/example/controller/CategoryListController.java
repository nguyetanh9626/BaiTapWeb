package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.DataAccessException;
import org.example.model.Category;
import org.example.model.User;
import org.example.service.CategoryService;
import org.example.service.CategoryServiceImpl;
import org.example.util.Constant;
import org.example.util.SessionUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Controller hiển thị danh sách Category cũ bằng JDBC (đã chuyển giao sang CategoryController JPA).
 * Giữ nguyên code cũ để đối chiếu, đổi URL để tránh xung đột với CategoryController JPA.
 */
@WebServlet(urlPatterns = "/legacy/category/list")
public class CategoryListController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User account = SessionUtil.requireLogin(req, resp);
        if (account == null) {
            return;
        }

        SessionUtil.getOrCreateCsrfToken(req);
        SessionUtil.exposeFlashMessages(req);
        req.setAttribute("activeMenu", "jdbc-list");

        String keyword = req.getParameter("keyword");
        String trimmedKeyword = keyword == null ? "" : keyword.trim();

        try {
            List<Category> cateList = trimmedKeyword.isEmpty()
                    ? cateService.getAll()
                    : cateService.search(trimmedKeyword);
            req.setAttribute("cateList", cateList);
            req.setAttribute("keyword", trimmedKeyword);
        } catch (DataAccessException e) {
            e.printStackTrace();
            req.setAttribute("cateList", Collections.emptyList());
            req.setAttribute("alert", "Không thể tải danh sách danh mục: " + e.getMessage());
        }

        req.getRequestDispatcher(Constant.Path.CATEGORY_LIST).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
