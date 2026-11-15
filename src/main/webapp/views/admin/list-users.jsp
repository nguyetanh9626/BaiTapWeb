<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeMenu" value="users" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tài Khoản Có Thể Đăng Nhập - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>

<div class="admin-layout">
    <jsp:include page="/views/admin/partials/sidebar.jsp"/>

    <div class="admin-main">
        <header class="admin-header">
            <h1>Danh Sách Tài Khoản Đăng Nhập</h1>
            <div class="admin-header-right">
                <span class="admin-header-user">
                    Xin chào, <strong><c:out value="${sessionScope.account.fullName}"/></strong>
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn-action btn-delete">Đăng xuất</a>
            </div>
        </header>

        <main class="admin-content">
            <div class="panel">
                <div class="panel-header">
                    <h2>Các tài khoản có thể đăng nhập hệ thống</h2>
                </div>

                <c:if test="${not empty alert}">
                    <div class="alert alert-danger">
                        <c:out value="${alert}"/>
                    </div>
                </c:if>

                <div class="toolbar">
                    <form class="search-form" method="get" action="${pageContext.request.contextPath}/admin/users">
                        <input type="text"
                               name="keyword"
                               class="search-input"
                               value="<c:out value='${keyword}'/>"
                               placeholder="Tìm theo username, họ tên hoặc email..." />
                        <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-neutral">Làm mới</a>
                    </form>
                </div>

                <div class="table-wrap">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th class="col-center" style="width: 65px;">STT</th>
                                <th>Tên đăng nhập</th>
                                <th>Họ và tên</th>
                                <th>Email</th>
                                <th>Số điện thoại</th>
                                <th>Vai trò</th>
                                <th>Ngày tạo</th>
                                <th>Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty userList}">
                                    <tr>
                                        <td colspan="8" class="empty-state">
                                            Không tìm thấy tài khoản nào phù hợp.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${userList}" var="u" varStatus="STT">
                                        <tr>
                                            <td class="col-center">${STT.index + 1}</td>
                                            <td><strong><c:out value="${u.userName}"/></strong></td>
                                            <td><c:out value="${u.fullName}"/></td>
                                            <td><c:out value="${u.email}"/></td>
                                            <td><c:out value="${u.phone}"/></td>
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
                                            <td><c:out value="${u.createdDate}"/></td>
                                            <td><span class="badge-active">Có thể đăng nhập</span></td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
    </div>
</div>

</body>
</html>
