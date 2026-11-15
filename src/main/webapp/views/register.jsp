<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Ký Tài Khoản - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="auth-bg">

    <div class="auth-container">
        <div class="auth-card wide">
            <div class="auth-header">
                <h2>Đăng Ký Tài Khoản</h2>
                <p>Điền đầy đủ thông tin bên dưới để tạo tài khoản mới</p>
            </div>

            <c:if test="${not empty alert}">
                <div class="alert alert-danger">
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="post">
                <div class="form-group">
                    <label for="fullname">Họ và tên</label>
                    <input type="text"
                           id="fullname"
                           name="fullname"
                           value="<c:out value='${valFullname}'/>"
                           placeholder="Nhập họ và tên..."
                           required />
                </div>

                <div class="form-grid">
                    <div class="form-group">
                        <label for="username">Tên đăng nhập</label>
                        <input type="text"
                               id="username"
                               name="username"
                               value="<c:out value='${valUsername}'/>"
                               placeholder="Nhập tên đăng nhập..."
                               required />
                    </div>

                    <div class="form-group">
                        <label for="phone">Số điện thoại</label>
                        <input type="tel"
                               id="phone"
                               name="phone"
                               value="<c:out value='${valPhone}'/>"
                               placeholder="VD: 0901234567"
                               required />
                    </div>
                </div>

                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email"
                           id="email"
                           name="email"
                           value="<c:out value='${valEmail}'/>"
                           placeholder="example@cnkt.edu.vn"
                           required />
                </div>

                <div class="form-grid">
                    <div class="form-group">
                        <label for="password">Mật khẩu</label>
                        <input type="password"
                               id="password"
                               name="password"
                               placeholder="Nhập mật khẩu..."
                               required />
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Nhập lại mật khẩu</label>
                        <input type="password"
                               id="confirmPassword"
                               name="confirmPassword"
                               placeholder="Xác nhận mật khẩu..."
                               required />
                    </div>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Đăng ký</button>
            </form>

            <div class="auth-footer">
                <span>Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập ngay</a></span>
                <a href="${pageContext.request.contextPath}/home">&larr; Quay lại Trang chủ</a>
            </div>
        </div>
    </div>

</body>
</html>
