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
import org.example.util.ImageUploadUtil;
import org.example.util.SessionUtil;

import java.io.IOException;

/**
 * Controller xóa Category cũ bằng JDBC (đã chuyển giao sang CategoryController JPA).
 * Giữ nguyên code cũ để đối chiếu, đổi URL để tránh xung đột với CategoryController JPA.
 */
@WebServlet(urlPatterns = "/legacy/category/delete")
public class CategoryDeleteController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User account = SessionUtil.requireAdminOrManager(req, resp);
        if (account == null) {
            return;
        }
        SessionUtil.setFlashError(req, "Thao tác xóa danh mục chỉ được thực hiện bằng phương thức POST trên trang danh sách.");
        resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
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

        if (!SessionUtil.validateCsrfToken(req)) {
            SessionUtil.setFlashError(req, "Phiên làm việc hoặc mã xác thực bảo mật (CSRF) không hợp lệ!");
            resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
            return;
        }

        String rawId = req.getParameter("id");
        if (rawId == null || rawId.trim().isEmpty()) {
            rawId = req.getParameter("cateid");
        }

        int id;
        try {
            id = Integer.parseInt(rawId == null ? "" : rawId.trim());
            if (id <= 0) {
                SessionUtil.setFlashError(req, "Mã danh mục cần xóa phải là số nguyên dương.");
                resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
                return;
            }
        } catch (NumberFormatException e) {
            SessionUtil.setFlashError(req, "Mã danh mục cần xóa không hợp lệ.");
            resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
            return;
        }

        try {
            Category existing = cateService.get(id);
            if (existing == null) {
                SessionUtil.setFlashError(req, "Danh mục #" + id + " không tồn tại hoặc đã được xóa.");
            } else {
                cateService.delete(id);
                // Dọn dẹp ảnh upload cục bộ nếu có
                if (existing.getIcon() != null) {
                    ImageUploadUtil.deleteCategoryImageIfExists(existing.getIcon());
                }
                SessionUtil.setFlashSuccess(req, "Đã xóa danh mục \"" + existing.getCatename() + "\" thành công!");
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            SessionUtil.setFlashError(req, "Không thể xóa danh mục: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/legacy/category/list");
    }
}
