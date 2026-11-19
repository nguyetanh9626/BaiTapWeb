package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.model.User;
import org.example.model.Video;
import org.example.service.VideoService;
import org.example.service.VideoServiceImpl;
import org.example.util.Constant;
import org.example.util.SessionUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Controller phát video HTML5 và xem chi tiết video (/video/detail, /video/watch).
 */
@WebServlet(urlPatterns = {"/video/detail", "/video/watch"})
public class VideoDetailController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final VideoService videoService = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/video");
            return;
        }

        int videoId;
        try {
            videoId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/video");
            return;
        }

        Video video = videoService.findById(videoId);
        if (video == null) {
            resp.sendRedirect(req.getContextPath() + "/video?notFound=true");
            return;
        }

        // Kiểm tra quyền xem nếu video bị ẩn/tạm khóa (status != 1)
        if (video.getStatus() != 1) {
            HttpSession session = req.getSession(false);
            User currentUser = session != null ? (User) session.getAttribute(Constant.SESSION_ACCOUNT) : null;
            boolean canView = currentUser != null && (currentUser.getRoleid() == Constant.ROLE_ADMIN || currentUser.getId() == video.getUserId());
            if (!canView) {
                SessionUtil.setFlashError(req, "Video này đã bị ẩn hoặc tạm khóa. Bạn không có quyền xem.");
                resp.sendRedirect(req.getContextPath() + "/video");
                return;
            }
        }

        // Chuẩn bị CSRF token cho form xóa video
        SessionUtil.getOrCreateCsrfToken(req);

        // Tăng lượt xem an toàn
        videoService.incrementViews(videoId);
        video.setViews(video.getViews() + 1);

        // Lấy danh sách video gợi ý
        List<Video> relatedVideos = videoService.findTop10Newest();
        relatedVideos.removeIf(v -> v.getVideoId() == videoId);
        if (relatedVideos.size() > 5) {
            relatedVideos = relatedVideos.subList(0, 5);
        }

        req.setAttribute("video", video);
        req.setAttribute("videoMime", resolveMimeType(video.getVideoPath()));
        req.setAttribute("relatedVideos", relatedVideos);
        req.setAttribute("activeMenu", "video");

        req.getRequestDispatcher(Constant.Path.VIDEO_DETAIL).forward(req, resp);
    }

    private String resolveMimeType(String filename) {
        if (filename == null) return "video/mp4";
        String lower = filename.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".webm")) return "video/webm";
        if (lower.endsWith(".mkv")) return "video/x-matroska";
        if (lower.endsWith(".mov")) return "video/quicktime";
        if (lower.endsWith(".avi")) return "video/x-msvideo";
        return "video/mp4";
    }
}
