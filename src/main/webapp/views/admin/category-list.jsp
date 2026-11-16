<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeMenu" value="jpa-list" scope="request"/>
<c:set var="canManageCategory" value="${sessionScope.account.roleid == 1 or sessionScope.account.roleid == 2}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Danh Mục (JPA/Hibernate) - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
    <style>
        .category-table img {
            border-radius: 8px;
            object-fit: cover;
            border: 1px solid #e2e8f0;
            background: #f8fafc;
        }
        .badge-active {
            background-color: #dcfce7;
            color: #166534;
            padding: 4px 10px;
            border-radius: 9999px;
            font-size: 13px;
            font-weight: 600;
            display: inline-block;
        }
        .badge-locked {
            background-color: #fee2e2;
            color: #991b1b;
            padding: 4px 10px;
            border-radius: 9999px;
            font-size: 13px;
            font-weight: 600;
            display: inline-block;
        }
    </style>
</head>
<body class="admin-body">
<div class="admin-layout">
    <jsp:include page="/views/admin/partials/sidebar.jsp"/>

    <main class="admin-main">
        <header class="admin-topbar">
            <div>
                <h1>Quản Lý Danh Mục (JPA & Hibernate)</h1>
                <p>Cơ sở dữ liệu: <strong>webst2</strong> | Bảng: <strong>categories</strong></p>
            </div>
            <div class="topbar-actions">
                <c:if test="${canManageCategory}">
                    <a href="<c:url value='/admin/category/add'/>" class="btn btn-primary">+ Thêm Danh Mục Mới (Add Category)</a>
                </c:if>
            </div>
        </header>

        <section class="admin-content">
            <!-- Thông báo flash -->
            <c:if test="${not empty sessionScope.message_success}">
                <div class="alert alert-success" style="margin-bottom: 18px; padding: 12px 16px; border-radius: 8px; background: #ecfdf5; color: #065f46; border: 1px solid #a7f3d0;">
                    ✓ <c:out value="${sessionScope.message_success}"/>
                </div>
                <c:remove var="message_success" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.message_error}">
                <div class="alert alert-danger" style="margin-bottom: 18px; padding: 12px 16px; border-radius: 8px; background: #fef2f2; color: #991b1b; border: 1px solid #fecaca;">
                    ⚠ <c:out value="${sessionScope.message_error}"/>
                </div>
                <c:remove var="message_error" scope="session"/>
            </c:if>

            <c:if test="${not canManageCategory}">
                <div class="alert alert-info" style="margin-bottom: 18px; padding: 12px 16px; border-radius: 8px; background: #eff6ff; color: #1e40af; border: 1px solid #bfdbfe;">
                    ℹ Bạn đang xem danh sách với quyền Thành viên (chế độ chỉ đọc, không có quyền thêm, sửa hoặc xóa).
                </div>
            </c:if>

            <!-- Thanh tìm kiếm danh mục -->
            <div class="table-card" style="margin-bottom: 20px; padding: 16px;">
                <form action="<c:url value='/admin/categories'/>" method="get" style="display: flex; gap: 12px; align-items: center;">
                    <input type="text" name="keyword" value="<c:out value='${keyword}'/>"
                           placeholder="Nhập tên danh mục cần tìm kiếm..."
                           class="form-control" style="flex: 1; padding: 10px 14px; border: 1px solid #cbd5e1; border-radius: 8px;">
                    <button type="submit" class="btn btn-primary" style="padding: 10px 20px;">Tìm kiếm</button>
                    <c:if test="${not empty keyword}">
                        <a href="<c:url value='/admin/categories'/>" class="btn btn-secondary" style="padding: 10px 16px;">Xóa lọc</a>
                    </c:if>
                </form>
            </div>

            <!-- Bảng hiển thị danh mục theo đúng cấu trúc giảng viên yêu cầu -->
            <div class="table-card">
                <table class="category-table" border="1" width="100%" style="width: 100%; border-collapse: collapse; text-align: left;">
                    <thead>
                    <tr style="background: #f8fafc; border-bottom: 2px solid #e2e8f0;">
                        <th style="padding: 12px 16px; width: 60px; text-align: center;">STT</th>
                        <th style="padding: 12px 16px; width: 220px; text-align: center;">Images</th>
                        <th style="padding: 12px 16px;">Category name</th>
                        <th style="padding: 12px 16px; width: 140px; text-align: center;">Status</th>
                        <c:if test="${canManageCategory}">
                            <th style="padding: 12px 16px; width: 160px; text-align: center;">Action</th>
                        </c:if>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty listcate}">
                            <c:forEach items="${listcate}" var="cate" varStatus="STT">
                                <tr style="border-bottom: 1px solid #f1f5f9;">
                                    <td style="padding: 12px 16px; text-align: center; font-weight: 600;">${STT.index + 1}</td>

                                    <!-- Xử lý URL hình ảnh: link https trực tiếp hoặc ảnh nội bộ qua /image?fname=... -->
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

                                    <td style="padding: 12px 16px; text-align: center;">
                                        <img height="150" width="200" src="${imgUrl}" alt="${cate.categoryname}" />
                                    </td>
                                    <td style="padding: 12px 16px; font-weight: 600; font-size: 15px;">
                                        <c:out value="${cate.categoryname}"/>
                                        <div style="font-size: 12px; color: #64748b; font-weight: normal; margin-top: 4px;">ID: ${cate.categoryid}</div>
                                    </td>
                                    <td style="padding: 12px 16px; text-align: center;">
                                        <c:if test="${cate.status == 1}">
                                            <span class="badge-active">Hoạt động</span>
                                        </c:if>
                                        <c:if test="${cate.status != 1}">
                                            <span class="badge-locked">Khóa</span>
                                        </c:if>
                                    </td>
                                    <c:if test="${canManageCategory}">
                                        <td style="padding: 12px 16px; text-align: center;">
                                            <div style="display: inline-flex; align-items: center; gap: 8px; justify-content: center;">
                                                <a href="<c:url value='/admin/category/edit?id=${cate.categoryid}'/>"
                                                   class="btn btn-sm btn-outline-primary" style="padding: 6px 12px; text-decoration: none;">Sửa</a>
                                                <form action="<c:url value='/admin/category/delete'/>" method="post" style="display: inline; margin: 0;"
                                                      onsubmit="return confirm('Bạn có chắc chắn muốn xóa danh mục &quot;${cate.categoryname}&quot; (ID: ${cate.categoryid})?');">
                                                    <input type="hidden" name="id" value="${cate.categoryid}"/>
                                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                                    <button type="submit" class="btn btn-sm btn-outline-danger"
                                                            style="padding: 6px 12px; color: #dc2626; background: none; border: 1px solid #fca5a5; border-radius: 6px; cursor: pointer; font-size: 13px;">
                                                        Xóa
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="${canManageCategory ? 5 : 4}" style="padding: 40px; text-align: center; color: #64748b;">
                                    Chưa có danh mục nào trong cơ sở dữ liệu. Nhấn <strong>Thêm Danh Mục Mới</strong> để tạo!
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</div>
</body>
</html>
