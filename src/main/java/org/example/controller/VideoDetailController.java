package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Video;
import org.example.service.VideoService;
import org.example.service.VideoServiceImpl;
import org.example.util.Constant;

import java.io.IOException;
import java.util.List;

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
        req.setAttribute("relatedVideos", relatedVideos);
        req.setAttribute("activeMenu", "video");

        req.getRequestDispatcher(Constant.Path.VIDEO_DETAIL).forward(req, resp);
    }
}
