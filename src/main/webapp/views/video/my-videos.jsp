<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="activeMenu" value="my-videos" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Video Của Tôi - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>

    <jsp:include page="/views/partials/topbar.jsp"/>

    <main class="page-container" style="max-width: 1200px; margin: 32px auto; padding: 0 20px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
            <div>
                <h1 style="font-size: 24px; color: #1e3a8a; margin: 0 0 6px 0;">Danh Sách Video Của Tôi</h1>
                <p style="color: #64748b; margin: 0; font-size: 14px;">Quản lý các bài giảng và video do bạn đăng tải (Tổng: ${totalItems} video)</p>
            </div>
            <a href="${pageContext.request.contextPath}/video/add" class="btn btn-primary">+ Đăng Tải Video Mới</a>
        </div>

        <c:if test="${not empty sessionScope.flash_success}">
            <div class="alert alert-success" style="margin-bottom: 20px;">
                <c:out value="${sessionScope.flash_success}"/>
            </div>
            <c:remove var="flash_success" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.flash_error}">
            <div class="alert alert-danger" style="margin-bottom: 20px;">
                <c:out value="${sessionScope.flash_error}"/>
            </div>
            <c:remove var="flash_error" scope="session"/>
        </c:if>

        <div class="panel">
            <c:choose>
                <c:when test="${empty videoList}">
                    <div style="text-align: center; padding: 48px 20px;">
                        <p style="color: #64748b; font-size: 16px; margin-bottom: 16px;">Bạn chưa tải lên video nào.</p>
                        <a href="${pageContext.request.contextPath}/video/add" class="btn btn-primary">+ Đăng video đầu tiên</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th style="width: 60px;">STT</th>
                                    <th style="width: 140px;">Hình ảnh</th>
                                    <th>Tiêu đề & Mô tả</th>
                                    <th style="width: 100px;">Lượt xem</th>
                                    <th style="width: 150px;">Ngày đăng</th>
                                    <th style="width: 120px;">Trạng thái</th>
                                    <th style="width: 160px; text-align: center;">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="v" items="${videoList}" varStatus="status">
                                    <tr>
                                        <td>${(currentPage - 1) * 6 + status.count}</td>
                                        <td>
                                            <img src="${pageContext.request.contextPath}/media/thumbnail?fname=${v.thumbnail}"
                                                 alt="<c:out value='${v.title}'/>"
                                                 style="width: 120px; height: 68px; object-fit: cover; border-radius: 6px; border: 1px solid #e2e8f0;"
                                                 onerror="this.src='${pageContext.request.contextPath}/assets/images/categories/default-category.png';" />
                                        </td>
                                        <td>
                                            <strong>
                                                <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" style="color: #1e293b; text-decoration: none;">
                                                    <c:out value="${v.title}"/>
                                                </a>
                                            </strong>
                                            <p style="color: #64748b; font-size: 13px; margin: 4px 0 0 0; line-height: 1.4;">
                                                <c:out value="${v.description}"/>
                                            </p>
                                        </td>
                                        <td>${v.views}</td>
                                        <td><fmt:formatDate value="${v.uploadDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${v.status == 1}">
                                                    <span class="badge" style="background: #ecfdf5; color: #047857; padding: 4px 8px; border-radius: 4px; font-size: 12px;">Hoạt động</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge" style="background: #fef2f2; color: #b91c1c; padding: 4px 8px; border-radius: 4px; font-size: 12px;">Tạm ẩn</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="text-align: center;">
                                            <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" class="btn btn-sm btn-secondary" style="padding: 4px 8px; font-size: 12px;">Xem</a>
                                            <a href="${pageContext.request.contextPath}/video/edit?id=${v.videoId}" class="btn btn-sm btn-secondary" style="padding: 4px 8px; font-size: 12px;">Sửa</a>
                                            <form action="${pageContext.request.contextPath}/video/delete" method="post" style="display: inline;"
                                                  onsubmit="return confirm('Bạn có chắc muốn xóa video này? Thao tác không thể hoàn tác.');">
                                                <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrf_token}'/>"/>
                                                <input type="hidden" name="id" value="${v.videoId}"/>
                                                <button type="submit" class="btn btn-sm btn-danger" style="padding: 4px 8px; font-size: 12px;">Xóa</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

</body>
</html>
