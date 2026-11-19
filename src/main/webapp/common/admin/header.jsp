<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="navbar navbar-dark bg-dark px-3 py-2 shadow-sm border-bottom border-secondary" style="background: linear-gradient(135deg, #111827 0%, #1f2937 100%) !important;">
    <div class="container-fluid d-flex justify-content-between align-items-center">
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold text-white text-decoration-none" href="${pageContext.request.contextPath}/admin/home">
            <i class="bi bi-shield-lock-fill text-warning fs-4"></i>
            <span>Baitapweb - Admin Portal</span>
        </a>
        <div class="d-flex align-items-center gap-3">
            <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-light btn-sm">
                <i class="bi bi-globe me-1"></i> Về Website
            </a>
            <span class="text-light small d-none d-md-inline">
                Xin chào, <strong><c:out value="${sessionScope.account.fullName != null ? sessionScope.account.fullName : sessionScope.account.userName}"/></strong>
            </span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm">
                <i class="bi bi-box-arrow-right me-1"></i> Đăng xuất
            </a>
        </div>
    </div>
</header>
