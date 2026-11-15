<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Nhập - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="auth-bg">

    <div class="auth-container">
        <div class="auth-card">
            <div class="auth-header">
                <h2>Đăng Nhập</h2>
                <p>Nhập tài khoản và mật khẩu để tiếp tục</p>
            </div>

            <c:if test="${not empty successMsg}">
                <div class="alert alert-success">
                    <c:out value="${successMsg}"/>
                </div>
            </c:if>

            <c:if test="${not empty alert}">
                <div class="alert alert-danger">
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <c:if test="${fromCookie and not empty rememberedUsername}">
                <div class="cookie-badge">
                    <span>Đã ghi nhớ tài khoản: <strong><c:out value="${rememberedUsername}"/></strong></span>
                    <a href="${pageContext.request.contextPath}/forget-cookie">Xóa ghi nhớ</a>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label for="username">Tên đăng nhập</label>
                    <input type="text"
                           id="username"
                           name="username"
                           value="<c:out value='${rememberedUsername}'/>"
                           placeholder="Nhập tên đăng nhập..."
                           required
                           autofocus />
                </div>

                <div class="form-group">
                    <label for="password">Mật khẩu</label>
                    <input type="password"
                           id="password"
                           name="password"
                           placeholder="Nhập mật khẩu..."
                           required />
                </div>

                <div class="form-options">
                    <label class="checkbox-label">
                        <input type="checkbox"
                               id="remember"
                               name="remember"
                               <c:if test="${rememberChecked}">checked</c:if> />
                        <span>Remember Me</span>
                    </label>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Login</button>
            </form>

            <div class="demo-accounts">
                <strong>Tài khoản mẫu:</strong><br/>
                &bull; Admin: <code>admin</code> / <code>123456</code><br/>
                &bull; Manager: <code>Nganh</code> / <code>123</code><br/>
                &bull; User: <code>student</code> / <code>123</code>
            </div>

            <div class="auth-footer">
                <span>Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký ngay</a></span>
                <a href="${pageContext.request.contextPath}/home">&larr; Quay lại Trang chủ</a>
            </div>
        </div>
    </div>

</body>
</html>
