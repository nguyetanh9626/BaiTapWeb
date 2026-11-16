<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="topbar">
    <div class="brand">
        <h2>Trường Đại học Công nghệ Kỹ thuật</h2>
        <p>Hệ thống quản lý tài khoản</p>
    </div>
    <nav class="nav-links">
        <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/video">Kho Video</a>
        <c:if test="${sessionScope.account != null}">
            <a href="${pageContext.request.contextPath}/video/my-videos">Video của tôi</a>
            <a href="${pageContext.request.contextPath}/admin/categories">Danh mục (JPA)</a>
            <a href="${pageContext.request.contextPath}/legacy/category/list">Danh mục (JDBC)</a>
        </c:if>
        <c:choose>
            <c:when test="${sessionScope.account == null}">
                <a href="${pageContext.request.contextPath}/member/myaccount">Tài khoản</a>
                <a href="${pageContext.request.contextPath}/login" class="btn-nav-primary">Login</a>
                <a href="${pageContext.request.contextPath}/register">Register</a>
            </c:when>
            <c:otherwise>
                <span class="nav-greeting">
                    Xin chào, <strong><c:out value="${sessionScope.account.fullName}"/></strong>
                </span>
                <a href="${pageContext.request.contextPath}/member/myaccount">Tài khoản</a>
                <c:if test="${sessionScope.account.roleid == 1}">
                    <a href="${pageContext.request.contextPath}/admin/home">Quản trị</a>
                    <a href="${pageContext.request.contextPath}/admin/users">Tài khoản đăng nhập</a>
                </c:if>
                <c:if test="${sessionScope.account.roleid == 2}">
                    <a href="${pageContext.request.contextPath}/manager/home">Quản lý</a>
                </c:if>
                <a href="${pageContext.request.contextPath}/logout" class="btn-nav-danger">Đăng xuất</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>
