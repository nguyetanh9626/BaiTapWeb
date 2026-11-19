<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang Quản Lý - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/views/partials/topbar.jsp"/>

    <main class="page-container">
        <div class="card card-center">
            <h1>Khu Vực Quản Lý</h1>
            <p class="subtitle">Chào mừng quản lý quay trở lại hệ thống</p>

            <div class="info-box">
                <h3>Thông tin tài khoản quản lý</h3>
                <p><strong>Họ và tên:</strong> <c:out value="${sessionScope.account.fullName}"/></p>
                <p><strong>Tên đăng nhập:</strong> <c:out value="${sessionScope.account.userName}"/></p>
                <p><strong>Email:</strong> <c:out value="${sessionScope.account.email}"/></p>
                <p><strong>Vai trò:</strong> <span class="role-pill">Quản lý</span></p>
            </div>

            <div class="btn-row">
                <a href="${pageContext.request.contextPath}/video" class="btn btn-secondary">Kho Video</a>
                <a href="${pageContext.request.contextPath}/video/my-videos" class="btn btn-secondary">Video của tôi</a>
                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">Quản lý danh mục (JPA)</a>
                <a href="${pageContext.request.contextPath}/legacy/category/list" class="btn btn-secondary">Quản lý danh mục (JDBC)</a>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">Trang chủ</a>
                <a href="${pageContext.request.contextPath}/member/myaccount" class="btn btn-secondary">Hồ sơ cá nhân</a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Đăng xuất</a>
            </div>
        </div>
    </main>

</body>
</html>
