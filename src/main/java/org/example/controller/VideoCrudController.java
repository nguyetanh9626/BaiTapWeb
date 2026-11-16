package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.example.model.User;
import org.example.model.Video;
import org.example.service.VideoService;
import org.example.service.VideoServiceImpl;
import org.example.util.Constant;
import org.example.util.SessionUtil;
import org.example.util.VideoUploadUtil;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Controller xử lý đầy đủ CRUD Video với Multipart upload và phân quyền nghiêm ngặt:
 * - /video/add       : Thêm video mới (tất cả người dùng đã đăng nhập)
 * - /video/edit      : Chỉnh sửa video (chủ sở hữu hoặc Quản trị viên)
 * - /video/delete    : Xóa video (chủ sở hữu hoặc Quản trị viên, POST với CSRF token)
 * - /video/my-videos : Danh sách video do chính người dùng đăng tải
 */
@WebServlet(urlPatterns = {"/video/add", "/video/edit", "/video/delete", "/video/my-videos"})
@MultipartConfig(
        fileSizeThreshold = 2 * 1024 * 1024,      // 2MB
        maxFileSize = 100 * 1024 * 1024,          // 100MB cho file video
        maxRequestSize = 120 * 1024 * 1024        // 120MB tổng request
)
public class VideoCrudController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final VideoService videoService = new VideoServiceImpl();

    private User getLoggedInUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(Constant.SESSION_ACCOUNT) == null) {
            resp.sendRedirect(req.getContextPath() + "/login?authRequired=true");
            return null;
        }
        return (User) session.getAttribute(Constant.SESSION_ACCOUNT);
    }

    private boolean isOwnerOrAdmin(User user, Video video) {
        if (user == null || video == null) return false;
        return user.getRoleid() == Constant.ROLE_ADMIN || user.getId() == video.getUserId();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User account = getLoggedInUser(req, resp);
        if (account == null) {
            return;
        }

        String path = req.getServletPath();

        if ("/video/my-videos".equals(path)) {
            handleMyVideos(req, resp, account);
        } else if ("/video/add".equals(path)) {
            req.setAttribute("activeMenu", "video-add");
            req.getRequestDispatcher(Constant.Path.VIDEO_ADD).forward(req, resp);
        } else if ("/video/edit".equals(path)) {
            handleEditGet(req, resp, account);
        } else if ("/video/delete".equals(path)) {
            SessionUtil.setFlashError(req, "Thao tác xóa video chỉ được thực hiện bằng phương thức POST!");
            resp.sendRedirect(req.getContextPath() + "/video/my-videos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User account = getLoggedInUser(req, resp);
        if (account == null) {
            return;
        }

        String path = req.getServletPath();

        if ("/video/add".equals(path)) {
            handleAddPost(req, resp, account);
        } else if ("/video/edit".equals(path)) {
            handleEditPost(req, resp, account);
        } else if ("/video/delete".equals(path)) {
            handleDeletePost(req, resp, account);
        }
    }

    private void handleMyVideos(HttpServletRequest req, HttpServletResponse resp, User account)
            throws ServletException, IOException {
        String pageParam = req.getParameter("page");
        int page = 1;
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Math.max(1, Integer.parseInt(pageParam.trim()));
            } catch (NumberFormatException ignored) {
                page = 1;
            }
        }

        int pageSize = 6;
        List<Video> myVideos = videoService.findByUserId(account.getId(), page, pageSize);
        int totalItems = videoService.countByUserId(account.getId());
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        req.setAttribute("videoList", myVideos);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", Math.max(1, totalPages));
        req.setAttribute("totalItems", totalItems);
        req.setAttribute("activeMenu", "my-videos");

        req.getRequestDispatcher(Constant.Path.VIDEO_MY).forward(req, resp);
    }

    private void handleAddPost(HttpServletRequest req, HttpServletResponse resp, User account)
            throws ServletException, IOException {
        String title = req.getParameter("title");
        String description = req.getParameter("description");

        if (title == null || title.trim().isEmpty()) {
            req.setAttribute("alert", "Tiêu đề video không được để trống!");
            req.setAttribute("valTitle", title);
            req.setAttribute("valDescription", description);
            req.getRequestDispatcher(Constant.Path.VIDEO_ADD).forward(req, resp);
            return;
        }

        Part videoPart = req.getPart("videoFile");
        Part thumbPart = req.getPart("thumbnailFile");

        if (videoPart == null || videoPart.getSize() == 0) {
            req.setAttribute("alert", "Vui lòng chọn file video để tải lên!");
            req.setAttribute("valTitle", title);
            req.setAttribute("valDescription", description);
            req.getRequestDispatcher(Constant.Path.VIDEO_ADD).forward(req, resp);
            return;
        }

        String savedVideoName;
        String savedThumbName = null;

        try {
            savedVideoName = VideoUploadUtil.saveVideo(videoPart);
            if (thumbPart != null && thumbPart.getSize() > 0) {
                savedThumbName = VideoUploadUtil.saveThumbnail(thumbPart);
            }
        } catch (IllegalArgumentException | SecurityException e) {
            req.setAttribute("alert", e.getMessage());
            req.setAttribute("valTitle", title);
            req.setAttribute("valDescription", description);
            req.getRequestDispatcher(Constant.Path.VIDEO_ADD).forward(req, resp);
            return;
        }

        Video newVideo = new Video();
        newVideo.setTitle(title.trim());
        newVideo.setDescription(description != null ? description.trim() : "");
        newVideo.setVideoPath(savedVideoName);
        newVideo.setThumbnail(savedThumbName != null ? savedThumbName : "default-thumb.jpg");
        newVideo.setUploadDate(new Timestamp(System.currentTimeMillis()));
        newVideo.setViews(0);
        newVideo.setStatus(1);
        newVideo.setUserId(account.getId()); // Gán trực tiếp từ session, chống giả mạo

        videoService.insert(newVideo);

        SessionUtil.setFlashSuccess(req, "Tải lên video \"" + newVideo.getTitle() + "\" thành công!");
        resp.sendRedirect(req.getContextPath() + "/video/detail?id=" + newVideo.getVideoId());
    }

    private void handleEditGet(HttpServletRequest req, HttpServletResponse resp, User account)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        int videoId;
        try {
            videoId = Integer.parseInt(idParam != null ? idParam.trim() : "");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/video/my-videos");
            return;
        }

        Video video = videoService.findById(videoId);
        if (video == null) {
            SessionUtil.setFlashError(req, "Không tìm thấy video ID #" + videoId);
            resp.sendRedirect(req.getContextPath() + "/video/my-videos");
            return;
        }

        if (!isOwnerOrAdmin(account, video)) {
            SessionUtil.setFlashError(req, "Bạn không có quyền chỉnh sửa video này!");
            resp.sendRedirect(req.getContextPath() + "/video/my-videos");
            return;
        }

        req.setAttribute("video", video);
        req.setAttribute("activeMenu", "my-videos");
        req.getRequestDispatcher(Constant.Path.VIDEO_EDIT).forward(req, resp);
    }

    private void handleEditPost(HttpServletRequest req, HttpServletResponse resp, User account)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        int videoId;
        try {
            videoId = Integer.parseInt(idParam != null ? idParam.trim() : "");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/video/my-videos");
            return;
        }

        Video video = videoService.findById(videoId);
        if (video == null) {
            SessionUtil.setFlashError(req, "Không tìm thấy video cần chỉnh sửa!");
            resp.sendRedirect(req.getContextPath() + "/video/my-videos");
            return;
        }

        if (!isOwnerOrAdmin(account, video)) {
            SessionUtil.setFlashError(req, "Bạn không có quyền chỉnh sửa video này!");
            resp.sendRedirect(req.getContextPath() + "/video/my-videos");
            return;
        }

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String statusParam = req.getParameter("status");

        if (title == null || title.trim().isEmpty()) {
            req.setAttribute("alert", "Tiêu đề video không được để trống!");
            req.setAttribute("video", video);
            req.getRequestDispatcher(Constant.Path.VIDEO_EDIT).forward(req, resp);
            return;
        }

        video.setTitle(title.trim());
        video.setDescription(description != null ? description.trim() : "");
        if (statusParam != null) {
            try {
                video.setStatus(Integer.parseInt(statusParam));
            } catch (NumberFormatException ignored) {
            }
        }

        Part videoPart = req.getPart("videoFile");
        Part thumbPart = req.getPart("thumbnailFile");

        String oldVideo = video.getVideoPath();
        String oldThumb = video.getThumbnail();

        try {
            if (videoPart != null && videoPart.getSize() > 0) {
                String newVideoPath = VideoUploadUtil.saveVideo(videoPart);
                video.setVideoPath(newVideoPath);
                VideoUploadUtil.deleteVideoFileIfExists(oldVideo);
            }
            if (thumbPart != null && thumbPart.getSize() > 0) {
                String newThumbPath = VideoUploadUtil.saveThumbnail(thumbPart);
                video.setThumbnail(newThumbPath);
                VideoUploadUtil.deleteThumbnailFileIfExists(oldThumb);
            }
        } catch (IllegalArgumentException | SecurityException e) {
            req.setAttribute("alert", e.getMessage());
            req.setAttribute("video", video);
            req.getRequestDispatcher(Constant.Path.VIDEO_EDIT).forward(req, resp);
            return;
        }

        videoService.update(video);

        SessionUtil.setFlashSuccess(req, "Cập nhật video thành công!");
        resp.sendRedirect(req.getContextPath() + "/video/detail?id=" + video.getVideoId());
    }

    private void handleDeletePost(HttpServletRequest req, HttpServletResponse resp, User account)
            throws ServletException, IOException {
        if (!SessionUtil.validateCsrfToken(req)) {
            SessionUtil.setFlashError(req, "Mã xác thực bảo mật (CSRF) không hợp lệ!");
            resp.sendRedirect(req.getContextPath() + "/video/my-videos");
            return;
        }

        String idParam = req.getParameter("id");
        int videoId;
        try {
            videoId = Integer.parseInt(idParam != null ? idParam.trim() : "");
        } catch (NumberFormatException e) {
            SessionUtil.setFlashError(req, "Mã video không hợp lệ!");
            resp.sendRedirect(req.getContextPath() + "/video/my-videos");
            return;
        }

        Video video = videoService.findById(videoId);
        if (video == null) {
            SessionUtil.setFlashError(req, "Video không tồn tại hoặc đã được xóa!");
            resp.sendRedirect(req.getContextPath() + "/video/my-videos");
            return;
        }

        if (!isOwnerOrAdmin(account, video)) {
            SessionUtil.setFlashError(req, "Bạn không có quyền xóa video này!");
            resp.sendRedirect(req.getContextPath() + "/video/my-videos");
            return;
        }

        videoService.delete(videoId);
        VideoUploadUtil.deleteVideoFileIfExists(video.getVideoPath());
        VideoUploadUtil.deleteThumbnailFileIfExists(video.getThumbnail());

        SessionUtil.setFlashSuccess(req, "Đã xóa video \"" + video.getTitle() + "\" thành công!");
        resp.sendRedirect(req.getContextPath() + "/video/my-videos");
    }
}
