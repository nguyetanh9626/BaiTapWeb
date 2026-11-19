<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác Thực OTP Đặt Lại Mật Khẩu - Trường Đại học Công nghệ Kỹ thuật</title>
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
                <p>Nhập mã OTP 6 số đã được gửi tới email <strong><c:out value="${email}"/></strong></p>
            </div>

            <c:if test="${not empty infoMsg}">
                <div class="alert alert-success">
                    <c:out value="${infoMsg}"/>
                </div>
            </c:if>

            <c:if test="${not empty alert}">
                <div class="alert alert-danger">
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <form id="verifyResetOtpForm" action="${pageContext.request.contextPath}/verify-reset-otp" method="post" novalidate>
                <input type="hidden" name="email" value="<c:out value='${email}'/>" />

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
                           autofocus />
                    <div id="otpError" class="field-error-msg">Vui lòng nhập đúng 6 chữ số mã OTP!</div>
                    <small style="display:block; text-align:center; color:#1d4ed8; font-weight:600; margin-top: 8px; background: #eff6ff; padding: 8px 12px; border-radius: 6px; border: 1px solid #bfdbfe;">
                        Mã OTP xác thực 6 số đã được gửi tới email của bạn (Hiệu lực: 5 phút).
                    </small>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Tiếp Tục</button>
            </form>

            <div class="auth-footer" style="margin-top: 24px;">
                <a href="${pageContext.request.contextPath}/forgot-password">&larr; Nhập lại Email</a>
            </div>
        </div>
    </div>

    <script>
    document.addEventListener("DOMContentLoaded", function () {
        const form = document.getElementById("verifyResetOtpForm");
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
