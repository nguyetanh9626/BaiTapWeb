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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Controller phục vụ hiển thị Thumbnail và phát video HTML5 (/media/video, /media/thumbnail).
 * Hỗ trợ chuẩn HTTP Range Requests (mã 206 Partial Content) cho thẻ HTML5 <video> tua mượt mà và tương thích mọi trình duyệt.
 */
@WebServlet(urlPatterns = {"/media/video", "/media/thumbnail"})
public class MediaStreamController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final VideoService videoService = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String servletPath = req.getServletPath();
        String fname = req.getParameter("fname");
        if (fname == null || fname.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu tham số tên file.");
            return;
        }

        String safeFileName = Paths.get(fname.trim()).getFileName().toString();

        if (servletPath.contains("thumbnail")) {
            serveThumbnail(req, resp, safeFileName);
        } else {
            serveVideo(req, resp, safeFileName);
        }
    }

    private void serveThumbnail(HttpServletRequest req, HttpServletResponse resp, String filename) throws IOException {
        Path filePath = Constant.getThumbnailUploadDir().resolve(filename).normalize();
        if (Files.isRegularFile(filePath) && filePath.startsWith(Constant.getThumbnailUploadDir())) {
            resp.setContentType(resolveImageMime(filename));
            resp.setContentLengthLong(Files.size(filePath));
            Files.copy(filePath, resp.getOutputStream());
            return;
        }

        // Kiểm tra ảnh trong webapp
        String realWebappPath = getServletContext().getRealPath("/assets/images/thumbnails/" + filename);
        if (realWebappPath != null) {
            Path webappFilePath = Paths.get(realWebappPath);
            if (Files.isRegularFile(webappFilePath)) {
                resp.setContentType(resolveImageMime(filename));
                resp.setContentLengthLong(Files.size(webappFilePath));
                Files.copy(webappFilePath, resp.getOutputStream());
                return;
            }
        }

        String bundledPath = "/assets/images/thumbnails/" + filename;
        try (InputStream in = getServletContext().getResourceAsStream(bundledPath)) {
            if (in != null) {
                resp.setContentType(resolveImageMime(filename));
                in.transferTo(resp.getOutputStream());
                return;
            }
        }

        // Fallback ảnh mặc định
        try (InputStream fallback = getServletContext().getResourceAsStream("/assets/images/categories/default-category.png")) {
            if (fallback != null) {
                resp.setContentType("image/png");
                fallback.transferTo(resp.getOutputStream());
                return;
            }
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy ảnh thumbnail.");
    }

    private void serveVideo(HttpServletRequest req, HttpServletResponse resp, String filename) throws IOException {
        // Kiểm tra quyền truy cập dựa trên bản ghi Video trong database (Phần 7)
        try {
            Video video = videoService.findByVideoPath(filename);
            if (video != null && video.getStatus() != 1) {
                HttpSession session = req.getSession(false);
                User currentUser = session != null ? (User) session.getAttribute(Constant.SESSION_ACCOUNT) : null;
                boolean canAccess = currentUser != null && (currentUser.getRoleid() == Constant.ROLE_ADMIN || currentUser.getId() == video.getUserId());
                if (!canAccess) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Video này đã bị ẩn hoặc tạm khóa. Bạn không có quyền xem video này.");
                    return;
                }
            }
        } catch (Exception ignored) {
        }

        Path filePath = Constant.getVideoUploadDir().resolve(filename).normalize();
        if (!Files.isRegularFile(filePath) || !filePath.startsWith(Constant.getVideoUploadDir())) {
            // Kiểm tra trong thư mục assets của webapp
            String realPath = getServletContext().getRealPath("/assets/videos/" + filename);
            if (realPath != null && Files.isRegularFile(Paths.get(realPath))) {
                filePath = Paths.get(realPath);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy file video: " + filename);
                return;
            }
        }

        long length = Files.size(filePath);
        String mime = resolveVideoMime(filename);
        String range = req.getHeader("Range");

        resp.setContentType(mime);
        resp.setHeader("Accept-Ranges", "bytes");

        if (range == null || !range.startsWith("bytes=")) {
            resp.setContentLengthLong(length);
            Files.copy(filePath, resp.getOutputStream());
            return;
        }

        // Xử lý HTTP 206 Partial Content cho video player
        String[] ranges = range.substring(6).split("-");
        long start;
        long end;
        try {
            start = Long.parseLong(ranges[0]);
            end = ranges.length > 1 && !ranges[1].trim().isEmpty() ? Long.parseLong(ranges[1].trim()) : length - 1;
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            resp.setHeader("Content-Range", "bytes */" + length);
            return;
        }

        if (start > end || start >= length) {
            resp.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            resp.setHeader("Content-Range", "bytes */" + length);
            return;
        }

        long contentLength = end - start + 1;
        resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + length);
        resp.setContentLengthLong(contentLength);

        try (InputStream in = Files.newInputStream(filePath);
             OutputStream out = resp.getOutputStream()) {
            long remainingToSkip = start;
            while (remainingToSkip > 0) {
                long skipped = in.skip(remainingToSkip);
                if (skipped <= 0) {
                    if (in.read() == -1) break;
                    skipped = 1;
                }
                remainingToSkip -= skipped;
            }

            byte[] buffer = new byte[16384];
            long bytesRemaining = contentLength;
            while (bytesRemaining > 0) {
                int toRead = (int) Math.min(buffer.length, bytesRemaining);
                int bytesRead = in.read(buffer, 0, toRead);
                if (bytesRead == -1) {
                    break;
                }
                out.write(buffer, 0, bytesRead);
                bytesRemaining -= bytesRead;
            }
        }
    }

    private String resolveVideoMime(String filename) {
        String lower = filename.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".webm")) return "video/webm";
        if (lower.endsWith(".mkv")) return "video/x-matroska";
        if (lower.endsWith(".mov")) return "video/quicktime";
        if (lower.endsWith(".avi")) return "video/x-msvideo";
        return "video/mp4";
    }

    private String resolveImageMime(String filename) {
        String lower = filename.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }
}
