<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeMenu" value="jdbc-list" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cập Nhật Danh Mục - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>

<div class="admin-layout">
    <jsp:include page="/views/admin/partials/sidebar.jsp"/>

    <div class="admin-main">
        <header class="admin-header">
            <h1>Chỉnh Sửa Danh Mục</h1>
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
                    <h2>Cập nhật thông tin danh mục</h2>
                </div>

                <c:if test="${not empty alert}">
                    <div class="alert alert-danger">
                        <c:out value="${alert}"/>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/legacy/category/edit"
                      method="post"
                      enctype="multipart/form-data">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                    <input type="hidden" name="cateid" value="${category.cateid}" />

                    <div class="form-group">
                        <label for="cateidDisplay">Mã danh mục</label>
                        <input type="text"
                               id="cateidDisplay"
                               class="readonly-input"
                               value="#${category.cateid}"
                               readonly />
                    </div>

                    <div class="form-group">
                        <label for="catename">Tên danh mục</label>
                        <input type="text"
                               id="catename"
                               name="catename"
                               value="<c:out value='${category.catename}'/>"
                               placeholder="Nhập tên danh mục..."
                               required />
                    </div>

                    <div class="form-group">
                        <label>Ảnh hiện tại</label>
                        <c:choose>
                            <c:when test="${not empty category.icon}">
                                <c:url value="/image" var="imgUrl">
                                    <c:param name="fname" value="${category.icon}"/>
                                </c:url>
                                <img class="category-preview"
                                     src="${imgUrl}"
                                     alt="<c:out value='${category.catename}'/>"/>
                            </c:when>
                            <c:otherwise>
                                <img class="category-preview"
                                     src="${pageContext.request.contextPath}/image?fname=default-category.png"
                                     alt="Ảnh mặc định"/>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="form-group">
                        <label for="icon">Chọn ảnh mới</label>
                        <input type="file"
                               id="icon"
                               name="icon"
                               class="file-input"
                               accept="image/png,image/jpeg,image/gif,image/webp" />
                        <span class="form-hint">Để trống nếu muốn giữ nguyên ảnh hiện tại.</span>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Cập nhật</button>
                        <a href="${pageContext.request.contextPath}/legacy/category/list" class="btn btn-neutral">Hủy</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>

</body>
</html>
