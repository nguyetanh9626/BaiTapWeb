<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<aside class="sidebar-web-nav card shadow-sm p-3 mb-3 border">
    <h6 class="fw-bold text-primary mb-3 pb-2 border-bottom">
        <i class="bi bi-compass me-2"></i> Danh Mục & Điều Hướng
    </h6>
    <div class="list-group list-group-flush small">
        <a href="${pageContext.request.contextPath}/home" class="list-group-item list-group-item-action d-flex align-items-center gap-2 py-2">
            <i class="bi bi-house-door text-primary"></i> Trang chủ
        </a>
        <a href="${pageContext.request.contextPath}/video" class="list-group-item list-group-item-action d-flex align-items-center gap-2 py-2">
            <i class="bi bi-collection-play text-info"></i> Kho Video Bài Giảng
        </a>
        <c:if test="${sessionScope.account != null}">
            <a href="${pageContext.request.contextPath}/video/my-videos" class="list-group-item list-group-item-action d-flex align-items-center gap-2 py-2">
                <i class="bi bi-person-video text-success"></i> Video của tôi
            </a>
            <a href="${pageContext.request.contextPath}/video/add" class="list-group-item list-group-item-action d-flex align-items-center gap-2 py-2">
                <i class="bi bi-cloud-arrow-up text-warning"></i> Đăng Video Mới
            </a>
            <a href="${pageContext.request.contextPath}/profile" class="list-group-item list-group-item-action d-flex align-items-center gap-2 py-2">
                <i class="bi bi-person-badge text-danger"></i> Hồ sơ cá nhân
            </a>
        </c:if>
    </div>
</aside>
