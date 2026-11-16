<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Lại Mật Khẩu Mới - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
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

            <form action="${pageContext.request.contextPath}/reset-password" method="post">
                <div class="form-group">
                    <label for="newPassword">Mật khẩu mới</label>
                    <input type="password"
                           id="newPassword"
                           name="newPassword"
                           placeholder="Tối thiểu 6 ký tự..."
                           minlength="6"
                           required
                           autofocus />
                </div>

                <div class="form-group">
                    <label for="confirmPassword">Nhập lại mật khẩu mới</label>
                    <input type="password"
                           id="confirmPassword"
                           name="confirmPassword"
                           placeholder="Xác nhận mật khẩu..."
                           minlength="6"
                           required />
                </div>

                <button type="submit" class="btn btn-primary btn-block">Cập Nhật Mật Khẩu Mới</button>
            </form>

            <div class="auth-footer" style="margin-top: 24px;">
                <a href="${pageContext.request.contextPath}/login">&larr; Hủy & Quay lại Đăng nhập</a>
            </div>
        </div>
    </div>

</body>
</html>
