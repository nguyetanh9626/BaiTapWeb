<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeMenu" value="jpa-list" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh Sửa Danh Mục (Edit Category) - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
<div class="admin-layout">
    <jsp:include page="/views/admin/partials/sidebar.jsp"/>

    <main class="admin-main">
        <header class="admin-topbar">
            <div>
                <h1>Chỉnh Sửa Danh Mục (Edit Category)</h1>
                <p>Cập nhật thông tin danh mục ID: <strong>${cate.categoryid}</strong></p>
            </div>
            <div class="topbar-actions">
                <a href="<c:url value='/admin/categories'/>" class="btn btn-secondary">← Quay lại danh sách</a>
            </div>
        </header>

        <section class="admin-content">
            <div class="card" style="max-width: 650px; margin: 0 auto; padding: 28px; background: #ffffff; border-radius: 12px; box-shadow: 0 4px 14px rgba(0,0,0,0.06);">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" style="margin-bottom: 20px; padding: 12px 16px; border-radius: 8px; background: #fef2f2; color: #991b1b; border: 1px solid #fecaca;">
                        ⚠ <c:out value="${error}"/>
                    </div>
                </c:if>

                <!-- Form sửa danh mục đúng theo mã nguồn giảng viên cung cấp -->
                <form action="<c:url value='/admin/category/update'/>" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="categoryid" value="${cate.categoryid}">

                    <div style="margin-bottom: 18px;">
                        <label for="categoryname" style="display: block; font-weight: 600; margin-bottom: 6px;">Category name:</label>
                        <input type="text" id="categoryname" name="categoryname" value="<c:out value='${cate.categoryname}'/>" required
                               style="width: 100%; padding: 10px 14px; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 14px;">
                    </div>

                    <div style="margin-bottom: 18px;">
                        <label for="images" style="display: block; font-weight: 600; margin-bottom: 6px;">Link images (URL https/http):</label>
                        <input type="text" id="images" name="images" value="<c:out value='${cate.images}'/>"
                               style="width: 100%; padding: 10px 14px; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 14px;">
                    </div>

                    <div style="margin-bottom: 18px;">
                        <label style="display: block; font-weight: 600; margin-bottom: 8px;">Hình ảnh hiện tại:</label>
                        <c:choose>
                            <c:when test="${not empty cate.images and (cate.images.startsWith('http://') or cate.images.startsWith('https://'))}">
                                <c:set var="imgUrl" value="${cate.images}"/>
                            </c:when>
                            <c:when test="${not empty cate.images}">
                                <c:url value="/image?fname=${cate.images}" var="imgUrl"/>
                            </c:when>
                            <c:otherwise>
                                <c:url value="/image?fname=default-category.png" var="imgUrl"/>
                            </c:otherwise>
                        </c:choose>
                        <div style="margin-bottom: 10px;">
                            <img height="150" width="200" src="${imgUrl}" alt="${cate.categoryname}"
                                 style="border-radius: 8px; border: 1px solid #e2e8f0; object-fit: cover;"/>
                        </div>

                        <label for="images1" style="display: block; font-weight: 600; margin-bottom: 6px;">Upload ảnh mới (nếu muốn thay đổi):</label>
                        <input type="file" id="images1" name="images1" accept="image/*"
                               style="width: 100%; padding: 8px 12px; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 14px;">
                    </div>

                    <div style="margin-bottom: 24px;">
                        <label style="display: block; font-weight: 600; margin-bottom: 8px;">Status:</label>
                        <div style="display: flex; gap: 24px; align-items: center;">
                            <label style="display: flex; align-items: center; gap: 6px; cursor: pointer;">
                                <input type="radio" id="ston" name="status" value="1" ${cate.status == 1 ? 'checked' : ''}>
                                <span>Hoạt động</span>
                            </label>
                            <label style="display: flex; align-items: center; gap: 6px; cursor: pointer;">
                                <input type="radio" id="stoff" name="status" value="0" ${cate.status != 1 ? 'checked' : ''}>
                                <span>Khóa</span>
                            </label>
                        </div>
                    </div>

                    <div style="display: flex; gap: 12px;">
                        <input type="submit" value="Update" class="btn btn-primary"
                               style="padding: 11px 24px; font-size: 15px; font-weight: 600; border-radius: 8px; cursor: pointer; background: #2563eb; color: #fff; border: none;">
                        <a href="<c:url value='/admin/categories'/>" class="btn btn-secondary"
                           style="padding: 11px 20px; font-size: 15px; border-radius: 8px; text-decoration: none; display: inline-block;">Hủy</a>
                    </div>
                </form>
            </div>
        </section>
    </main>
</div>
</body>
</html>
