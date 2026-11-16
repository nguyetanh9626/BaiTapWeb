<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeMenu" value="jdbc-add" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm Danh Mục - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>

<div class="admin-layout">
    <jsp:include page="/views/admin/partials/sidebar.jsp"/>

    <div class="admin-main">
        <header class="admin-header">
            <h1>Thêm Danh Mục Mới</h1>
            <div class="admin-header-right">
                <span class="admin-header-user">
                    Xin chào, <strong><c:out value="${sessionScope.account.fullName}"/></strong>
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn-action btn-delete">Đăng xuất</a>
            </div>
        </header>

        <main class="admin-content">
            <div class="panel form-panel">
                <div class="panel-header">
                    <h2>Thông tin danh mục</h2>
                </div>

                <c:if test="${not empty alert}">
                    <div class="alert alert-danger">
                        <c:out value="${alert}"/>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/legacy/category/add"
                      method="post"
                      enctype="multipart/form-data">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                    <div class="form-group">
                        <label for="catename">Tên danh mục</label>
                        <input type="text"
                               id="catename"
                               name="catename"
                               value="<c:out value='${valCatename}'/>"
                               placeholder="Nhập tên danh mục..."
                               required
                               autofocus />
                    </div>

                    <div class="form-group">
                        <label for="icon">Ảnh đại diện</label>
                        <input type="file"
                               id="icon"
                               name="icon"
                               class="file-input"
                               accept="image/png,image/jpeg,image/gif,image/webp" />
                        <span class="form-hint">Hỗ trợ ảnh PNG, JPG, GIF, WEBP (tối đa 5MB).</span>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Thêm</button>
                        <a href="${pageContext.request.contextPath}/legacy/category/list" class="btn btn-neutral">Hủy</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>

</body>
</html>
