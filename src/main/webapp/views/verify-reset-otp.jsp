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

            <form action="${pageContext.request.contextPath}/verify-reset-otp" method="post">
                <input type="hidden" name="email" value="<c:out value='${email}'/>" />

                <div class="form-group">
                    <label for="otp">Mã xác thực OTP (6 chữ số):</label>
                    <input type="text"
                           id="otp"
                           name="otp"
                           class="otp-input-field"
                           placeholder="------"
                           maxlength="6"
                           required
                           autofocus />
                    <small style="display:block; text-align:center; color:#64748b; margin-top: 6px;">
                        Mã OTP có hiệu lực trong 5 phút. Vui lòng kiểm tra hộp thư email.
                    </small>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Tiếp Tục</button>
            </form>

            <div class="auth-footer" style="margin-top: 24px;">
                <a href="${pageContext.request.contextPath}/forgot-password">&larr; Nhập lại Email</a>
            </div>
        </div>
    </div>

</body>
</html>
