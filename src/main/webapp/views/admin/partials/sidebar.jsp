<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="uri" value="${pageContext.request.requestURI}" />
<c:set var="isHome" value="${activeMenu == 'home' or fn:endsWith(uri, '/home') or fn:endsWith(uri, '/home/')}" />
<c:set var="isJpaList" value="${activeMenu == 'jpa-list' or activeMenu == 'jpa' or fn:contains(uri, '/admin/categories') or (fn:contains(uri, '/admin/category/') and not fn:contains(uri, '/admin/category/add'))}" />
<c:set var="isJpaAdd" value="${activeMenu == 'jpa-add' or fn:contains(uri, '/admin/category/add')}" />
<c:set var="isJdbcList" value="${activeMenu == 'jdbc-list' or activeMenu == 'jdbc' or activeMenu == 'list' or fn:contains(uri, '/legacy/category/list') or fn:contains(uri, '/legacy/category/edit')}" />
<c:set var="isJdbcAdd" value="${activeMenu == 'jdbc-add' or activeMenu == 'add' or fn:contains(uri, '/legacy/category/add')}" />
<c:set var="isUsers" value="${activeMenu == 'users' or fn:contains(uri, '/admin/users')}" />
<c:set var="isMyAccount" value="${activeMenu == 'myaccount' or fn:contains(uri, '/member/myaccount')}" />

<aside class="admin-sidebar">
    <div class="sidebar-brand">
        <h2>Quản Trị Hệ Thống</h2>
        <p>Trường Đại học Công nghệ Kỹ thuật - Web Portal</p>
    </div>

    <div class="sidebar-user">
        <div class="sidebar-avatar">
            <c:out value="${sessionScope.account.userName.substring(0, 1)}"/>
        </div>
        <div class="sidebar-user-info">
            <strong><c:out value="${sessionScope.account.fullName}"/></strong>
            <span>
                <c:choose>
                    <c:when test="${sessionScope.account.roleid == 1}">Quản trị viên</c:when>
                    <c:when test="${sessionScope.account.roleid == 2}">Quản lý</c:when>
                    <c:otherwise>Thành viên</c:otherwise>
                </c:choose>
            </span>
        </div>
    </div>

    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/home"
           class="${isHome ? 'active' : ''}">Trang chủ</a>
        <c:choose>
            <c:when test="${sessionScope.account.roleid == 1 or sessionScope.account.roleid == 2}">
                <a href="${pageContext.request.contextPath}/admin/categories"
                   class="${isJpaList ? 'active' : ''}">Danh sách danh mục (JPA)</a>
                <a href="${pageContext.request.contextPath}/admin/category/add"
                   class="${isJpaAdd ? 'active' : ''}">+ Thêm danh mục (JPA)</a>
                <a href="${pageContext.request.contextPath}/legacy/category/list"
                   class="${isJdbcList ? 'active' : ''}">Danh sách danh mục (JDBC)</a>
                <a href="${pageContext.request.contextPath}/legacy/category/add"
                   class="${isJdbcAdd ? 'active' : ''}">+ Thêm danh mục (JDBC)</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/admin/categories"
                   class="${isJpaList ? 'active' : ''}">Xem danh mục (JPA)</a>
                <a href="${pageContext.request.contextPath}/legacy/category/list"
                   class="${isJdbcList ? 'active' : ''}">Xem danh mục (JDBC)</a>
            </c:otherwise>
        </c:choose>
        <c:if test="${sessionScope.account.roleid == 1}">
            <a href="${pageContext.request.contextPath}/admin/users"
               class="${isUsers ? 'active' : ''}">Tài khoản đăng nhập</a>
        </c:if>
        <a href="${pageContext.request.contextPath}/member/myaccount"
           class="${isMyAccount ? 'active' : ''}">Hồ sơ cá nhân</a>
        <a href="${pageContext.request.contextPath}/logout" class="nav-logout">Đăng xuất</a>
    </nav>
</aside>

<script>
    (function () {
        try {
            var currentPath = window.location.pathname;
            var navLinks = document.querySelectorAll('.sidebar-nav a:not(.nav-logout)');
            var matchedLink = null;
            
            navLinks.forEach(function (link) {
                var href = link.getAttribute('href');
                if (!href) return;
                var cleanHref = href.split('?')[0];
                
                if (currentPath === cleanHref) {
                    matchedLink = link;
                } else if (cleanHref.indexOf('/admin/categories') !== -1 && (currentPath.indexOf('/admin/categories') !== -1 || currentPath.indexOf('/admin/category/edit') !== -1)) {
                    matchedLink = link;
                } else if (cleanHref.indexOf('/legacy/category/list') !== -1 && (currentPath.indexOf('/legacy/category/list') !== -1 || currentPath.indexOf('/legacy/category/edit') !== -1)) {
                    matchedLink = link;
                } else if (cleanHref.indexOf('/admin/category/add') !== -1 && currentPath.indexOf('/admin/category/add') !== -1) {
                    matchedLink = link;
                } else if (cleanHref.indexOf('/legacy/category/add') !== -1 && currentPath.indexOf('/legacy/category/add') !== -1) {
                    matchedLink = link;
                } else if (cleanHref.indexOf('/admin/users') !== -1 && currentPath.indexOf('/admin/users') !== -1) {
                    matchedLink = link;
                } else if (cleanHref.indexOf('/member/myaccount') !== -1 && currentPath.indexOf('/member/myaccount') !== -1) {
                    matchedLink = link;
                } else if (cleanHref.endsWith('/home') && (currentPath.endsWith('/home') || currentPath.endsWith('/home/'))) {
                    matchedLink = link;
                }
            });
            
            if (matchedLink) {
                navLinks.forEach(function (l) { l.classList.remove('active'); });
                matchedLink.classList.add('active');
            }
        } catch (e) {
            // ignore
        }
    })();
</script>
