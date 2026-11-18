<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="activeMenu" value="video" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kho Video Bài Giảng - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .video-container {
            max-width: 1200px;
            margin: 32px auto;
            padding: 0 20px;
        }
        .video-header-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 16px;
            margin-bottom: 24px;
        }
        .video-search-form {
            display: flex;
            gap: 8px;
            max-width: 450px;
            width: 100%;
        }
        .video-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
            gap: 24px;
        }
        .video-card {
            background: #ffffff;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.07), 0 2px 4px -2px rgba(0,0,0,0.05);
            transition: transform 0.2s, box-shadow 0.2s;
            display: flex;
            flex-direction: column;
            border: 1px solid #e2e8f0;
        }
        .video-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -4px rgba(0,0,0,0.1);
        }
        .video-thumb-wrap {
            position: relative;
            padding-top: 56.25%; /* 16:9 Aspect Ratio */
            background: #0f172a;
            overflow: hidden;
        }
        .video-thumb-img {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .video-play-overlay {
            position: absolute;
            inset: 0;
            background: rgba(0,0,0,0.25);
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0.95;
            transition: opacity 0.2s, background 0.2s;
        }
        .video-card:hover .video-play-overlay {
            background: rgba(0,0,0,0.4);
            opacity: 1;
        }
        .play-btn-circle {
            width: 48px;
            height: 48px;
            background: #2563eb;
            color: #fff;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 12px rgba(0,0,0,0.35);
            transition: transform 0.2s, background 0.2s;
        }
        .video-card:hover .play-btn-circle {
            transform: scale(1.1);
            background: #1d4ed8;
        }
        .video-content {
            padding: 16px;
            display: flex;
            flex-direction: column;
            flex-grow: 1;
        }
        .video-title {
            font-size: 16px;
            font-weight: 700;
            color: #1e293b;
            margin: 0 0 8px 0;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        .video-title a {
            color: inherit;
            text-decoration: none;
        }
        .video-title a:hover {
            color: #2563eb;
        }
        .video-desc {
            font-size: 13px;
            color: #64748b;
            margin: 0 0 12px 0;
            line-height: 1.5;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            flex-grow: 1;
        }
        .video-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 12px;
            color: #94a3b8;
            border-top: 1px solid #f1f5f9;
            padding-top: 12px;
        }
        .pagination-bar {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 8px;
            margin-top: 40px;
        }
        .page-link-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-width: 36px;
            height: 36px;
            padding: 0 12px;
            border-radius: 6px;
            border: 1px solid #cbd5e1;
            background: #ffffff;
            color: #334155;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            transition: all 0.15s;
        }
        .page-link-btn:hover {
            border-color: #2563eb;
            color: #2563eb;
            background: #eff6ff;
        }
        .page-link-btn.active {
            background: #2563eb;
            border-color: #2563eb;
            color: #ffffff;
            font-weight: 600;
        }
        .page-link-btn.disabled {
            opacity: 0.5;
            pointer-events: none;
            cursor: not-allowed;
        }
    </style>
</head>
<body>

    <jsp:include page="/views/partials/topbar.jsp"/>

    <main class="video-container">
        <div class="video-header-row">
            <div>
                <h1 style="font-size: 26px; color: #1e3a8a; margin: 0 0 6px 0;">Kho Video & Bài Giảng Trực Tuyến</h1>
                <p style="color: #64748b; margin: 0; font-size: 14px;">
                    Tổng số <strong>${totalItems}</strong> video được chia sẻ bởi sinh viên và giảng viên
                </p>
            </div>

            <div style="display: flex; gap: 12px; align-items: center;">
                <form action="${pageContext.request.contextPath}/video" method="get" class="video-search-form">
                    <input type="text"
                           name="keyword"
                           value="<c:out value='${keyword}'/>"
                           placeholder="Tìm kiếm video..."
                           class="form-control"
                           style="padding: 8px 14px; border: 1px solid #cbd5e1; border-radius: 6px; flex-grow: 1;" />
                    <button type="submit" class="btn btn-primary" style="padding: 8px 16px;">Tìm</button>
                    <c:if test="${not empty keyword}">
                        <a href="${pageContext.request.contextPath}/video" class="btn btn-secondary" style="padding: 8px 12px;">Xóa</a>
                    </c:if>
                </form>

                <c:if test="${not empty sessionScope.account}">
                    <a href="${pageContext.request.contextPath}/video/add" class="btn btn-primary" style="white-space: nowrap; padding: 8px 16px;">
                        + Đăng Video Mới
                    </a>
                </c:if>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty videoList}">
                <div style="text-align: center; padding: 60px 20px; background: #fff; border-radius: 12px; border: 1px solid #e2e8f0;">
                    <p style="font-size: 18px; color: #64748b; margin-bottom: 16px;">Không tìm thấy video nào phù hợp.</p>
                    <a href="${pageContext.request.contextPath}/video" class="btn btn-secondary">Xem tất cả video</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="video-grid">
                    <c:forEach var="v" items="${videoList}">
                        <div class="video-card">
                            <div class="video-thumb-wrap">
                                <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}" style="display: block; width: 100%; height: 100%;">
                                    <img src="${pageContext.request.contextPath}/media/thumbnail?fname=${v.thumbnail}"
                                         alt="<c:out value='${v.title}'/>"
                                         class="video-thumb-img"
                                         onerror="this.src='${pageContext.request.contextPath}/assets/images/categories/default-category.png';" />
                                    <div class="video-play-overlay">
                                        <div class="play-btn-circle">
                                            <svg width="24" height="24" viewBox="0 0 24 24" fill="#ffffff" style="margin-left: 3px;">
                                                <path d="M8 5v14l11-7z"/>
                                            </svg>
                                        </div>
                                    </div>
                                </a>
                            </div>
                            <div class="video-content">
                                <h3 class="video-title">
                                    <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}">
                                        <c:out value="${v.title}"/>
                                    </a>
                                </h3>
                                <p class="video-desc"><c:out value="${v.description}"/></p>
                                <div class="video-meta">
                                    <span>Đăng bởi: <strong><c:out value="${not empty v.uploaderName ? v.uploaderName : 'Thành viên'}"/></strong></span>
                                    <span>&#128065; ${v.views} lượt xem</span>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Phân trang chuẩn 6 video/trang theo yêu cầu giảng viên -->
                <c:if test="${totalPages > 1}">
                    <div class="pagination-bar">
                        <c:url var="prevUrl" value="/video">
                            <c:param name="page" value="${currentPage - 1}"/>
                            <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
                        </c:url>
                        <a href="${prevUrl}" class="page-link-btn ${currentPage <= 1 ? 'disabled' : ''}">&laquo; Trước</a>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:url var="pageUrl" value="/video">
                                <c:param name="page" value="${i}"/>
                                <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
                            </c:url>
                            <a href="${pageUrl}" class="page-link-btn ${currentPage == i ? 'active' : ''}">${i}</a>
                        </c:forEach>

                        <c:url var="nextUrl" value="/video">
                            <c:param name="page" value="${currentPage + 1}"/>
                            <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
                        </c:url>
                        <a href="${nextUrl}" class="page-link-btn ${currentPage >= totalPages ? 'disabled' : ''}">Sau &raquo;</a>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>
    </main>

</body>
</html>
