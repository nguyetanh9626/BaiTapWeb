<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><sitemesh:write property='title'/> - Trường Đại học Công nghệ Kỹ thuật</title>

    <!-- Bootstrap 5.3.3 CSS (CDN) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- Bootstrap Icons 1.11.3 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <!-- CSS tùy chỉnh hệ thống -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

    <style>
        /* Tối ưu giao diện thanh điều hướng Bootstrap và Avatar */
        .navbar-brand-custom {
            display: flex;
            align-items: center;
            gap: 12px;
            font-weight: 700;
            color: #ffffff !important;
            letter-spacing: 0.3px;
        }
        .navbar-brand-custom .brand-icon {
            width: 38px;
            height: 38px;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
        }
        .navbar-brand-custom .brand-text-main {
            font-size: 16px;
            line-height: 1.2;
            text-transform: uppercase;
        }
        .navbar-brand-custom .brand-text-sub {
            font-size: 12px;
            font-weight: 400;
            color: rgba(255, 255, 255, 0.85);
            display: block;
        }
        .user-nav-avatar {
            width: 34px;
            height: 34px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid rgba(255, 255, 255, 0.6);
            background-color: #ffffff;
        }
        .site-main-wrapper {
            flex: 1 0 auto;
            min-height: calc(100vh - 160px);
        }
        .site-footer-custom {
            background: #0f172a;
            color: #94a3b8;
            padding: 24px 0 20px 0;
            margin-top: auto;
            border-top: 3px solid #2563eb;
        }
        .site-footer-custom a {
            color: #cbd5e1;
            text-decoration: none;
        }
        .site-footer-custom a:hover {
            color: #60a5fa;
            text-decoration: underline;
        }
    </style>

    <sitemesh:write property='head'/>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">

    <!-- Header / Navbar Bootstrap duy nhất cho toàn hệ thống -->
    <header>
        <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm" style="background: linear-gradient(135deg, #0f2f6d 0%, #1d4ed8 100%) !important;">
            <div class="container-fluid px-lg-4">
                <a class="navbar-brand navbar-brand-custom" href="${pageContext.request.contextPath}/home">
                    <div class="brand-icon">
                        <i class="bi bi-play-btn-fill"></i>
                    </div>
                    <div>
                        <span class="brand-text-main">ĐH Công Nghệ Kỹ Thuật</span>
                        <span class="brand-text-sub">Cổng Thông Tin & Kho Video Trực Tuyến</span>
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
                            <a class="nav-link px-3 active" href="${pageContext.request.contextPath}/home">
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
                                        <!-- Hiển thị ảnh đại diện Cloudinary hoặc ảnh mặc định -->
                                        <c:choose>
                                            <c:when test="${not empty sessionScope.account.avatar}">
                                                <img src="<c:out value='${sessionScope.account.avatar}'/>"
                                                     alt="Avatar" class="user-nav-avatar me-2"
                                                     onerror="this.src='${pageContext.request.contextPath}/assets/images/default-avatar.png';"/>
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/assets/images/default-avatar.png"
                                                     alt="Default Avatar" class="user-nav-avatar me-2"/>
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
    </header>

    <!-- Khu vực hiển thị thông báo Flash (Success / Error) dùng chung toàn hệ thống -->
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

    <!-- Main Content Placeholder của SiteMesh 3 -->
    <main class="site-main-wrapper">
        <sitemesh:write property='body'/>
    </main>

    <!-- Footer Bootstrap đồng bộ duy nhất -->
    <footer class="site-footer-custom">
        <div class="container">
            <div class="row gy-3 align-items-center">
                <div class="col-md-6 text-center text-md-start">
                    <h6 class="text-white mb-1 fw-bold">TRƯỜNG ĐẠI HỌC CÔNG NGHỆ KỸ THUẬT</h6>
                    <p class="mb-0 small text-secondary">
                        Khoa Công Nghệ Thông Tin &bull; Cổng dịch vụ học tập và chia sẻ video bài giảng trực tuyến.
                    </p>
                </div>
                <div class="col-md-6 text-center text-md-end">
                    <p class="mb-1 small">
                        &copy; 2026 <strong>Baitapweb Portal</strong>. Tất cả quyền được bảo lưu.
                    </p>
                    <div class="small">
                        <a href="${pageContext.request.contextPath}/home" class="me-3">Trang chủ</a>
                        <a href="${pageContext.request.contextPath}/video" class="me-3">Kho Video</a>
                        <a href="${pageContext.request.contextPath}/profile">Hồ sơ cá nhân</a>
                    </div>
                </div>
            </div>
        </div>
    </footer>

    <!-- Bootstrap 5.3.3 JS Bundle (Popper.js đi kèm) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

</body>
</html>
