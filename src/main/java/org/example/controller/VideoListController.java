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
 * Controller hiển thị danh sách Video phân trang đúng 6 video/trang (/video).
 */
@WebServlet(urlPatterns = "/video")
public class VideoListController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 6;

    private final VideoService videoService = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String pageParam = req.getParameter("page");
        String keyword = req.getParameter("keyword");

        int currentPage = 1;
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                currentPage = Math.max(1, Integer.parseInt(pageParam.trim()));
            } catch (NumberFormatException ignored) {
                currentPage = 1;
            }
        }

        List<Video> videoList;
        int totalItems;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String trimmedKw = keyword.trim();
            videoList = videoService.search(trimmedKw, currentPage, PAGE_SIZE);
            totalItems = videoService.countSearch(trimmedKw);
            req.setAttribute("keyword", trimmedKw);
        } else {
            videoList = videoService.findAll(currentPage, PAGE_SIZE);
            totalItems = videoService.countAll();
        }

        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        if (totalPages == 0) {
            totalPages = 1;
        }

        req.setAttribute("videoList", videoList);
        req.setAttribute("currentPage", currentPage);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalItems", totalItems);
        req.setAttribute("pageSize", PAGE_SIZE);
        req.setAttribute("activeMenu", "video");

        req.getRequestDispatcher(Constant.Path.VIDEO_LIST).forward(req, resp);
    }
}
