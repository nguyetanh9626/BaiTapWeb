<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ Sơ Cá Nhân - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/views/partials/topbar.jsp"/>

    <main class="page-container">
        <div class="card card-center">
            <h1>Hồ Sơ Cá Nhân</h1>
            <p class="subtitle">Thông tin chi tiết tài khoản của bạn</p>

            <div class="info-box">
                <h3>Chi tiết tài khoản</h3>
                <p><strong>Họ và tên:</strong> <c:out value="${sessionScope.account.fullName}"/></p>
                <p><strong>Tên đăng nhập:</strong> <c:out value="${sessionScope.account.userName}"/></p>
                <p><strong>Email:</strong> <c:out value="${sessionScope.account.email}"/></p>
                <p><strong>Số điện thoại:</strong> <c:out value="${sessionScope.account.phone}"/></p>
                <p><strong>Vai trò:</strong>
                    <span class="role-pill">
                        <c:choose>
                            <c:when test="${sessionScope.account.roleid == 1}">Quản trị viên</c:when>
                            <c:when test="${sessionScope.account.roleid == 2}">Quản lý</c:when>
                            <c:otherwise>Thành viên</c:otherwise>
                        </c:choose>
                    </span>
                </p>
                <p><strong>Ngày tạo:</strong> <c:out value="${sessionScope.account.createdDate}"/></p>
            </div>

            <div class="btn-row">
                <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">Về Trang chủ</a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Đăng xuất</a>
            </div>
        </div>
    </main>

    <footer class="site-footer">
        &copy; 2026 - Khoa Công Nghệ Thông Tin - Trường Đại học Công nghệ Kỹ thuật
    </footer>

</body>
</html>
