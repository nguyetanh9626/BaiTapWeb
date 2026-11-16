package org.example.service;

import org.example.dao.IUserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.example.model.UserModel;
import org.example.util.Constant;
import org.example.util.OtpUtil;
import org.example.util.PasswordUtil;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * Triển khai Business Logic Layer cho đăng nhập, đăng ký, xác thực OTP và bảo mật mật khẩu BCrypt.
 */
public class UserServiceImpl implements IUserService {

    private final IUserDao userDao = new UserDaoImpl();
    private final EmailService emailService = new EmailService();

    @Override
    public UserModel login(String usernameOrEmail, String password) {
        if (isBlank(usernameOrEmail) || isBlank(password)) {
            return null;
        }
        return userDao.checkLogin(usernameOrEmail.trim(), password);
    }

    @Override
    public User get(String username) {
        return findByUsername(username);
    }

    @Override
    public UserModel findByUsername(String username) {
        if (isBlank(username)) {
            return null;
        }
        return userDao.findByUsername(username.trim());
    }

    @Override
    public User findByEmail(String email) {
        if (isBlank(email)) {
            return null;
        }
        return userDao.findByEmail(email.trim());
    }

    @Override
    public User findByUsernameOrEmail(String identifier) {
        if (isBlank(identifier)) {
            return null;
        }
        return userDao.findByUsernameOrEmail(identifier.trim());
    }

    @Override
    public void insert(User user) {
        if (user != null) {
            userDao.insert(user);
        }
    }

    @Override
    public boolean register(String email, String password, String username, String fullname, String phone) {
        String result = registerWithOtp(email, password, username, fullname, phone);
        return "SUCCESS".equals(result);
    }

    @Override
    public String registerWithOtp(String email, String password, String username, String fullname, String phone) {
        if (isBlank(email) || isBlank(password) || isBlank(username)
                || isBlank(fullname) || isBlank(phone)) {
            return "Vui lòng nhập đầy đủ tất cả các trường thông tin!";
        }
        String trimmedEmail = email.trim();
        String trimmedUsername = username.trim();
        String trimmedFullname = fullname.trim();
        String trimmedPhone = phone.trim();

        if (!trimmedEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Địa chỉ email không đúng định dạng!";
        }
        if (password.length() < 6) {
            return "Mật khẩu phải chứa ít nhất 6 ký tự!";
        }
        if (userDao.checkExistUsername(trimmedUsername)) {
            return "Tên đăng nhập \"" + trimmedUsername + "\" đã tồn tại!";
        }
        if (userDao.checkExistEmail(trimmedEmail)) {
            return "Email \"" + trimmedEmail + "\" đã được đăng ký trong hệ thống!";
        }
        if (userDao.checkExistPhone(trimmedPhone)) {
            return "Số điện thoại \"" + trimmedPhone + "\" đã được đăng ký trong hệ thống!";
        }

        // Băm mật khẩu bằng BCrypt
        String hashedPassword = PasswordUtil.hashPassword(password);

        // Sinh mã OTP 6 chữ số an toàn
        String plainOtp = OtpUtil.generateOtp();
        String otpHash = OtpUtil.hashOtp(plainOtp);

        long now = System.currentTimeMillis();
        Timestamp expiry = new Timestamp(now + (OtpUtil.OTP_EXPIRY_MINUTES * 60 * 1000L));
        Timestamp lastSent = new Timestamp(now);

        User newUser = new User();
        newUser.setEmail(trimmedEmail);
        newUser.setUserName(trimmedUsername);
        newUser.setFullName(trimmedFullname);
        newUser.setPassWord(hashedPassword);
        newUser.setAvatar(null);
        newUser.setRoleid(Constant.ROLE_USER);
        newUser.setPhone(trimmedPhone);
        newUser.setCreatedDate(new Date(now));
        newUser.setActive(false); // Trạng thái chưa kích hoạt
        newUser.setOtpHash(otpHash);
        newUser.setOtpExpiry(expiry);
        newUser.setOtpAttempts(0);
        newUser.setOtpLastSentAt(lastSent);

        userDao.insert(newUser);

        // Gửi email OTP qua Jakarta Mail
        emailService.sendOtpEmail(trimmedEmail, trimmedFullname, plainOtp, "Kích hoạt tài khoản");

        return "SUCCESS";
    }

    @Override
    public String verifyOtpAndActivate(String username, String enteredOtp) {
        if (isBlank(username) || isBlank(enteredOtp)) {
            return "Vui lòng nhập mã OTP!";
        }
        User user = userDao.findByUsername(username.trim());
        if (user == null) {
            return "Tài khoản không tồn tại trong hệ thống!";
        }
        if (user.isActive()) {
            return "ALREADY_ACTIVE";
        }
        if (user.getOtpHash() == null) {
            return "Mã OTP chưa được tạo hoặc đã hết hiệu lực. Vui lòng yêu cầu mã mới!";
        }
        if (user.getOtpAttempts() >= OtpUtil.MAX_OTP_ATTEMPTS) {
            return "Bạn đã nhập sai mã OTP quá 5 lần. Vui lòng yêu cầu gửi lại mã mới!";
        }
        long now = System.currentTimeMillis();
        if (user.getOtpExpiry() == null || now > user.getOtpExpiry().getTime()) {
            return "Mã OTP đã hết hạn sử dụng (5 phút). Vui lòng yêu cầu gửi lại mã mới!";
        }

        if (!OtpUtil.verifyOtp(enteredOtp.trim(), user.getOtpHash())) {
            userDao.incrementOtpAttempts(user.getId());
            int remaining = OtpUtil.MAX_OTP_ATTEMPTS - (user.getOtpAttempts() + 1);
            if (remaining <= 0) {
                return "Bạn đã nhập sai mã OTP quá 5 lần. Vui lòng yêu cầu gửi lại mã mới!";
            }
            return "Mã OTP không chính xác! Bạn còn " + remaining + " lần nhập.";
        }

        // Kích hoạt tài khoản thành công
        userDao.activateUser(user.getId());
        return "SUCCESS";
    }

    @Override
    public String resendActivationOtp(String username) {
        if (isBlank(username)) {
            return "Tên đăng nhập không được để trống!";
        }
        User user = userDao.findByUsername(username.trim());
        if (user == null) {
            return "Tài khoản không tồn tại trong hệ thống!";
        }
        if (user.isActive()) {
            return "Tài khoản này đã được kích hoạt từ trước!";
        }

        long now = System.currentTimeMillis();
        if (user.getOtpLastSentAt() != null) {
            long diffSec = (now - user.getOtpLastSentAt().getTime()) / 1000L;
            if (diffSec < OtpUtil.OTP_RESEND_COOLDOWN_SECONDS) {
                long waitTime = OtpUtil.OTP_RESEND_COOLDOWN_SECONDS - diffSec;
                return "Vui lòng chờ " + waitTime + " giây trước khi yêu cầu gửi lại mã OTP!";
            }
        }

        String plainOtp = OtpUtil.generateOtp();
        String otpHash = OtpUtil.hashOtp(plainOtp);
        Timestamp expiry = new Timestamp(now + (OtpUtil.OTP_EXPIRY_MINUTES * 60 * 1000L));
        Timestamp lastSent = new Timestamp(now);

        userDao.updateOtp(user.getId(), otpHash, expiry, lastSent);
        emailService.sendOtpEmail(user.getEmail(), user.getFullName(), plainOtp, "Kích hoạt tài khoản");

        return "SUCCESS";
    }

    @Override
    public String sendForgotPasswordOtp(String email) {
        if (isBlank(email)) {
            return "Vui lòng nhập địa chỉ email!";
        }
        User user = userDao.findByEmail(email.trim());
        if (user == null) {
            return "Địa chỉ email \"" + email.trim() + "\" không tồn tại trong hệ thống!";
        }

        long now = System.currentTimeMillis();
        if (user.getOtpLastSentAt() != null) {
            long diffSec = (now - user.getOtpLastSentAt().getTime()) / 1000L;
            if (diffSec < OtpUtil.OTP_RESEND_COOLDOWN_SECONDS) {
                long waitTime = OtpUtil.OTP_RESEND_COOLDOWN_SECONDS - diffSec;
                return "Vui lòng chờ " + waitTime + " giây trước khi yêu cầu gửi lại mã OTP!";
            }
        }

        String plainOtp = OtpUtil.generateOtp();
        String otpHash = OtpUtil.hashOtp(plainOtp);
        Timestamp expiry = new Timestamp(now + (OtpUtil.OTP_EXPIRY_MINUTES * 60 * 1000L));
        Timestamp lastSent = new Timestamp(now);

        userDao.updateOtp(user.getId(), otpHash, expiry, lastSent);
        emailService.sendOtpEmail(user.getEmail(), user.getFullName(), plainOtp, "Đặt lại mật khẩu");

        return "SUCCESS";
    }

    @Override
    public String verifyResetPasswordOtp(String email, String enteredOtp) {
        if (isBlank(email) || isBlank(enteredOtp)) {
            return "Vui lòng nhập đầy đủ email và mã OTP!";
        }
        User user = userDao.findByEmail(email.trim());
        if (user == null) {
            return "Tài khoản email không tồn tại!";
        }
        if (user.getOtpHash() == null) {
            return "Yêu cầu đặt lại mật khẩu chưa được khởi tạo hoặc mã OTP đã hết hạn!";
        }
        if (user.getOtpAttempts() >= OtpUtil.MAX_OTP_ATTEMPTS) {
            return "Đã nhập sai OTP quá 5 lần. Vui lòng yêu cầu mã OTP mới!";
        }
        long now = System.currentTimeMillis();
        if (user.getOtpExpiry() == null || now > user.getOtpExpiry().getTime()) {
            return "Mã OTP đã hết hạn (5 phút). Vui lòng yêu cầu mã mới!";
        }
        if (!OtpUtil.verifyOtp(enteredOtp.trim(), user.getOtpHash())) {
            userDao.incrementOtpAttempts(user.getId());
            int remaining = OtpUtil.MAX_OTP_ATTEMPTS - (user.getOtpAttempts() + 1);
            if (remaining <= 0) {
                return "Bạn đã nhập sai mã OTP quá 5 lần. Vui lòng yêu cầu lại mã mới!";
            }
            return "Mã OTP không chính xác! Bạn còn " + remaining + " lần nhập.";
        }
        return "SUCCESS";
    }

    @Override
    public String resetPassword(String email, String otp, String newPassword) {
        String checkOtp = verifyResetPasswordOtp(email, otp);
        if (!"SUCCESS".equals(checkOtp)) {
            return checkOtp;
        }
        if (isBlank(newPassword) || newPassword.length() < 6) {
            return "Mật khẩu mới phải có ít nhất 6 ký tự!";
        }
        User user = userDao.findByEmail(email.trim());
        if (user == null) {
            return "Người dùng không tồn tại!";
        }

        // Băm mật khẩu mới bằng BCrypt và lưu DB
        String newHashed = PasswordUtil.hashPassword(newPassword);
        userDao.updatePassword(user.getId(), newHashed);

        // Kích hoạt tài khoản và vô hiệu hóa OTP cũ
        userDao.activateUser(user.getId());

        return "SUCCESS";
    }

    @Override
    public boolean checkExistEmail(String email) {
        return !isBlank(email) && userDao.checkExistEmail(email.trim());
    }

    @Override
    public boolean checkExistUsername(String username) {
        return !isBlank(username) && userDao.checkExistUsername(username.trim());
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return !isBlank(phone) && userDao.checkExistPhone(phone.trim());
    }

    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public List<User> search(String keyword) {
        return userDao.search(keyword);
    }

    private static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
