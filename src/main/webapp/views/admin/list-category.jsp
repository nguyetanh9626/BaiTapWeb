<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeMenu" value="jdbc-list" scope="request"/>
<c:set var="canManageCategory" value="${sessionScope.account.roleid == 1 or sessionScope.account.roleid == 2}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Danh Mục - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>

<div class="admin-layout">
    <jsp:include page="/views/admin/partials/sidebar.jsp"/>

    <div class="admin-main">
        <header class="admin-header">
            <h1>Quản Lý Danh Mục</h1>
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
                    <h2>Danh sách danh mục (JDBC)</h2>
                    <c:if test="${canManageCategory}">
                        <a href="${pageContext.request.contextPath}/legacy/category/add" class="btn btn-primary">
                            + Thêm danh mục
                        </a>
                    </c:if>
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

                <c:if test="${not canManageCategory}">
                    <div class="alert alert-info">
                        Bạn đang đăng nhập với quyền Thành viên (chỉ Admin và Quản lý mới có quyền thêm, sửa, xóa danh mục).
                    </div>
                </c:if>

                <div class="toolbar">
                    <form class="search-form" method="get" action="${pageContext.request.contextPath}/legacy/category/list">
                        <input type="text"
                               name="keyword"
                               class="search-input"
                               value="<c:out value='${keyword}'/>"
                               placeholder="Nhập tên danh mục cần tìm..." />
                        <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                        <a href="${pageContext.request.contextPath}/legacy/category/list" class="btn btn-neutral">Làm mới</a>
                    </form>
                </div>

                <div class="table-wrap">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th class="col-center" style="width: 70px;">STT</th>
                                <th class="col-center" style="width: 120px;">Mã danh mục</th>
                                <th style="width: 120px;">Hình ảnh</th>
                                <th>Tên danh mục</th>
                                <c:if test="${canManageCategory}">
                                    <th style="width: 190px;">Thao tác</th>
                                </c:if>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty cateList}">
                                    <tr>
                                        <td colspan="${canManageCategory ? 5 : 4}" class="empty-state">
                                            Không có danh mục nào phù hợp.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${cateList}" var="cate" varStatus="STT">
                                        <tr>
                                            <td class="col-center">${STT.index + 1}</td>
                                            <td class="col-center">
                                                <span class="cate-id-badge">#${cate.cateid}</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty cate.icon}">
                                                        <c:url value="/image" var="imgUrl">
                                                            <c:param name="fname" value="${cate.icon}"/>
                                                        </c:url>
                                                        <img class="category-thumb"
                                                             src="${imgUrl}"
                                                             alt="<c:out value='${cate.catename}'/>"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img class="category-thumb"
                                                             src="${pageContext.request.contextPath}/image?fname=default-category.png"
                                                             alt="Ảnh mặc định"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <strong><c:out value="${cate.catename}"/></strong>
                                            </td>
                                            <c:if test="${canManageCategory}">
                                                <td>
                                                    <div class="actions-cell">
                                                        <a href="${pageContext.request.contextPath}/legacy/category/edit?id=${cate.cateid}"
                                                           class="btn-action btn-edit">Sửa</a>

                                                        <form method="post"
                                                              action="${pageContext.request.contextPath}/legacy/category/delete"
                                                              class="inline-delete-form"
                                                              onsubmit="return confirm('Bạn có chắc muốn xóa danh mục này?');">
                                                            <input type="hidden" name="id" value="${cate.cateid}"/>
                                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                                            <button type="submit" class="btn-action btn-delete">Xóa</button>
                                                        </form>
                                                    </div>
                                                </td>
                                            </c:if>
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
