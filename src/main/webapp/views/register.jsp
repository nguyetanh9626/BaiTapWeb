<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Ký Tài Khoản - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .field-error-msg {
            color: #dc2626;
            font-size: 13px;
            margin-top: 4px;
            display: none;
            text-align: left;
        }
        .input-error {
            border-color: #dc2626 !important;
            background-color: #fef2f2 !important;
        }
    </style>
</head>
<body class="auth-bg">

    <div class="auth-container">
        <div class="auth-card wide">
            <div class="auth-header">
                <h2>Đăng Ký Tài Khoản</h2>
                <p>Điền đầy đủ thông tin bên dưới để tạo tài khoản mới</p>
                <div style="margin-top: 6px; font-size: 13px; color: #2563eb; background: #eff6ff; padding: 6px 12px; border-radius: 6px;">
                    * Lưu ý: Hệ thống sẽ gửi mã xác thực OTP 6 số đến email để kích hoạt tài khoản.
                </div>
            </div>

            <c:if test="${not empty alert}">
                <div class="alert alert-danger" style="margin-top: 14px;">
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <form id="registerForm" action="${pageContext.request.contextPath}/register" method="post" style="margin-top: 14px;" novalidate>
                <div class="form-group text-start">
                    <label for="fullname">Họ và tên <span style="color:#dc2626;">*</span></label>
                    <input type="text"
                           id="fullname"
                           name="fullname"
                           value="<c:out value='${valFullname}'/>"
                           placeholder="Nhập họ và tên đầy đủ..."
                           required />
                    <div id="fullnameError" class="field-error-msg">Họ và tên không được để trống (từ 2 đến 100 ký tự)!</div>
                </div>

                <div class="form-grid">
                    <div class="form-group text-start">
                        <label for="username">Tên đăng nhập <span style="color:#dc2626;">*</span></label>
                        <input type="text"
                               id="username"
                               name="username"
                               value="<c:out value='${valUsername}'/>"
                               placeholder="Nhập tên đăng nhập (viết liền)..."
                               required />
                        <div id="usernameError" class="field-error-msg">Tên đăng nhập từ 3 đến 50 ký tự, không chứa khoảng trắng!</div>
                    </div>

                    <div class="form-group text-start">
                        <label for="phone">Số điện thoại <span style="color:#dc2626;">*</span></label>
                        <input type="tel"
                               id="phone"
                               name="phone"
                               value="<c:out value='${valPhone}'/>"
                               placeholder="VD: 0901234567"
                               required />
                        <div id="phoneError" class="field-error-msg">Số điện thoại không đúng định dạng (10 số bắt đầu bằng 0)!</div>
                    </div>
                </div>

                <div class="form-group text-start">
                    <label for="email">Email nhận mã OTP <span style="color:#dc2626;">*</span></label>
                    <input type="email"
                           id="email"
                           name="email"
                           value="<c:out value='${valEmail}'/>"
                           placeholder="example@student.hcmute.edu.vn"
                           required />
                    <div id="emailError" class="field-error-msg">Địa chỉ email không đúng định dạng!</div>
                </div>

                <div class="form-grid">
                    <div class="form-group text-start">
                        <label for="password">Mật khẩu <span style="color:#dc2626;">*</span></label>
                        <input type="password"
                               id="password"
                               name="password"
                               placeholder="Tối thiểu 6 ký tự..."
                               minlength="6"
                               required />
                        <div id="passwordError" class="field-error-msg">Mật khẩu phải chứa ít nhất 6 ký tự!</div>
                    </div>

                    <div class="form-group text-start">
                        <label for="confirmPassword">Nhập lại mật khẩu <span style="color:#dc2626;">*</span></label>
                        <input type="password"
                               id="confirmPassword"
                               name="confirmPassword"
                               placeholder="Xác nhận mật khẩu..."
                               minlength="6"
                               required />
                        <div id="confirmPasswordError" class="field-error-msg">Mật khẩu xác nhận không khớp!</div>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Đăng Ký & Nhận Mã OTP</button>
            </form>

            <div class="auth-footer">
                <span>Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập ngay</a></span>
                <a href="${pageContext.request.contextPath}/home">&larr; Quay lại Trang chủ</a>
            </div>
        </div>
    </div>

    <script>
    document.addEventListener("DOMContentLoaded", function () {
        const form = document.getElementById("registerForm");
        const fnInput = document.getElementById("fullname");
        const uInput = document.getElementById("username");
        const phInput = document.getElementById("phone");
        const emInput = document.getElementById("email");
        const pInput = document.getElementById("password");
        const cpInput = document.getElementById("confirmPassword");

        const fnErr = document.getElementById("fullnameError");
        const uErr = document.getElementById("usernameError");
        const phErr = document.getElementById("phoneError");
        const emErr = document.getElementById("emailError");
        const pErr = document.getElementById("passwordError");
        const cpErr = document.getElementById("confirmPasswordError");

        function checkFn() {
            const val = fnInput.value.trim();
            if (val.length < 2 || val.length > 100) {
                fnErr.style.display = "block";
                fnInput.classList.add("input-error");
                return false;
            }
            fnErr.style.display = "none";
            fnInput.classList.remove("input-error");
            return true;
        }

        function checkUser() {
            const val = uInput.value.trim();
            if (val.length < 3 || val.length > 50 || /\s/.test(val)) {
                uErr.style.display = "block";
                uInput.classList.add("input-error");
                return false;
            }
            uErr.style.display = "none";
            uInput.classList.remove("input-error");
            return true;
        }

        function checkPhone() {
            const val = phInput.value.trim();
            if (!/^(0[3|5|7|8|9])[0-9]{8}$|^0[0-9]{9,10}$/.test(val)) {
                phErr.style.display = "block";
                phInput.classList.add("input-error");
                return false;
            }
            phErr.style.display = "none";
            phInput.classList.remove("input-error");
            return true;
        }

        function checkEmail() {
            const val = emInput.value.trim();
            const re = /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$/;
            if (!re.test(val)) {
                emErr.style.display = "block";
                emInput.classList.add("input-error");
                return false;
            }
            emErr.style.display = "none";
            emInput.classList.remove("input-error");
            return true;
        }

        function checkPassword() {
            const val = pInput.value;
            if (!val || val.length < 6) {
                pErr.style.display = "block";
                pInput.classList.add("input-error");
                return false;
            }
            pErr.style.display = "none";
            pInput.classList.remove("input-error");
            return true;
        }

        function checkConfirm() {
            const val = cpInput.value;
            if (val !== pInput.value || !val) {
                cpErr.style.display = "block";
                cpInput.classList.add("input-error");
                return false;
            }
            cpErr.style.display = "none";
            cpInput.classList.remove("input-error");
            return true;
        }

        fnInput.addEventListener("input", checkFn);
        uInput.addEventListener("input", checkUser);
        phInput.addEventListener("input", checkPhone);
        emInput.addEventListener("input", checkEmail);
        pInput.addEventListener("input", checkPassword);
        cpInput.addEventListener("input", checkConfirm);

        form.addEventListener("submit", function (e) {
            const ok = checkFn() & checkUser() & checkPhone() & checkEmail() & checkPassword() & checkConfirm();
            if (!ok) {
                e.preventDefault();
            }
        });
    });
    </script>
</body>
</html>
