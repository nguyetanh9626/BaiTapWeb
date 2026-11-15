package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.example.dao.DataAccessException;
import org.example.model.Category;
import org.example.model.User;
import org.example.service.CategoryService;
import org.example.service.CategoryServiceImpl;
import org.example.util.Constant;
import org.example.util.ImageUploadUtil;
import org.example.util.SessionUtil;

import java.io.IOException;

/**
 * Controller thêm mới Category cũ bằng JDBC (đã chuyển giao sang CategoryController JPA).
 * Giữ nguyên code cũ để đối chiếu, đổi URL để tránh xung đột với CategoryController JPA.
 */
@WebServlet(urlPatterns = "/legacy/category/add")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 25
)
public class CategoryAddController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User account = SessionUtil.requireAdminOrManager(req, resp);
        if (account == null) {
            return;
        }

        SessionUtil.getOrCreateCsrfToken(req);
        req.setAttribute("activeMenu", "jdbc-add");
        req.getRequestDispatcher(Constant.Path.CATEGORY_ADD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User account = SessionUtil.requireAdminOrManager(req, resp);
        if (account == null) {
            return;
        }

        String rawName = req.getParameter("catename");
        String catename = rawName == null ? "" : rawName.trim();
        req.setAttribute("valCatename", catename);

        if (catename.isEmpty()) {
            req.setAttribute("alert", "Vui lòng nhập tên danh mục!");
            req.getRequestDispatcher(Constant.Path.CATEGORY_ADD).forward(req, resp);
            return;
        }

        String uploadedFileName = null;
        try {
            String contentType = req.getContentType();
            boolean isMultipart = contentType != null && contentType.toLowerCase().startsWith("multipart/");
            Part part = null;
            if (isMultipart) {
                try {
                    part = req.getPart("icon");
                    if (part == null) {
                        part = req.getPart("images");
                    }
                } catch (Exception ignored) {
                }
            }
            uploadedFileName = ImageUploadUtil.saveCategoryImage(part);

            Category category = new Category(catename, uploadedFileName);
            cateService.insert(category);

            SessionUtil.setFlashSuccess(req, "Thêm danh mục \"" + catename + "\" thành công!");
            resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
        } catch (IllegalArgumentException e) {
            ImageUploadUtil.deleteCategoryImageIfExists(uploadedFileName);
            req.setAttribute("alert", e.getMessage());
            req.getRequestDispatcher(Constant.Path.CATEGORY_ADD).forward(req, resp);
        } catch (DataAccessException e) {
            ImageUploadUtil.deleteCategoryImageIfExists(uploadedFileName);
            e.printStackTrace();
            req.setAttribute("alert", "Lỗi cơ sở dữ liệu khi thêm danh mục: " + e.getMessage());
            req.getRequestDispatcher(Constant.Path.CATEGORY_ADD).forward(req, resp);
        }
    }
}
