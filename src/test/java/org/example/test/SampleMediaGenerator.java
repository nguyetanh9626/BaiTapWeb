package org.example.test;

import org.example.util.Constant;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tiện ích tự động tạo 12 ảnh thumbnail sắc nét và file video mẫu cho 12 video mẫu trong CSDL.
 */
public class SampleMediaGenerator {

    private static final String[] TITLES = {
            "Giới thiệu Trường ĐH Công nghệ Kỹ thuật",
            "Hướng dẫn Lập trình Web Servlet & JSP",
            "Lập trình CSDL với JDBC & SQL Server",
            "Tìm hiểu Hibernate & JPA Enterprise",
            "Bảo mật Mật khẩu an toàn với BCrypt",
            "Xác thực Tài khoản qua OTP Email",
            "Upload File & Video với MultipartConfig",
            "Giao diện Responsive CSS Grid & Bootstrap",
            "Phân trang dữ liệu OFFSET FETCH NEXT",
            "Chống tấn công CSRF & XSS trong Java Web",
            "Quản lý Session & Cookie an toàn",
            "Xây dựng RESTful API & Tích hợp Dịch vụ"
    };

    private static final Color[] GRADIENT_COLORS = {
            new Color(30, 58, 138), new Color(37, 99, 235),
            new Color(15, 23, 42), new Color(51, 65, 85),
            new Color(13, 148, 136), new Color(20, 184, 166),
            new Color(124, 58, 237), new Color(168, 85, 247),
            new Color(194, 65, 12), new Color(249, 115, 22),
            new Color(190, 24, 93), new Color(236, 72, 153)
    };

    public static void generateAllSampleMedia(String webappBaseDir) {
        Path webappThumbDir = Paths.get(webappBaseDir, "assets", "images", "thumbnails");
        Path webappVideoDir = Paths.get(webappBaseDir, "assets", "videos");
        Path uploadThumbDir = Constant.getThumbnailUploadDir();
        Path uploadVideoDir = Constant.getVideoUploadDir();

        try {
            Files.createDirectories(webappThumbDir);
            Files.createDirectories(webappVideoDir);
            Files.createDirectories(uploadThumbDir);
            Files.createDirectories(uploadVideoDir);

            // 1. Tạo 12 Thumbnail đẹp mắt
            for (int i = 1; i <= 12; i++) {
                String filename = "thumb-" + i + ".jpg";
                String title = (i - 1 < TITLES.length) ? TITLES[i - 1] : "Bài Giảng #" + i;
                Color c1 = GRADIENT_COLORS[(i - 1) % GRADIENT_COLORS.length];
                Color c2 = new Color(Math.min(255, c1.getRed() + 40), Math.min(255, c1.getGreen() + 40), Math.min(255, c1.getBlue() + 40));

                BufferedImage img = createThumbnailImage(i, title, c1, c2);

                File webappFile = webappThumbDir.resolve(filename).toFile();
                File uploadFile = uploadThumbDir.resolve(filename).toFile();
                ImageIO.write(img, "jpg", webappFile);
                ImageIO.write(img, "jpg", uploadFile);
            }

            // Default thumbnail
            BufferedImage defaultImg = createThumbnailImage(0, "Kho Video ĐH Công nghệ Kỹ thuật", new Color(30, 41, 59), new Color(71, 85, 105));
            ImageIO.write(defaultImg, "jpg", webappThumbDir.resolve("default-thumb.jpg").toFile());
            ImageIO.write(defaultImg, "jpg", uploadThumbDir.resolve("default-thumb.jpg").toFile());

            // 2. Bảo toàn file video mẫu MP4 nếu đã tồn tại và hợp lệ
            Path seedVideo = Paths.get("C:/Users/USER/.vscode/extensions/openai.chatgpt-26.1111.21308-win32-x64/webview/assets/3f0460c4764f.mp4");
            byte[] realMp4Bytes = null;
            if (Files.isRegularFile(seedVideo)) {
                try {
                    realMp4Bytes = Files.readAllBytes(seedVideo);
                } catch (Exception ignored) {
                }
            }
            if (realMp4Bytes == null) {
                realMp4Bytes = createMinimalMp4Bytes();
            }

            for (int i = 1; i <= 12; i++) {
                String videoName = "sample-video-" + i + ".mp4";
                Path f1 = webappVideoDir.resolve(videoName);
                Path f2 = uploadVideoDir.resolve(videoName);
                if (!Files.exists(f1) || Files.size(f1) < 1000) {
                    Files.write(f1, realMp4Bytes);
                }
                if (!Files.exists(f2) || Files.size(f2) < 1000) {
                    Files.write(f2, realMp4Bytes);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage createThumbnailImage(int index, String title, Color color1, Color color2) {
        int width = 640;
        int height = 360;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Nền Gradient
        GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);

        // Icon Play ở giữa
        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.fillOval(width / 2 - 40, height / 2 - 55, 80, 80);
        g2d.setColor(Color.WHITE);
        Polygon triangle = new Polygon();
        triangle.addPoint(width / 2 - 10, height / 2 - 35);
        triangle.addPoint(width / 2 - 10, height / 2 + 5);
        triangle.addPoint(width / 2 + 20, height / 2 - 15);
        g2d.fillPolygon(triangle);

        // Header trường
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.drawString("TRƯỜNG ĐẠI HỌC CÔNG NGHỆ KỸ THUẬT", 30, 40);

        // Tiêu đề video
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.setColor(Color.WHITE);
        g2d.drawString(title, 30, height - 55);

        // Badge Bài học
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Video #" + (index > 0 ? index : "HD") + " • Kho Bài Giảng Trực Tuyến", 30, height - 25);

        g2d.dispose();
        return image;
    }

    private static byte[] createMinimalMp4Bytes() {
        // Minimal ISO Base Media File Format (MP4) container header
        byte[] ftyp = new byte[]{
                0x00, 0x00, 0x00, 0x18, 'f', 't', 'y', 'p', 'm', 'p', '4', '2',
                0x00, 0x00, 0x00, 0x00, 'i', 's', 'o', 'm', 'm', 'p', '4', '2'
        };
        byte[] moov = new byte[]{
                0x00, 0x00, 0x00, 0x08, 'm', 'o', 'o', 'v'
        };
        byte[] mdat = new byte[]{
                0x00, 0x00, 0x00, 0x08, 'm', 'd', 'a', 't'
        };
        byte[] all = new byte[ftyp.length + moov.length + mdat.length];
        System.arraycopy(ftyp, 0, all, 0, ftyp.length);
        System.arraycopy(moov, 0, all, ftyp.length, moov.length);
        System.arraycopy(mdat, 0, all, ftyp.length + moov.length, mdat.length);
        return all;
    }
}
