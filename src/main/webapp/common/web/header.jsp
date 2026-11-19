<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header>
    <%@ include file="/common/web/topbar.jsp" %>
    <%@ include file="/common/web/navbar.jsp" %>
</header>

<!-- Khu vực hiển thị Flash Message dùng chung toàn hệ thống -->
<c:if test="${not empty sessionScope.flashSuccess or not empty flashSuccess}">
    <div class="container mt-3">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>
            <c:out value="${not empty sessionScope.flashSuccess ? sessionScope.flashSuccess : flashSuccess}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </div>
    <c:remove var="flashSuccess" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashError or not empty flashError}">
    <div class="container mt-3">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <c:out value="${not empty sessionScope.flashError ? sessionScope.flashError : flashError}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </div>
    <c:remove var="flashError" scope="session"/>
</c:if>
