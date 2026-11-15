<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang Chủ - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/views/partials/topbar.jsp"/>

    <main class="page-container">
        <div class="card card-center">
            <h1>Chào mừng đến với Hệ Thống Quản Lý Tài Khoản</h1>
            <p class="subtitle">Cổng đăng nhập và quản lý thông tin thành viên</p>

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

            <c:choose>
                <c:when test="${not empty sessionScope.account}">
                    <div class="info-box">
                        <h3>Thông tin tài khoản</h3>
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
                        <p><strong>Trạng thái:</strong> <span class="status-ok">Đang đăng nhập</span></p>
                    </div>

                    <div class="btn-row">
                        <c:choose>
                            <c:when test="${sessionScope.account.roleid == 1 or sessionScope.account.roleid == 2}">
                                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">Quản lý danh mục (JPA)</a>
                                <a href="${pageContext.request.contextPath}/legacy/category/list" class="btn btn-secondary">Quản lý danh mục (JDBC)</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">Xem danh mục (JPA)</a>
                                <a href="${pageContext.request.contextPath}/legacy/category/list" class="btn btn-secondary">Xem danh mục (JDBC)</a>
                            </c:otherwise>
                        </c:choose>
                        <a href="${pageContext.request.contextPath}/member/myaccount" class="btn btn-secondary">Hồ sơ cá nhân</a>
                        <c:if test="${sessionScope.account.roleid == 1}">
                            <a href="${pageContext.request.contextPath}/admin/home" class="btn btn-secondary">Trang quản trị</a>
                            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Tài khoản có thể đăng nhập</a>
                        </c:if>
                        <c:if test="${sessionScope.account.roleid == 2}">
                            <a href="${pageContext.request.contextPath}/manager/home" class="btn btn-secondary">Trang quản lý</a>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-neutral">Đăng xuất</a>
                        <a href="${pageContext.request.contextPath}/logout?clearCookie=true" class="btn btn-danger">Đăng xuất &amp; Quên tài khoản</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="subtitle">
                        Vui lòng đăng nhập hoặc tạo tài khoản mới để tiếp tục sử dụng hệ thống.
                    </p>
                    <div class="btn-row">
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Đăng nhập (Login)</a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-secondary">Đăng ký (Register)</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <footer class="site-footer">
        &copy; 2026 - Khoa Công Nghệ Thông Tin - Trường Đại học Công nghệ Kỹ thuật
    </footer>

</body>
</html>
