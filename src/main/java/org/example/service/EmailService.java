package org.example.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dịch vụ gửi Email thông báo và mã xác thực OTP qua Jakarta Mail.
 * Có cơ chế ghi log dự phòng (Fallback Logging) khi chưa cấu hình tài khoản SMTP thực tế.
 */
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    private static final Properties configProps = new Properties();

    static {
        try (InputStream is = EmailService.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                configProps.load(is);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể tải file db.properties để đọc cấu hình Mail", e);
        }
    }

    /**
     * Gửi mã OTP kích hoạt tài khoản hoặc đặt lại mật khẩu đến email người dùng.
     *
     * @param toEmail   địa chỉ email nhận
     * @param recipient tên người nhận
     * @param otp       mã OTP 6 số
     * @param purpose   mục đích gửi ("Kích hoạt tài khoản" hoặc "Đặt lại mật khẩu")
     * @return true nếu gửi thành công hoặc đã log OTP hợp lệ
     */
    public boolean sendOtpEmail(String toEmail, String recipient, String otp, String purpose) {
        // In mã OTP ra màn hình console Tomcat để nhận mã trực tiếp thuận tiện kiểm thử
        System.out.println();
        System.out.println("================================================================");
        System.out.println(">>> [TOMCAT CONSOLE] MÃ OTP XÁC THỰC BẢO MẬT <<<");
        System.out.println(">>> Người nhận : " + (recipient != null ? recipient : "Người dùng") + " <" + toEmail + ">");
        System.out.println(">>> Mục đích   : " + purpose);
        System.out.println(">>> ========================================");
        System.out.println(">>>  MÃ OTP CỦA BẠN LÀ: [ " + otp + " ]");
        System.out.println(">>> ========================================");
        System.out.println(">>> Thời hạn   : 5 phút (Nhập mã tại trang web để kích hoạt)");
        System.out.println("================================================================");
        System.out.println();

        String host = getProperty("mail.smtp.host", "smtp.gmail.com");
        String port = getProperty("mail.smtp.port", "587");
        String auth = getProperty("mail.smtp.auth", "true");
        String starttls = getProperty("mail.smtp.starttls.enable", "true");
        String senderEmail = getProperty("mail.smtp.username", "").trim();
        // Tự động bỏ khoảng trắng nếu người dùng copy mật khẩu ứng dụng dạng 'xxxx xxxx xxxx xxxx' từ Google
        String senderPass = getProperty("mail.smtp.password", "").trim().replace(" ", "");

        // Nếu chưa cấu hình email thật thì hoàn tất gửi bằng log an toàn
        if (senderEmail.isEmpty() || senderPass.isEmpty()) {
            LOGGER.info("[EMAIL SERVICE] Chưa cấu hình mail.smtp.username/password trong db.properties. OTP đã được in ra console.");
            return true;
        }

        try {
            Properties mailProps = new Properties();
            mailProps.put("mail.smtp.host", host);
            mailProps.put("mail.smtp.port", port);
            mailProps.put("mail.smtp.auth", auth);
            mailProps.put("mail.smtp.starttls.enable", starttls);
            mailProps.put("mail.smtp.ssl.trust", host.equals("smtp.gmail.com") ? "*" : host);
            mailProps.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
            if ("465".equals(port)) {
                mailProps.put("mail.smtp.ssl.enable", "true");
            }

            Session session = Session.getInstance(mailProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPass);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, "Trường Đại học Công nghệ Kỹ thuật", "UTF-8"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("[" + purpose + "] Mã xác thực OTP của bạn: " + otp, "UTF-8");

            String htmlContent = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 24px; border: 1px solid #e2e8f0; border-radius: 8px;\">"
                    + "<div style=\"text-align: center; border-bottom: 2px solid #2563eb; padding-bottom: 16px; margin-bottom: 20px;\">"
                    + "<h2 style=\"color: #1e3a8a; margin: 0;\">TRƯỜNG ĐẠI HỌC CÔNG NGHỆ KỸ THUẬT</h2>"
                    + "<p style=\"color: #64748b; font-size: 14px; margin: 4px 0 0 0;\">Hệ thống Đăng ký & Quản lý Tài khoản</p>"
                    + "</div>"
                    + "<p>Xin chào <strong>" + (recipient != null ? recipient : "Bạn") + "</strong>,</p>"
                    + "<p>Hệ thống nhận được yêu cầu <strong>" + purpose.toLowerCase() + "</strong> của bạn. Dưới đây là mã xác thực OTP của bạn:</p>"
                    + "<div style=\"background: #f1f5f9; padding: 18px; text-align: center; border-radius: 6px; margin: 24px 0;\">"
                    + "<span style=\"font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #2563eb;\">" + otp + "</span>"
                    + "</div>"
                    + "<p style=\"color: #dc2626; font-size: 14px;\"><strong>* Lưu ý quan trọng:</strong></p>"
                    + "<ul style=\"color: #475569; font-size: 13px; line-height: 1.6;\">"
                    + "<li>Mã OTP có hiệu lực trong vòng <strong>5 phút</strong> kể từ thời điểm gửi.</li>"
                    + "<li>Mã này chỉ được sử dụng duy nhất một lần.</li>"
                    + "<li>Tuyệt đối không chia sẻ mã này cho bất kỳ ai để đảm bảo an toàn tài khoản.</li>"
                    + "</ul>"
                    + "<p style=\"color: #94a3b8; font-size: 12px; margin-top: 30px; text-align: center; border-top: 1px solid #f1f5f9; padding-top: 16px;\">"
                    + "Đây là email tự động, vui lòng không trả lời thư này.</p>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(message);
            LOGGER.info("[EMAIL SERVICE] Đã gửi email OTP thực tế tới " + toEmail);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "[EMAIL SERVICE] Không thể gửi email qua SMTP: " + e.getMessage() + ". OTP vẫn có hiệu lực từ console.", e);
            return true; // Trả về true để không chặn luồng đăng ký của người dùng khi offline
        }
    }

    private static String getProperty(String key, String defaultValue) {
        String sysVal = System.getenv(key.replace('.', '_').toUpperCase());
        if (sysVal != null && !sysVal.isEmpty()) {
            return sysVal;
        }
        return configProps.getProperty(key, defaultValue);
    }
}
