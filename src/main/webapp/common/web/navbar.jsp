<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm" style="background: linear-gradient(135deg, #0f2f6d 0%, #1d4ed8 100%) !important;">
    <div class="container-fluid px-lg-4">
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold text-white text-decoration-none" href="${pageContext.request.contextPath}/home">
            <div class="rounded-3 bg-white bg-opacity-25 d-flex align-items-center justify-content-center" style="width: 38px; height: 38px;">
                <i class="bi bi-play-btn-fill fs-5 text-white"></i>
            </div>
            <div>
                <span class="fs-6 text-uppercase d-block" style="line-height: 1.2;">ĐH Công Nghệ Kỹ Thuật</span>
                <span class="small fw-normal text-white-50" style="font-size: 11px;">Cổng Kho Video Trực Tuyến</span>
            </div>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain"
                aria-controls="navbarMain" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarMain">
            <!-- Menu bên trái -->
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3">
                <li class="nav-item">
                    <a class="nav-link px-3 text-white" href="${pageContext.request.contextPath}/home">
                        <i class="bi bi-house-door me-1"></i> Trang chủ
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link px-3 text-white" href="${pageContext.request.contextPath}/video">
                        <i class="bi bi-collection-play me-1"></i> Kho Video
                    </a>
                </li>
                <c:if test="${sessionScope.account != null}">
                    <li class="nav-item">
                        <a class="nav-link px-3 text-white" href="${pageContext.request.contextPath}/video/my-videos">
                            <i class="bi bi-person-video me-1"></i> Video của tôi
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link px-3 text-white" href="${pageContext.request.contextPath}/video/add">
                            <i class="bi bi-cloud-arrow-up me-1"></i> Đăng Video
                        </a>
                    </li>

                    <!-- Menu dành cho Quản trị viên (roleid = 1) -->
                    <c:if test="${sessionScope.account.roleid == 1}">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle text-white px-3" href="#" id="adminDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="bi bi-gear-fill me-1"></i> Quản trị
                            </a>
                            <ul class="dropdown-menu shadow" aria-labelledby="adminDropdown">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/home"><i class="bi bi-speedometer2 me-2"></i>Bảng điều khiển Admin</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/users"><i class="bi bi-people me-2"></i>Quản lý Tài khoản</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/categories"><i class="bi bi-tags me-2"></i>Quản lý Danh mục (JPA)</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/legacy/category/list"><i class="bi bi-database me-2"></i>Danh mục Cũ (JDBC)</a></li>
                            </ul>
                        </li>
                    </c:if>

                    <!-- Menu dành cho Quản lý (roleid = 2) -->
                    <c:if test="${sessionScope.account.roleid == 2}">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle text-white px-3" href="#" id="managerDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="bi bi-kanban me-1"></i> Quản lý
                            </a>
                            <ul class="dropdown-menu shadow" aria-labelledby="managerDropdown">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/manager/home"><i class="bi bi-speedometer2 me-2"></i>Trang Quản lý</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/categories"><i class="bi bi-tags me-2"></i>Quản lý Danh mục</a></li>
                            </ul>
                        </li>
                    </c:if>
                </c:if>
            </ul>

            <!-- Khu vực Tài khoản bên phải -->
            <ul class="navbar-nav ms-auto align-items-lg-center">
                <c:choose>
                    <c:when test="${sessionScope.account == null}">
                        <li class="nav-item me-2 mb-2 mb-lg-0">
                            <a class="btn btn-outline-light btn-sm px-3" href="${pageContext.request.contextPath}/login">
                                <i class="bi bi-box-arrow-in-right me-1"></i> Đăng nhập
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="btn btn-light btn-sm px-3 text-primary fw-bold" href="${pageContext.request.contextPath}/register">
                                <i class="bi bi-person-plus me-1"></i> Đăng ký
                            </a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle d-flex align-items-center text-white py-1" href="#" id="userMenuDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.account.avatar}">
                                        <img src="<c:out value='${sessionScope.account.avatar}'/>"
                                             alt="Avatar" class="user-nav-avatar me-2 rounded-circle"
                                             style="width: 34px; height: 34px; object-fit: cover; border: 2px solid #ffffff;"
                                             onerror="this.src='${pageContext.request.contextPath}/assets/images/default-avatar.png';"/>
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/assets/images/default-avatar.png"
                                             alt="Default Avatar" class="user-nav-avatar me-2 rounded-circle"
                                             style="width: 34px; height: 34px; object-fit: cover; border: 2px solid #ffffff;"/>
                                    </c:otherwise>
                                </c:choose>
                                <span class="fw-semibold">
                                    <c:out value="${not empty sessionScope.account.fullName ? sessionScope.account.fullName : sessionScope.account.userName}"/>
                                </span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="userMenuDropdown">
                                <li class="px-3 py-2 text-muted small border-bottom">
                                    Tài khoản: <strong><c:out value="${sessionScope.account.userName}"/></strong><br/>
                                    Email: <c:out value="${sessionScope.account.email}"/>
                                </li>
                                <li>
                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/profile">
                                        <i class="bi bi-person-badge me-2 text-primary"></i> Hồ sơ cá nhân (Profile)
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/member/myaccount">
                                        <i class="bi bi-shield-check me-2 text-secondary"></i> Chi tiết tài khoản
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/video/my-videos">
                                        <i class="bi bi-camera-video me-2 text-info"></i> Quản lý video của tôi
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item py-2 text-danger" href="${pageContext.request.contextPath}/logout">
                                        <i class="bi bi-box-arrow-right me-2"></i> Đăng xuất
                                    </a>
                                </li>
                            </ul>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>
