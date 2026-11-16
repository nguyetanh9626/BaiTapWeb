package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.example.model.User;
import org.example.util.Constant;
import org.example.util.ImageUploadUtil;
import org.example.util.SessionUtil;
import vn.iotstar.entity.Category;
import vn.iotstar.service.CategoryServiceImpl;
import vn.iotstar.service.ICategoryService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Controller xử lý tất cả các thao tác CRUD Category bằng JPA/Hibernate theo yêu cầu Phần 8.
 * Tích hợp kiểm tra quyền Quản trị (Admin/Manager), bảo vệ dữ liệu, chống lỗi 500 khi ID sai định dạng.
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1MB
        maxFileSize = 1024 * 1024 * 10,        // 10MB
        maxRequestSize = 1024 * 1024 * 50      // 50MB
)
@WebServlet(urlPatterns = {
        "/admin/categories",
        "/admin/category/list",
        "/admin/category/add",
        "/admin/category/insert",
        "/admin/category/edit",
        "/admin/category/update",
        "/admin/category/delete"
})
public class CategoryController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    public ICategoryService cateService = new CategoryServiceImpl();

    /**
     * Kiểm tra người dùng đã đăng nhập chưa.
     * Cho phép tất cả các tài khoản đã đăng nhập (kể cả Thành viên / User) xem danh mục ở chế độ chỉ đọc.
     */
    private boolean checkLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(Constant.SESSION_ACCOUNT) == null) {
            resp.sendRedirect(req.getContextPath() + "/login?authRequired=true");
            return false;
        }
        return true;
    }

    /**
     * Kiểm tra quyền quản trị: Chỉ Admin (roleid = 1) hoặc Manager (roleid = 2) mới được phép thêm, sửa hoặc xóa.
     * Kiểm tra chặt chẽ cả GET và POST, không sử dụng bất kỳ cơ chế bypass nào.
     */
    private boolean checkAdminPermission(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!checkLogin(req, resp)) {
            return false;
        }

        HttpSession session = req.getSession(false);
        User account = (User) session.getAttribute(Constant.SESSION_ACCOUNT);
        if (account == null || (account.getRoleid() != Constant.ROLE_ADMIN && account.getRoleid() != Constant.ROLE_MANAGER)) {
            resp.sendRedirect(req.getContextPath() + "/home?forbidden=true");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String url = req.getRequestURI();

        if (url.contains("/admin/categories") || url.contains("/admin/category/list")) {
            // Cho phép người dùng đã đăng nhập (kể cả User) xem danh mục ở chế độ chỉ đọc
            if (!checkLogin(req, resp)) {
                return;
            }

            // Đảm bảo khởi tạo CSRF token cho các form POST
            SessionUtil.getOrCreateCsrfToken(req);

            // Hiển thị danh sách hoặc tìm kiếm theo tên
            String keyword = req.getParameter("keyword");
            List<Category> list;
            try {
                if (keyword != null && !keyword.trim().isEmpty()) {
                    list = cateService.searchByName(keyword.trim());
                    req.setAttribute("keyword", keyword.trim());
                } else {
                    list = cateService.findAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
                list = Collections.emptyList();
                req.setAttribute("error", "Lỗi tải dữ liệu danh mục từ cơ sở dữ liệu: " + e.getMessage());
            }
            req.setAttribute("activeMenu", "jpa-list");
            req.setAttribute("listcate", list);
            req.getRequestDispatcher("/views/admin/category-list.jsp").forward(req, resp);

        } else if (url.contains("/admin/category/add")) {
            // Chỉ Admin hoặc Manager mới được mở trang thêm danh mục
            if (!checkAdminPermission(req, resp)) {
                return;
            }
            SessionUtil.getOrCreateCsrfToken(req);
            req.setAttribute("activeMenu", "jpa-add");
            req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);

        } else if (url.contains("/admin/category/edit")) {
            // Chỉ Admin hoặc Manager mới được mở trang cập nhật danh mục
            if (!checkAdminPermission(req, resp)) {
                return;
            }
            SessionUtil.getOrCreateCsrfToken(req);
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                req.getSession().setAttribute("message_error", "Thiếu mã danh mục (ID) cần chỉnh sửa!");
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idParam.trim());
                if (id <= 0) {
                    req.getSession().setAttribute("message_error", "Mã danh mục không hợp lệ (phải là số nguyên dương)!");
                    resp.sendRedirect(req.getContextPath() + "/admin/categories");
                    return;
                }
            } catch (NumberFormatException e) {
                // Xử lý an toàn: Không ném lỗi 500 khi người dùng nhập ID sai định dạng
                req.getSession().setAttribute("message_error", "Mã danh mục '" + idParam + "' không đúng định dạng số!");
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }

            try {
                Category category = cateService.findById(id);
                if (category == null) {
                    req.getSession().setAttribute("message_error", "Không tìm thấy danh mục có ID: " + id);
                    resp.sendRedirect(req.getContextPath() + "/admin/categories");
                    return;
                }
                req.setAttribute("activeMenu", "jpa-list");
                req.setAttribute("cate", category);
                req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
                req.getSession().setAttribute("message_error", "Lỗi khi truy vấn danh mục: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
            }

        } else if (url.contains("/admin/category/delete")) {
            // Theo yêu cầu Section III: GET không được thực hiện xóa dữ liệu
            req.getSession().setAttribute("message_error", "Thao tác xóa danh mục chỉ được thực hiện bằng phương thức POST trên trang danh sách!");
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // 1. Kiểm tra quyền Admin/Manager
        if (!checkAdminPermission(req, resp)) {
            return;
        }

        String url = req.getRequestURI();

        if (url.contains("/admin/category/delete")) {
            // Xử lý xóa danh mục qua POST và xác thực CSRF Token
            if (!SessionUtil.validateCsrfToken(req)) {
                req.getSession().setAttribute("message_error", "Phiên làm việc hoặc mã xác thực bảo mật (CSRF) không hợp lệ!");
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }

            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                idParam = req.getParameter("categoryid");
            }

            int id;
            try {
                id = Integer.parseInt(idParam == null ? "" : idParam.trim());
                if (id <= 0) {
                    req.getSession().setAttribute("message_error", "Mã danh mục không hợp lệ!");
                    resp.sendRedirect(req.getContextPath() + "/admin/categories");
                    return;
                }
            } catch (NumberFormatException e) {
                req.getSession().setAttribute("message_error", "Mã danh mục cần xóa không đúng định dạng số!");
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }

            try {
                Category category = cateService.findById(id);
                if (category == null) {
                    req.getSession().setAttribute("message_error", "Danh mục ID #" + id + " không tồn tại hoặc đã được xóa!");
                } else if (category.getVideos() != null && !category.getVideos().isEmpty()) {
                    req.getSession().setAttribute("message_error", "Không thể xóa danh mục \"" + category.getCategoryname() + "\" (ID #" + id + ") vì đang có " + category.getVideos().size() + " video thuộc danh mục này. Vui lòng chuyển hoặc xóa các video liên quan trước!");
                } else {
                    cateService.delete(id);
                    if (category.getImages() != null) {
                        ImageUploadUtil.deleteCategoryImageIfExists(category.getImages());
                    }
                    req.getSession().setAttribute("message_success", "Xóa danh mục thành công!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                String errorMsg = e.getMessage();
                if (errorMsg != null && (errorMsg.contains("REFERENCE constraint") || errorMsg.contains("conflict") || errorMsg.contains("foreign key") || errorMsg.contains("FK"))) {
                    req.getSession().setAttribute("message_error", "Không thể xóa danh mục ID #" + id + " do có ràng buộc dữ liệu: Đang có video thuộc danh mục này trong cơ sở dữ liệu!");
                } else {
                    req.getSession().setAttribute("message_error", "Không thể xóa danh mục ID #" + id + ": " + (errorMsg != null ? errorMsg : "Lỗi hệ thống."));
                }
            }
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;

        } else if (url.contains("/admin/category/insert")) {
            // Lấy dữ liệu từ form thêm mới
            String categoryname = req.getParameter("categoryname");
            String statusParam = req.getParameter("status");
            int status = 1; // Mặc định Hoạt động (1)
            if (statusParam != null && !statusParam.trim().isEmpty()) {
                try {
                    status = Integer.parseInt(statusParam.trim());
                } catch (NumberFormatException e) {
                    status = 1;
                }
            }
            String images = req.getParameter("images");

            // Kiểm tra tên rỗng
            if (categoryname == null || categoryname.trim().isEmpty()) {
                req.setAttribute("error", "Tên danh mục không được phép rỗng!");
                req.setAttribute("activeMenu", "jpa-add");
                req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
                return;
            }

            String finalImageName = "default-category.png";
            String savedUploadedFile = null;
            try {
                String contentType = req.getContentType();
                boolean isMultipart = contentType != null && contentType.toLowerCase().startsWith("multipart/");
                Part part = null;
                if (isMultipart) {
                    try {
                        part = req.getPart("images1");
                    } catch (Exception ignored) {
                    }
                }
                if (part != null && part.getSize() > 0) {
                    savedUploadedFile = ImageUploadUtil.saveCategoryImage(part);
                    finalImageName = savedUploadedFile;
                } else if (images != null && !images.trim().isEmpty()) {
                    String trimmedUrl = images.trim();
                    if (!ImageUploadUtil.isValidImageUrl(trimmedUrl)) {
                        req.setAttribute("error", "Đường dẫn hình ảnh bên ngoài không hợp lệ! Vui lòng nhập URL dạng http:// hoặc https://");
                        req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
                        return;
                    }
                    finalImageName = trimmedUrl;
                }
            } catch (IllegalArgumentException e) {
                req.setAttribute("error", e.getMessage());
                req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
                return;
            }

            Category category = new Category();
            category.setCategoryname(categoryname.trim());
            category.setStatus(status);
            category.setImages(finalImageName);

            try {
                cateService.insert(category);
                req.getSession().setAttribute("message_success", "Thêm danh mục mới thành công!");
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
            } catch (Exception e) {
                // Nếu insert thất bại, dọn ảnh mới đã upload
                ImageUploadUtil.deleteCategoryImageIfExists(savedUploadedFile);
                e.printStackTrace();
                req.setAttribute("error", e.getMessage());
                req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
            }

        } else if (url.contains("/admin/category/update")) {
            // Lấy dữ liệu từ form cập nhật
            String categoryidParam = req.getParameter("categoryid");
            if (categoryidParam == null || categoryidParam.trim().isEmpty()) {
                req.getSession().setAttribute("message_error", "Thiếu mã danh mục để cập nhật!");
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }

            int categoryid;
            try {
                categoryid = Integer.parseInt(categoryidParam.trim());
                if (categoryid <= 0) {
                    req.getSession().setAttribute("message_error", "Mã danh mục không hợp lệ!");
                    resp.sendRedirect(req.getContextPath() + "/admin/categories");
                    return;
                }
            } catch (NumberFormatException e) {
                req.getSession().setAttribute("message_error", "Mã danh mục không đúng định dạng!");
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }

            String categoryname = req.getParameter("categoryname");
            String statusParam = req.getParameter("status");
            int status = 1;
            if (statusParam != null && !statusParam.trim().isEmpty()) {
                try {
                    status = Integer.parseInt(statusParam.trim());
                } catch (NumberFormatException e) {
                    status = 1;
                }
            }
            String images = req.getParameter("images");

            // Kiểm tra danh mục tồn tại
            Category category;
            try {
                category = cateService.findById(categoryid);
            } catch (Exception e) {
                req.getSession().setAttribute("message_error", "Lỗi truy vấn danh mục: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }

            if (category == null) {
                req.getSession().setAttribute("message_error", "Không tìm thấy danh mục ID: " + categoryid + " để cập nhật!");
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }

            // Kiểm tra tên rỗng
            if (categoryname == null || categoryname.trim().isEmpty()) {
                req.setAttribute("error", "Tên danh mục không được phép rỗng!");
                req.setAttribute("activeMenu", "jpa-list");
                req.setAttribute("cate", category);
                req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
                return;
            }

            String fileold = category.getImages();
            String newUploadedFile = null;
            String finalImages = fileold;

            try {
                String contentType = req.getContentType();
                boolean isMultipart = contentType != null && contentType.toLowerCase().startsWith("multipart/");
                Part part = null;
                if (isMultipart) {
                    try {
                        part = req.getPart("images1");
                    } catch (Exception ignored) {
                    }
                }
                if (part != null && part.getSize() > 0) {
                    newUploadedFile = ImageUploadUtil.saveCategoryImage(part);
                    finalImages = newUploadedFile;
                } else if (images != null && !images.trim().isEmpty()) {
                    String trimmedUrl = images.trim();
                    if (!ImageUploadUtil.isValidImageUrl(trimmedUrl)) {
                        req.setAttribute("error", "Đường dẫn hình ảnh bên ngoài không hợp lệ! Vui lòng nhập URL dạng http:// hoặc https://");
                        req.setAttribute("cate", category);
                        req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
                        return;
                    }
                    finalImages = trimmedUrl;
                }
            } catch (IllegalArgumentException e) {
                req.setAttribute("error", e.getMessage());
                req.setAttribute("activeMenu", "jpa-list");
                req.setAttribute("cate", category);
                req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
                return;
            }

            category.setCategoryname(categoryname.trim());
            category.setStatus(status);
            category.setImages(finalImages);

            try {
                cateService.update(category);
                // Chỉ xóa ảnh cũ sau khi cập nhật database thành công nếu người dùng upload ảnh mới
                if (newUploadedFile != null && fileold != null) {
                    ImageUploadUtil.deleteCategoryImageIfExists(fileold);
                }
                req.getSession().setAttribute("message_success", "Cập nhật danh mục thành công!");
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
            } catch (Exception e) {
                // Nếu update thất bại, dọn ảnh mới đã upload
                ImageUploadUtil.deleteCategoryImageIfExists(newUploadedFile);
                e.printStackTrace();
                req.setAttribute("error", e.getMessage());
                req.setAttribute("activeMenu", "jpa-list");
                req.setAttribute("cate", category);
                req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
            }
        }
    }
}
