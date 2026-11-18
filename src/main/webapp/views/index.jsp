<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang Chủ - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .home-video-section {
            max-width: 1200px;
            margin: 40px auto 20px auto;
            padding: 0 20px;
        }
        .home-video-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
            border-bottom: 2px solid #e2e8f0;
            padding-bottom: 12px;
        }
        .home-video-header h2 {
            font-size: 22px;
            color: #1e3a8a;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .video-grid-10 {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
            gap: 20px;
        }
        .video-card-sm {
            background: #ffffff;
            border-radius: 10px;
            overflow: hidden;
            border: 1px solid #e2e8f0;
            box-shadow: 0 2px 4px rgba(0,0,0,0.04);
            display: flex;
            flex-direction: column;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .video-card-sm:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 16px rgba(0,0,0,0.08);
        }
        .video-card-sm .thumb-wrap {
            position: relative;
            padding-top: 56.25%;
            background: #0f172a;
        }
        .video-card-sm .thumb-img {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .video-card-sm .card-body {
            padding: 12px;
            display: flex;
            flex-direction: column;
            flex-grow: 1;
        }
        .video-card-sm .title {
            font-size: 14px;
            font-weight: 600;
            color: #1e293b;
            margin: 0 0 6px 0;
            line-height: 1.35;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        .video-card-sm .meta {
            font-size: 12px;
            color: #64748b;
            margin-top: auto;
            display: flex;
            justify-content: space-between;
            padding-top: 8px;
            border-top: 1px solid #f1f5f9;
        }
    </style>
</head>
<body>

    <jsp:include page="/views/partials/topbar.jsp"/>

    <main class="page-container">
        <div class="card card-center">
            <h1>Chào mừng đến với Hệ Thống Quản Lý Tài Khoản</h1>
            <p class="subtitle">Cổng thông tin và đào tạo - Trường Đại học Công nghệ Kỹ thuật</p>

            <c:if test="${not empty successMsg}">
                <div class="alert alert-success">
                    <c:out value="${successMsg}"/>
                </div>
            </c:if>

            <c:if test="${not empty alert}">
                <div class="alert alert-danger">
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${not empty sessionScope.account}">
                    <div class="info-box">
                        <h3>Thông tin tài khoản</h3>
                        <p><strong>Họ và tên:</strong> <c:out value="${sessionScope.account.fullName}"/></p>
                        <p><strong>Tên đăng nhập:</strong> <c:out value="${sessionScope.account.userName}"/></p>
                        <p><strong>Email:</strong> <c:out value="${sessionScope.account.email}"/></p>
                        <p><strong>Số điện thoại:</strong> <c:out value="${sessionScope.account.phone}"/></p>
                        <p><strong>Vai trò:</strong>
                            <span class="role-pill">
                                <c:choose>
                                    <c:when test="${sessionScope.account.roleid == 1}">Quản trị viên</c:when>
                                    <c:when test="${sessionScope.account.roleid == 2}">Quản lý</c:when>
                                    <c:otherwise>Thành viên</c:otherwise>
                                </c:choose>
                            </span>
                        </p>
                        <p><strong>Trạng thái:</strong> <span class="status-ok">Đang đăng nhập</span></p>
                    </div>

                    <div class="btn-row">
                        <a href="${pageContext.request.contextPath}/video" class="btn btn-primary" style="display: inline-flex; align-items: center; gap: 8px;">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>
                            Kho Video Bài Giảng
                        </a>
                        <a href="${pageContext.request.contextPath}/video/my-videos" class="btn btn-secondary">Video của tôi</a>
                        <c:choose>
                            <c:when test="${sessionScope.account.roleid == 1 or sessionScope.account.roleid == 2}">
                                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">Quản lý danh mục (JPA)</a>
                                <a href="${pageContext.request.contextPath}/legacy/category/list" class="btn btn-secondary">Quản lý danh mục (JDBC)</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">Xem danh mục (JPA)</a>
                                <a href="${pageContext.request.contextPath}/legacy/category/list" class="btn btn-secondary">Xem danh mục (JDBC)</a>
                            </c:otherwise>
                        </c:choose>
                        <a href="${pageContext.request.contextPath}/member/myaccount" class="btn btn-secondary">Hồ sơ cá nhân</a>
                        <c:if test="${sessionScope.account.roleid == 1}">
                            <a href="${pageContext.request.contextPath}/admin/home" class="btn btn-secondary">Trang quản trị</a>
                            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Tài khoản có thể đăng nhập</a>
                        </c:if>
                        <c:if test="${sessionScope.account.roleid == 2}">
                            <a href="${pageContext.request.contextPath}/manager/home" class="btn btn-secondary">Trang quản lý</a>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-neutral">Đăng xuất</a>
                        <a href="${pageContext.request.contextPath}/logout?clearCookie=true" class="btn btn-danger">Đăng xuất &amp; Quên tài khoản</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="subtitle">
                        Vui lòng đăng nhập hoặc tạo tài khoản mới để tiếp tục sử dụng hệ thống.
                    </p>
                    <div class="btn-row">
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Đăng nhập (Login)</a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-secondary">Đăng ký (Register)</a>
                        <a href="${pageContext.request.contextPath}/video" class="btn btn-secondary" style="display: inline-flex; align-items: center; gap: 8px;">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>
                            Xem Video Bài Giảng
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <!-- Section 10 Video mới nhất theo yêu cầu giảng viên -->
    <section class="home-video-section">
        <div class="home-video-header">
            <h2>
                <svg width="24" height="24" viewBox="0 0 24 24" fill="#2563eb" style="vertical-align: middle;"><path d="M4 6H2v14c0 1.1.9 2 2 2h14v-2H4V6zm16-4H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-8 12.5v-9l6 4.5-6 4.5z"/></svg>
                10 Video Mới Nhất
            </h2>
            <a href="${pageContext.request.contextPath}/video" class="btn btn-secondary btn-sm" style="font-size: 13px;">
                Xem tất cả kho video &rarr;
            </a>
        </div>

        <c:choose>
            <c:when test="${empty top10Videos}">
                <p style="color: #64748b; text-align: center; padding: 24px;">Chưa có video nào trong hệ thống.</p>
            </c:when>
            <c:otherwise>
                <div class="video-grid-10">
                    <c:forEach var="v" items="${top10Videos}">
                        <div class="video-card-sm">
                            <div class="thumb-wrap">
                                <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" style="display: block; width: 100%; height: 100%; text-decoration: none;">
                                    <img src="${pageContext.request.contextPath}/media/thumbnail?fname=${v.thumbnail}"
                                         alt="<c:out value='${v.title}'/>"
                                         class="thumb-img"
                                         onerror="this.src='${pageContext.request.contextPath}/assets/images/categories/default-category.png';" />
                                    <div style="position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; background: rgba(0,0,0,0.25);">
                                        <div style="width: 48px; height: 48px; border-radius: 50%; background: #2563eb; color: #ffffff; display: flex; align-items: center; justify-content: center; box-shadow: 0 4px 12px rgba(0,0,0,0.35); transition: transform 0.2s;">
                                            <svg width="24" height="24" viewBox="0 0 24 24" fill="#ffffff" style="margin-left: 3px;">
                                                <path d="M8 5v14l11-7z"/>
                                            </svg>
                                        </div>
                                    </div>
                                </a>
                            </div>
                            <div class="card-body">
                                <h3 class="title">
                                    <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" style="color: inherit; text-decoration: none;">
                                        <c:out value="${v.title}"/>
                                    </a>
                                </h3>
                                <div class="meta">
                                    <span><c:out value="${not empty v.uploaderName ? v.uploaderName : 'Thành viên'}"/></span>
                                    <span style="display: inline-flex; align-items: center; gap: 4px;">
                                        <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>
                                        ${v.views}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

    <footer class="site-footer">
        &copy; 2026 - Khoa Công Nghệ Thông Tin - Trường Đại học Công nghệ Kỹ thuật
    </footer>

</body>
</html>
