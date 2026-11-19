<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Lại Mật Khẩu Mới - Trường Đại học Công nghệ Kỹ thuật</title>
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
                <h2>Đặt Lại Mật Khẩu</h2>
                <p>Thiết lập mật khẩu mới an toàn cho tài khoản của bạn</p>
            </div>

            <c:if test="${not empty alert}">
                <div class="alert alert-danger">
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <form id="resetPwdForm" action="${pageContext.request.contextPath}/reset-password" method="post" novalidate>
                <div class="form-group text-start">
                    <label for="newPassword">Mật khẩu mới <span style="color:#dc2626;">*</span></label>
                    <input type="password"
                           id="newPassword"
                           name="newPassword"
                           placeholder="Tối thiểu 6 ký tự..."
                           minlength="6"
                           required
                           autofocus />
                    <div id="newPasswordError" class="field-error-msg">Mật khẩu mới phải có ít nhất 6 ký tự!</div>
                </div>

                <div class="form-group text-start">
                    <label for="confirmPassword">Nhập lại mật khẩu mới <span style="color:#dc2626;">*</span></label>
                    <input type="password"
                           id="confirmPassword"
                           name="confirmPassword"
                           placeholder="Xác nhận mật khẩu..."
                           minlength="6"
                           required />
                    <div id="confirmPasswordError" class="field-error-msg">Mật khẩu nhập lại không trùng khớp!</div>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Cập Nhật Mật Khẩu Mới</button>
            </form>

            <div class="auth-footer" style="margin-top: 24px;">
                <a href="${pageContext.request.contextPath}/login">&larr; Hủy & Quay lại Đăng nhập</a>
            </div>
        </div>
    </div>

    <script>
    document.addEventListener("DOMContentLoaded", function () {
        const form = document.getElementById("resetPwdForm");
        const np = document.getElementById("newPassword");
        const cp = document.getElementById("confirmPassword");
        const npErr = document.getElementById("newPasswordError");
        const cpErr = document.getElementById("confirmPasswordError");

        function checkNp() {
            const val = np.value;
            if (!val || val.length < 6) {
                npErr.style.display = "block";
                np.classList.add("input-error");
                return false;
            }
            npErr.style.display = "none";
            np.classList.remove("input-error");
            return true;
        }

        function checkCp() {
            const val = cp.value;
            if (val !== np.value || !val) {
                cpErr.style.display = "block";
                cp.classList.add("input-error");
                return false;
            }
            cpErr.style.display = "none";
            cp.classList.remove("input-error");
            return true;
        }

        np.addEventListener("input", checkNp);
        cp.addEventListener("input", checkCp);

        form.addEventListener("submit", function (e) {
            const ok = checkNp() & checkCp();
            if (!ok) {
                e.preventDefault();
            }
        });
    });
    </script>
</body>
</html>
