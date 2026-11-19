<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác Thực OTP Kích Hoạt - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .otp-input-field {
            font-size: 28px;
            letter-spacing: 12px;
            text-align: center;
            font-weight: bold;
            color: #1e3a8a;
            padding: 12px;
        }
        .resend-box {
            margin-top: 20px;
            padding-top: 16px;
            border-top: 1px dashed #cbd5e1;
            text-align: center;
            font-size: 14px;
        }
        .field-error-msg {
            color: #dc2626;
            font-size: 13px;
            margin-top: 4px;
            display: none;
            text-align: center;
        }
    </style>
</head>
<body class="auth-bg">

    <div class="auth-container">
        <div class="auth-card">
            <div class="auth-header">
                <h2>Xác Thực OTP</h2>
                <p>Nhập mã OTP 6 số để kích hoạt tài khoản <strong><c:out value="${username}"/></strong></p>
            </div>

            <c:if test="${not empty infoMsg}">
                <div class="alert alert-success">
                    <c:out value="${infoMsg}"/>
                </div>
            </c:if>

            <c:if test="${not empty errorMsg}">
                <div class="alert alert-danger">
                    <c:out value="${errorMsg}"/>
                </div>
            </c:if>

            <form id="verifyOtpForm" action="${pageContext.request.contextPath}/verify-otp" method="post" novalidate>
                <input type="hidden" name="username" value="<c:out value='${username}'/>" />

                <div class="form-group">
                    <label for="otp">Mã xác thực OTP (6 chữ số):</label>
                    <input type="text"
                           id="otp"
                           name="otp"
                           class="otp-input-field"
                           placeholder="------"
                           maxlength="6"
                           pattern="[0-9]{6}"
                           required
                           autocomplete="one-time-code"
                           autofocus />
                    <div id="otpError" class="field-error-msg">Vui lòng nhập đúng 6 chữ số mã OTP!</div>
                    <small style="display:block; text-align:center; color:#1d4ed8; font-weight:600; margin-top: 8px; background: #eff6ff; padding: 8px 12px; border-radius: 6px; border: 1px solid #bfdbfe;">
                        Mã OTP xác thực 6 số đã được gửi tới email của bạn (Hiệu lực: 5 phút).
                    </small>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Xác Nhận & Kích Hoạt</button>
            </form>

            <div class="resend-box">
                <span>Chưa nhận được mã OTP?</span><br/>
                <a href="${pageContext.request.contextPath}/resend-otp?username=${username}" style="color: #2563eb; font-weight: 600; text-decoration: none;">
                    &orarr; Gửi lại mã OTP qua Email
                </a>
            </div>

            <div class="auth-footer" style="margin-top: 20px;">
                <a href="${pageContext.request.contextPath}/login">&larr; Quay lại Đăng nhập</a>
            </div>
        </div>
    </div>

    <script>
    document.addEventListener("DOMContentLoaded", function () {
        const form = document.getElementById("verifyOtpForm");
        const otpInput = document.getElementById("otp");
        const otpError = document.getElementById("otpError");

        form.addEventListener("submit", function (e) {
            const val = otpInput.value.trim();
            if (!/^[0-9]{6}$/.test(val)) {
                otpError.style.display = "block";
                e.preventDefault();
            } else {
                otpError.style.display = "none";
            }
        });

        otpInput.addEventListener("input", function () {
            if (/^[0-9]{6}$/.test(this.value.trim())) {
                otpError.style.display = "none";
            }
        });
    });
    </script>
</body>
</html>
