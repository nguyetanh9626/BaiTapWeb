package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.util.Constant;
import org.example.util.ImageUploadUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controller phục vụ hiển thị và tải hình ảnh danh mục (/image?fname=...).
 */
@WebServlet(urlPatterns = "/image")
public class DownloadImageController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String SEEDED_IMAGES_BASE = "/assets/images/categories/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String rawFileName = req.getParameter("fname");
        if (rawFileName == null || rawFileName.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu tên file ảnh.");
            return;
        }

        String safeFileName = Paths.get(rawFileName.trim()).getFileName().toString();
        String contentType = ImageUploadUtil.resolveContentType(safeFileName);

        Path uploadedFile = Constant.getCategoryUploadDir().resolve(safeFileName);
        if (!Files.isRegularFile(uploadedFile)) {
            uploadedFile = Paths.get(Constant.DIR).resolve(safeFileName);
        }
        if (Files.isRegularFile(uploadedFile)) {
            resp.setContentType(contentType);
            resp.setContentLengthLong(Files.size(uploadedFile));
            Files.copy(uploadedFile, resp.getOutputStream());
            return;
        }

        try (InputStream bundled = getServletContext().getResourceAsStream(SEEDED_IMAGES_BASE + safeFileName)) {
            if (bundled != null) {
                resp.setContentType(contentType);
                bundled.transferTo(resp.getOutputStream());
                return;
            }
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
