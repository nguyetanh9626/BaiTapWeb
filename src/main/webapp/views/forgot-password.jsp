<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên Mật Khẩu - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
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

            <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                <div class="form-group">
                    <label for="email">Địa chỉ Email</label>
                    <input type="email"
                           id="email"
                           name="email"
                           value="<c:out value='${email}'/>"
                           placeholder="name@example.com"
                           required
                           autofocus />
                </div>

                <button type="submit" class="btn btn-primary btn-block">Gửi Mã OTP Qua Email</button>
            </form>

            <div class="auth-footer" style="margin-top: 24px;">
                <a href="${pageContext.request.contextPath}/login">&larr; Quay lại Đăng nhập</a>
            </div>
        </div>
    </div>

</body>
</html>
