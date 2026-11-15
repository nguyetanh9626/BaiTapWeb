<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Nhập Thất Bại - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <div class="auth-container">
        <div class="error-card">
            <div class="error-icon">&times;</div>
            <h1>Đăng Nhập Thất Bại!</h1>

            <div class="error-message">
                <c:choose>
                    <c:when test="${not empty sessionScope.errorMessage}">
                        <c:out value="${sessionScope.errorMessage}" />
                    </c:when>
                    <c:otherwise>
                        Tên đăng nhập hoặc mật khẩu không chính xác.
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="btn-row">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-danger">Thử đăng nhập lại</a>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-neutral">Về Trang chủ</a>
            </div>
        </div>
    </div>

</body>
</html>
