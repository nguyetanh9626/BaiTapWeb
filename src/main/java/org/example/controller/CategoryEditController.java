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
 * Controller chỉnh sửa Category cũ bằng JDBC (đã chuyển giao sang CategoryController JPA).
 * Giữ nguyên code cũ để đối chiếu, đổi URL để tránh xung đột với CategoryController JPA.
 */
@WebServlet(urlPatterns = "/legacy/category/edit")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 25
)
public class CategoryEditController extends HttpServlet {
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

        Integer id = parsePositiveInt(req.getParameter("id"));
        if (id == null) {
            SessionUtil.setFlashError(req, "Mã danh mục không hợp lệ.");
            resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
            return;
        }

        try {
            Category category = cateService.get(id);
            if (category == null) {
                SessionUtil.setFlashError(req, "Không tìm thấy danh mục có mã #" + id + ".");
                resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
                return;
            }
            req.setAttribute("category", category);
            req.setAttribute("activeMenu", "jdbc-list");
            req.getRequestDispatcher(Constant.Path.CATEGORY_EDIT).forward(req, resp);
        } catch (DataAccessException e) {
            e.printStackTrace();
            SessionUtil.setFlashError(req, "Lỗi truy vấn danh mục: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
        }
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

        String idRaw = req.getParameter("cateid");
        if (idRaw == null || idRaw.trim().isEmpty()) {
            idRaw = req.getParameter("id");
        }
        Integer cateid = parsePositiveInt(idRaw);
        if (cateid == null) {
            SessionUtil.setFlashError(req, "Mã danh mục không hợp lệ.");
            resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
            return;
        }

        Category oldCate;
        try {
            oldCate = cateService.get(cateid);
        } catch (DataAccessException e) {
            e.printStackTrace();
            SessionUtil.setFlashError(req, "Lỗi cơ sở dữ liệu: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
            return;
        }

        if (oldCate == null) {
            SessionUtil.setFlashError(req, "Danh mục không tồn tại hoặc đã bị xóa.");
            resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
            return;
        }

        String rawName = req.getParameter("catename");
        String catename = rawName == null ? "" : rawName.trim();
        if (catename.isEmpty()) {
            req.setAttribute("category", oldCate);
            req.setAttribute("alert", "Tên danh mục không được để trống!");
            req.getRequestDispatcher(Constant.Path.CATEGORY_EDIT).forward(req, resp);
            return;
        }

        String newFileName = null;
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
            if (part != null && part.getSize() > 0) {
                newFileName = ImageUploadUtil.saveCategoryImage(part);
            }

            Category updated = new Category();
            updated.setCateid(cateid);
            updated.setCatename(catename);
            updated.setIcon(newFileName != null ? newFileName : oldCate.getIcon());

            cateService.edit(updated);

            // Xóa ảnh cũ sau khi cập nhật database thành công nếu người dùng upload ảnh mới
            if (newFileName != null && oldCate.getIcon() != null) {
                ImageUploadUtil.deleteCategoryImageIfExists(oldCate.getIcon());
            }

            SessionUtil.setFlashSuccess(req, "Cập nhật danh mục #" + cateid + " thành công!");
            resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
        } catch (IllegalArgumentException e) {
            ImageUploadUtil.deleteCategoryImageIfExists(newFileName);
            oldCate.setCatename(catename);
            req.setAttribute("category", oldCate);
            req.setAttribute("alert", e.getMessage());
            req.getRequestDispatcher(Constant.Path.CATEGORY_EDIT).forward(req, resp);
        } catch (DataAccessException e) {
            ImageUploadUtil.deleteCategoryImageIfExists(newFileName);
            e.printStackTrace();
            oldCate.setCatename(catename);
            req.setAttribute("category", oldCate);
            req.setAttribute("alert", "Lỗi cập nhật SQL Server: " + e.getMessage());
            req.getRequestDispatcher(Constant.Path.CATEGORY_EDIT).forward(req, resp);
        }
    }

    private Integer parsePositiveInt(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            int val = Integer.parseInt(raw.trim());
            return val > 0 ? val : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
