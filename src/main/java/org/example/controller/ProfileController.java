package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.example.dao.DataAccessException;
import org.example.dao.IUserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.example.service.CloudinaryService;
import org.example.service.EmailService;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;
import org.example.util.Constant;
import org.example.util.OtpUtil;
import org.example.util.SessionUtil;
import org.example.util.ValidationUtil;

import java.io.IOException;

/**
 * Controller quản lý Hồ sơ cá nhân người dùng (User Profile):
 * - /profile                 : Xem thông tin tài khoản đang đăng nhập
 * - /profile/update-info     : Cập nhật Họ và tên, Số điện thoại
 * - /profile/send-email-otp  : Gửi mã OTP xác nhận tới địa chỉ Email mới
 * - /profile/verify-email-otp: Xác thực OTP và cập nhật Email mới vào CSDL
 * - /profile/upload-avatar   : Tải lên và đồng bộ ảnh đại diện qua Cloudinary bằng Multipart
 */
@WebServlet(urlPatterns = {
        "/profile",
        "/profile/update-info",
        "/profile/send-email-otp",
        "/profile/verify-email-otp",
        "/profile/upload-avatar"
})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1MB
        maxFileSize = 1024 * 1024 * 5,        // 5MB
        maxRequestSize = 1024 * 1024 * 10     // 10MB
)
public class ProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String SESS_EMAIL_CHANGE_NEW = "email_change_new";
    public static final String SESS_EMAIL_CHANGE_OTP_HASH = "email_change_otp_hash";
    public static final String SESS_EMAIL_CHANGE_EXPIRY = "email_change_expiry";
    public static final String SESS_EMAIL_CHANGE_ATTEMPTS = "email_change_attempts";
    public static final String SESS_EMAIL_CHANGE_LAST_SENT = "email_change_last_sent";

    private final UserService userService = new UserServiceImpl();
    private final IUserDao userDao = new UserDaoImpl();
    private final EmailService emailService = new EmailService();
    private final CloudinaryService cloudinaryService = new CloudinaryService();

    private User getAuthenticatedUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(Constant.SESSION_ACCOUNT) == null) {
            resp.sendRedirect(req.getContextPath() + "/login?authRequired=true");
            return null;
        }
        User sessionUser = (User) session.getAttribute(Constant.SESSION_ACCOUNT);
        // Luôn làm tươi dữ liệu từ Database để đảm bảo tính nhất quán
        User freshUser = userDao.findById(sessionUser.getId());
        if (freshUser == null) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        // Đồng bộ session
        session.setAttribute(Constant.SESSION_ACCOUNT, freshUser);
        session.setAttribute(Constant.SESSION_USER, freshUser);
        return freshUser;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User user = getAuthenticatedUser(req, resp);
        if (user == null) {
            return;
        }

        // Đảm bảo CSRF token sẵn sàng cho mọi form
        SessionUtil.getOrCreateCsrfToken(req);

        req.setAttribute("user", user);
        req.setAttribute("cloudinaryConfigured", cloudinaryService.isConfigured());

        // Kiểm tra xem có đang trong quá trình đổi email không
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(SESS_EMAIL_CHANGE_NEW) != null) {
            req.setAttribute("pendingEmail", session.getAttribute(SESS_EMAIL_CHANGE_NEW));
        }

        req.getRequestDispatcher(Constant.Path.PROFILE).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User user = getAuthenticatedUser(req, resp);
        if (user == null) {
            return;
        }

        // Bắt buộc xác thực CSRF Token
        if (!SessionUtil.validateCsrfToken(req)) {
            SessionUtil.setFlashError(req, "Mã bảo mật CSRF Token không hợp lệ hoặc phiên làm việc đã hết hạn!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String path = req.getServletPath();

        switch (path) {
            case "/profile/update-info":
                handleUpdateInfo(req, resp, user);
                break;
            case "/profile/send-email-otp":
                handleSendEmailOtp(req, resp, user);
                break;
            case "/profile/verify-email-otp":
                handleVerifyEmailOtp(req, resp, user);
                break;
            case "/profile/upload-avatar":
                handleUploadAvatar(req, resp, user);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/profile");
                break;
        }
    }

    private void handleUpdateInfo(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        String fullname = req.getParameter("fullname");
        String phone = req.getParameter("phone");

        // Backend Validation
        if (ValidationUtil.isBlank(fullname)) {
            SessionUtil.setFlashError(req, "Họ và tên không được để trống!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String cleanName = fullname.trim();
        if (cleanName.length() < 2 || cleanName.length() > 100) {
            SessionUtil.setFlashError(req, "Họ và tên phải có độ dài từ 2 đến 100 ký tự!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (ValidationUtil.isBlank(phone)) {
            SessionUtil.setFlashError(req, "Số điện thoại không được để trống!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String cleanPhone = phone.trim();
        if (!ValidationUtil.isValidPhone(cleanPhone)) {
            SessionUtil.setFlashError(req, "Số điện thoại không đúng định dạng (VD: 0901234567, 10 số bắt đầu bằng số 0)!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (userDao.checkExistPhoneExceptUser(cleanPhone, user.getId())) {
            SessionUtil.setFlashError(req, "Số điện thoại này đã được tài khoản khác sử dụng. Vui lòng chọn số khác!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        try {
            userDao.updateProfileInfo(user.getId(), cleanName, cleanPhone);
            user.setFullName(cleanName);
            user.setPhone(cleanPhone);
            HttpSession session = req.getSession(true);
            session.setAttribute(Constant.SESSION_ACCOUNT, user);
            session.setAttribute(Constant.SESSION_USER, user);

            SessionUtil.setFlashSuccess(req, "Cập nhật thông tin cá nhân thành công!");
        } catch (DataAccessException e) {
            SessionUtil.setFlashError(req, "Lỗi cập nhật CSDL: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    private void handleSendEmailOtp(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        String newEmail = req.getParameter("newEmail");

        if (ValidationUtil.isBlank(newEmail)) {
            SessionUtil.setFlashError(req, "Vui lòng nhập địa chỉ email mới!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String cleanEmail = newEmail.trim().toLowerCase();
        if (!ValidationUtil.isValidEmail(cleanEmail)) {
            SessionUtil.setFlashError(req, "Địa chỉ email mới không đúng định dạng!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (cleanEmail.equalsIgnoreCase(user.getEmail())) {
            SessionUtil.setFlashError(req, "Email mới phải khác với email hiện tại của bạn!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (userDao.checkExistEmail(cleanEmail)) {
            SessionUtil.setFlashError(req, "Địa chỉ email này đã được tài khoản khác đăng ký sử dụng!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        HttpSession session = req.getSession(true);
        Long lastSent = (Long) session.getAttribute(SESS_EMAIL_CHANGE_LAST_SENT);
        long now = System.currentTimeMillis();
        if (lastSent != null) {
            long diffSec = (now - lastSent) / 1000;
            if (diffSec < OtpUtil.OTP_RESEND_COOLDOWN_SECONDS) {
                long wait = OtpUtil.OTP_RESEND_COOLDOWN_SECONDS - diffSec;
                SessionUtil.setFlashError(req, "Vui lòng đợi " + wait + " giây trước khi yêu cầu gửi lại mã OTP!");
                resp.sendRedirect(req.getContextPath() + "/profile");
                return;
            }
        }

        String plainOtp = OtpUtil.generateOtp();
        String otpHash = OtpUtil.hashOtp(plainOtp);
        long expiry = now + (OtpUtil.OTP_EXPIRY_MINUTES * 60 * 1000L);

        // Lưu thông tin đổi email trong session an toàn của người dùng hiện tại
        session.setAttribute(SESS_EMAIL_CHANGE_NEW, cleanEmail);
        session.setAttribute(SESS_EMAIL_CHANGE_OTP_HASH, otpHash);
        session.setAttribute(SESS_EMAIL_CHANGE_EXPIRY, expiry);
        session.setAttribute(SESS_EMAIL_CHANGE_ATTEMPTS, 0);
        session.setAttribute(SESS_EMAIL_CHANGE_LAST_SENT, now);

        boolean sent = emailService.sendOtpEmail(cleanEmail, user.getFullName(), plainOtp, "Cập nhật Email");
        if (sent) {
            SessionUtil.setFlashSuccess(req, "Mã xác thực OTP đã được gửi tới email mới (" + cleanEmail + "). Hiệu lực trong 5 phút.");
        } else {
            SessionUtil.setFlashError(req, "Không thể gửi email OTP qua máy chủ SMTP. Vui lòng kiểm tra lại cấu hình mail.smtp trong db.properties!");
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    private void handleVerifyEmailOtp(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(SESS_EMAIL_CHANGE_NEW) == null) {
            SessionUtil.setFlashError(req, "Chưa có yêu cầu đổi email nào được khởi tạo hoặc phiên làm việc đã kết thúc!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String pendingEmail = (String) session.getAttribute(SESS_EMAIL_CHANGE_NEW);
        String expectedHash = (String) session.getAttribute(SESS_EMAIL_CHANGE_OTP_HASH);
        Long expiry = (Long) session.getAttribute(SESS_EMAIL_CHANGE_EXPIRY);
        Integer attempts = (Integer) session.getAttribute(SESS_EMAIL_CHANGE_ATTEMPTS);
        if (attempts == null) attempts = 0;

        String enteredOtp = req.getParameter("otp");
        if (ValidationUtil.isBlank(enteredOtp)) {
            SessionUtil.setFlashError(req, "Vui lòng nhập mã OTP gồm 6 chữ số!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        long now = System.currentTimeMillis();
        if (expiry == null || now > expiry) {
            clearEmailChangeSession(session);
            SessionUtil.setFlashError(req, "Mã OTP đổi email đã hết hạn (5 phút). Vui lòng yêu cầu mã OTP mới!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (attempts >= OtpUtil.MAX_OTP_ATTEMPTS) {
            clearEmailChangeSession(session);
            SessionUtil.setFlashError(req, "Bạn đã nhập sai mã OTP quá 5 lần. Yêu cầu đổi email đã bị hủy để đảm bảo an toàn!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (!OtpUtil.verifyOtp(enteredOtp.trim(), expectedHash)) {
            int newAttempts = attempts + 1;
            session.setAttribute(SESS_EMAIL_CHANGE_ATTEMPTS, newAttempts);
            int remaining = OtpUtil.MAX_OTP_ATTEMPTS - newAttempts;
            if (remaining <= 0) {
                clearEmailChangeSession(session);
                SessionUtil.setFlashError(req, "Bạn đã nhập sai mã OTP quá 5 lần. Yêu cầu đổi email đã bị hủy!");
            } else {
                SessionUtil.setFlashError(req, "Mã OTP không chính xác! Bạn còn lại " + remaining + " lần thử.");
            }
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        // Kiểm tra race condition ở thời điểm chốt cập nhật
        if (userDao.checkExistEmail(pendingEmail)) {
            clearEmailChangeSession(session);
            SessionUtil.setFlashError(req, "Email này vừa được tài khoản khác đăng ký. Vui lòng chọn địa chỉ email khác!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        try {
            userDao.updateEmail(user.getId(), pendingEmail);
            user.setEmail(pendingEmail);
            session.setAttribute(Constant.SESSION_ACCOUNT, user);
            session.setAttribute(Constant.SESSION_USER, user);
            clearEmailChangeSession(session);

            SessionUtil.setFlashSuccess(req, "Xác thực OTP thành công! Email của bạn đã được cập nhật thành: " + pendingEmail);
        } catch (DataAccessException e) {
            SessionUtil.setFlashError(req, "Lỗi cập nhật CSDL: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    private void handleUploadAvatar(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        Part avatarPart = null;
        try {
            avatarPart = req.getPart("avatarFile");
        } catch (Exception e) {
            SessionUtil.setFlashError(req, "Lỗi đọc dữ liệu Multipart tải lên: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (avatarPart == null || avatarPart.getSize() <= 0) {
            SessionUtil.setFlashError(req, "Vui lòng chọn một file ảnh để tải lên làm ảnh đại diện!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (!ValidationUtil.isValidImageFile(avatarPart)) {
            SessionUtil.setFlashError(req, "File không hợp lệ! Chỉ chấp nhận ảnh định dạng JPG, JPEG, PNG, WEBP và dung lượng không quá 5MB.");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (!cloudinaryService.isConfigured()) {
            SessionUtil.setFlashError(req, "Máy chủ chưa cấu hình Cloudinary (CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET). Không thể tải ảnh lên!");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String oldPublicId = user.getAvatarPublicId();
        CloudinaryService.UploadResult result;
        try {
            result = cloudinaryService.uploadAvatar(avatarPart, user.getId());
        } catch (Exception e) {
            SessionUtil.setFlashError(req, "Lỗi tải ảnh lên Cloudinary: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        try {
            // Cập nhật CSDL
            userDao.updateAvatar(user.getId(), result.getSecureUrl(), result.getPublicId());

            // Sau khi DB cập nhật thành công, xóa ảnh cũ trên Cloudinary nếu có
            if (ValidationUtil.isNotBlank(oldPublicId)) {
                cloudinaryService.deleteImage(oldPublicId);
            }

            user.setAvatar(result.getSecureUrl());
            user.setAvatarPublicId(result.getPublicId());
            HttpSession session = req.getSession(true);
            session.setAttribute(Constant.SESSION_ACCOUNT, user);
            session.setAttribute(Constant.SESSION_USER, user);

            SessionUtil.setFlashSuccess(req, "Cập nhật ảnh đại diện lên Cloudinary thành công!");
        } catch (Exception ex) {
            // Rollback dọn dẹp ảnh mới tải lên nếu ghi CSDL thất bại
            cloudinaryService.deleteImage(result.getPublicId());
            SessionUtil.setFlashError(req, "Lỗi cập nhật CSDL: " + ex.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    private void clearEmailChangeSession(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESS_EMAIL_CHANGE_NEW);
            session.removeAttribute(SESS_EMAIL_CHANGE_OTP_HASH);
            session.removeAttribute(SESS_EMAIL_CHANGE_EXPIRY);
            session.removeAttribute(SESS_EMAIL_CHANGE_ATTEMPTS);
            session.removeAttribute(SESS_EMAIL_CHANGE_LAST_SENT);
        }
    }
}
