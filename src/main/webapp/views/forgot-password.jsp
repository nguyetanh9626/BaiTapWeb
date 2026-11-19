<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên Mật Khẩu - Trường Đại học Công nghệ Kỹ thuật</title>
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
        <div class="auth-card">
            <div class="auth-header">
                <h2>Quên Mật Khẩu</h2>
                <p>Nhập địa chỉ email đã đăng ký để nhận mã OTP đặt lại mật khẩu</p>
            </div>

            <c:if test="${not empty alert}">
                <div class="alert alert-danger">
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <form id="forgotForm" action="${pageContext.request.contextPath}/forgot-password" method="post" novalidate>
                <div class="form-group text-start">
                    <label for="email">Địa chỉ Email đã đăng ký <span style="color:#dc2626;">*</span></label>
                    <input type="email"
                           id="email"
                           name="email"
                           value="<c:out value='${email}'/>"
                           placeholder="name@example.com"
                           required
                           autofocus />
                    <div id="emailError" class="field-error-msg">Vui lòng nhập địa chỉ email hợp lệ!</div>
                </div>

                <button type="submit" id="btnSubmitForgot" class="btn btn-primary btn-block">Gửi Mã OTP Qua Email</button>
            </form>

            <div class="auth-footer" style="margin-top: 24px;">
                <a href="${pageContext.request.contextPath}/login">&larr; Quay lại Đăng nhập</a>
            </div>
        </div>
    </div>

    <script>
    document.addEventListener("DOMContentLoaded", function () {
        const form = document.getElementById("forgotForm");
        const emInput = document.getElementById("email");
        const emErr = document.getElementById("emailError");
        const btn = document.getElementById("btnSubmitForgot");

        function checkEmail() {
            const val = emInput.value.trim();
            const re = /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$/;
            if (!val) {
                emErr.textContent = "Địa chỉ email không được để trống!";
                emErr.style.display = "block";
                emInput.classList.add("input-error");
                return false;
            }
            if (!re.test(val)) {
                emErr.textContent = "Địa chỉ email không đúng định dạng!";
                emErr.style.display = "block";
                emInput.classList.add("input-error");
                return false;
            }
            emErr.style.display = "none";
            emInput.classList.remove("input-error");
            return true;
        }

        emInput.addEventListener("input", checkEmail);

        form.addEventListener("submit", function (e) {
            if (!checkEmail()) {
                e.preventDefault();
                return;
            }
            btn.disabled = true;
            btn.textContent = "Đang gửi OTP...";
            form.submit();
        });
    });
    </script>
</body>
</html>
