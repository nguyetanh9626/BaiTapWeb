<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang Quản Trị - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>

    <jsp:include page="/views/partials/topbar.jsp"/>

    <main class="page-container">
        <div class="card card-center">
            <h1>Khu Vực Quản Trị</h1>
            <p class="subtitle">Chào mừng quản trị viên quay trở lại hệ thống</p>

            <c:if test="${not empty alert}">
                <div class="alert alert-danger">
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <div class="info-box">
                <h3>Thông tin quản trị viên</h3>
                <p><strong>Họ và tên:</strong> <c:out value="${sessionScope.account.fullName}"/></p>
                <p><strong>Tên đăng nhập:</strong> <c:out value="${sessionScope.account.userName}"/></p>
                <p><strong>Email:</strong> <c:out value="${sessionScope.account.email}"/></p>
                <p><strong>Vai trò:</strong> <span class="role-pill">Quản trị viên</span></p>
            </div>

            <div class="btn-row">
                <a href="${pageContext.request.contextPath}/video" class="btn btn-secondary">Kho Video</a>
                <a href="${pageContext.request.contextPath}/video/my-videos" class="btn btn-secondary">Video của tôi</a>
                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">Quản lý danh mục (JPA)</a>
                <a href="${pageContext.request.contextPath}/legacy/category/list" class="btn btn-secondary">Quản lý danh mục (JDBC)</a>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Xem chi tiết tài khoản</a>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">Trang chủ</a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Đăng xuất</a>
            </div>
        </div>

        <div class="panel" style="margin-top: 24px;">
            <div class="panel-header">
                <h2>Các tài khoản có thể đăng nhập (${fn:length(userList)})</h2>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Tìm kiếm / Quản lý</a>
            </div>

            <div class="table-wrap">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th class="col-center" style="width: 65px;">STT</th>
                            <th>Tên đăng nhập</th>
                            <th>Họ và tên</th>
                            <th>Email</th>
                            <th>Vai trò</th>
                            <th>Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${userList}" var="u" varStatus="STT">
                            <tr>
                                <td class="col-center">${STT.index + 1}</td>
                                <td><strong><c:out value="${u.userName}"/></strong></td>
                                <td><c:out value="${u.fullName}"/></td>
                                <td><c:out value="${u.email}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${u.roleid == 1}">
                                            <span class="badge-role badge-admin">Quản trị viên</span>
                                        </c:when>
                                        <c:when test="${u.roleid == 2}">
                                            <span class="badge-role badge-manager">Quản lý</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-role badge-user">Thành viên</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><span class="badge-active">Có thể đăng nhập</span></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <footer class="site-footer">
        &copy; 2026 - Khoa Công Nghệ Thông Tin - Trường Đại học Công nghệ Kỹ thuật
    </footer>

</body>
</html>
