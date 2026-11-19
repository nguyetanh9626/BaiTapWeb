<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="uri" value="${pageContext.request.requestURI}" />
<c:set var="isHome" value="${activeMenu == 'home' or fn:endsWith(uri, '/home') or fn:endsWith(uri, '/home/')}" />
<c:set var="isJpaList" value="${activeMenu == 'jpa-list' or activeMenu == 'jpa' or fn:contains(uri, '/admin/categories') or (fn:contains(uri, '/admin/category/') and not fn:contains(uri, '/admin/category/add'))}" />
<c:set var="isJpaAdd" value="${activeMenu == 'jpa-add' or fn:contains(uri, '/admin/category/add')}" />
<c:set var="isJdbcList" value="${activeMenu == 'jdbc-list' or activeMenu == 'jdbc' or activeMenu == 'list' or fn:contains(uri, '/legacy/category/list') or fn:contains(uri, '/legacy/category/edit')}" />
<c:set var="isJdbcAdd" value="${activeMenu == 'jdbc-add' or activeMenu == 'add' or fn:contains(uri, '/legacy/category/add')}" />
<c:set var="isVideo" value="${activeMenu == 'video' or (fn:contains(uri, '/video') and not fn:contains(uri, '/video/add') and not fn:contains(uri, '/video/my-videos'))}" />
<c:set var="isMyVideo" value="${activeMenu == 'my-videos' or fn:contains(uri, '/video/my-videos')}" />
<c:set var="isVideoAdd" value="${activeMenu == 'video-add' or fn:contains(uri, '/video/add')}" />
<c:set var="isUsers" value="${activeMenu == 'users' or fn:contains(uri, '/admin/users')}" />
<c:set var="isMyAccount" value="${activeMenu == 'myaccount' or fn:contains(uri, '/member/myaccount')}" />

<aside class="admin-sidebar bg-white p-3 rounded shadow-sm border mb-3">
    <div class="sidebar-brand mb-3 pb-2 border-bottom">
        <h6 class="fw-bold text-primary mb-1"><i class="bi bi-gear-wide-connected me-1"></i> Quản Trị Hệ Thống</h6>
        <small class="text-muted">ĐH Công Nghệ Kỹ Thuật</small>
    </div>

    <div class="sidebar-user d-flex align-items-center gap-2 mb-3 pb-2 border-bottom">
        <div class="sidebar-avatar bg-primary text-white rounded-circle d-flex align-items-center justify-content-center" style="width: 38px; height: 38px; font-weight: bold; overflow: hidden;">
            <c:choose>
                <c:when test="${not empty sessionScope.account.avatar}">
                    <img src="<c:out value='${sessionScope.account.avatar}'/>" alt="Avatar" class="rounded-circle" style="width: 38px; height: 38px; object-fit: cover;" onerror="this.src='${pageContext.request.contextPath}/assets/images/default-avatar.png';"/>
                </c:when>
                <c:otherwise>
                    <c:out value="${sessionScope.account != null and not empty sessionScope.account.userName ? sessionScope.account.userName.substring(0, 1).toUpperCase() : 'A'}"/>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="sidebar-user-info small">
            <strong class="d-block text-truncate" style="max-width: 140px;"><c:out value="${sessionScope.account.fullName != null ? sessionScope.account.fullName : sessionScope.account.userName}"/></strong>
            <span class="badge bg-primary-subtle text-primary">
                <c:choose>
                    <c:when test="${sessionScope.account.roleid == 1}">Quản trị viên</c:when>
                    <c:when test="${sessionScope.account.roleid == 2}">Quản lý</c:when>
                    <c:otherwise>Thành viên</c:otherwise>
                </c:choose>
            </span>
        </div>
    </div>

    <nav class="sidebar-nav d-flex flex-column gap-1">
        <a href="${pageContext.request.contextPath}/admin/home" class="btn btn-sm text-start ${fn:contains(uri, '/admin/home') ? 'btn-primary' : 'btn-outline-secondary border-0'}">
            <i class="bi bi-speedometer2 me-2"></i> Bảng điều khiển
        </a>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-sm text-start ${isHome ? 'btn-primary' : 'btn-outline-secondary border-0'}">
            <i class="bi bi-house-door me-2"></i> Trang chủ
        </a>
        <a href="${pageContext.request.contextPath}/video" class="btn btn-sm text-start ${isVideo ? 'btn-primary' : 'btn-outline-secondary border-0'}">
            <i class="bi bi-collection-play me-2"></i> Kho Video
        </a>
        <a href="${pageContext.request.contextPath}/video/my-videos" class="btn btn-sm text-start ${isMyVideo ? 'btn-primary' : 'btn-outline-secondary border-0'}">
            <i class="bi bi-person-video me-2"></i> Video của tôi
        </a>
        <a href="${pageContext.request.contextPath}/video/add" class="btn btn-sm text-start ${isVideoAdd ? 'btn-primary' : 'btn-outline-secondary border-0'}">
            <i class="bi bi-cloud-arrow-up me-2"></i> Đăng Video
        </a>

        <div class="nav-section-title text-uppercase fw-bold text-muted small mt-2 mb-1 px-2" style="font-size: 11px;">
            Quản lý Danh mục (JPA)
        </div>
        <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-sm text-start ${isJpaList ? 'btn-primary' : 'btn-outline-secondary border-0'}">
            <i class="bi bi-tags me-2"></i> Danh sách Category (JPA)
        </a>
        <a href="${pageContext.request.contextPath}/admin/category/add" class="btn btn-sm text-start ${isJpaAdd ? 'btn-primary' : 'btn-outline-secondary border-0'}">
            <i class="bi bi-plus-circle me-2"></i> Thêm Category (JPA)
        </a>

        <div class="nav-section-title text-uppercase fw-bold text-muted small mt-2 mb-1 px-2" style="font-size: 11px;">
            Quản lý Danh mục (JDBC)
        </div>
        <a href="${pageContext.request.contextPath}/legacy/category/list" class="btn btn-sm text-start ${isJdbcList ? 'btn-primary' : 'btn-outline-secondary border-0'}">
            <i class="bi bi-database me-2"></i> Danh sách Category (JDBC)
        </a>
        <a href="${pageContext.request.contextPath}/legacy/category/add" class="btn btn-sm text-start ${isJdbcAdd ? 'btn-primary' : 'btn-outline-secondary border-0'}">
            <i class="bi bi-plus-square me-2"></i> Thêm Category (JDBC)
        </a>

        <div class="nav-section-title text-uppercase fw-bold text-muted small mt-2 mb-1 px-2" style="font-size: 11px;">
            Tài khoản
        </div>
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-sm text-start ${isUsers ? 'btn-primary' : 'btn-outline-secondary border-0'}">
            <i class="bi bi-people me-2"></i> Quản lý Tài khoản
        </a>
        <a href="${pageContext.request.contextPath}/profile" class="btn btn-sm text-start btn-outline-secondary border-0">
            <i class="bi bi-person-badge me-2"></i> Hồ sơ cá nhân (Profile)
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm text-start text-danger btn-outline-danger border-0 mt-2">
            <i class="bi bi-box-arrow-right me-2"></i> Đăng xuất
        </a>
    </nav>
</aside>
