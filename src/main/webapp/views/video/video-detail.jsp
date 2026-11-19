<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="activeMenu" value="video" scope="request"/>
<c:set var="isOwnerOrAdmin" value="${not empty sessionScope.account and (sessionScope.account.roleid == 1 or sessionScope.account.id == video.userId)}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${video.title}"/> - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .player-layout {
            max-width: 1280px;
            margin: 28px auto;
            padding: 0 20px;
            display: grid;
            grid-template-columns: 1fr 340px;
            gap: 28px;
        }
        @media (max-width: 900px) {
            .player-layout {
                grid-template-columns: 1fr;
            }
        }
        .main-player-col {
            display: flex;
            flex-direction: column;
            gap: 16px;
        }
        .video-player-wrap {
            width: 100%;
            background: #000;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        .video-player-wrap video {
            width: 100%;
            max-height: 520px;
            display: block;
        }
        .video-meta-box {
            background: #fff;
            padding: 20px;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
        }
        .video-meta-box h1 {
            font-size: 22px;
            color: #0f172a;
            margin: 0 0 12px 0;
            line-height: 1.4;
        }
        .video-stats-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 12px;
            padding-bottom: 16px;
            border-bottom: 1px solid #f1f5f9;
            color: #64748b;
            font-size: 14px;
        }
        .author-info {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-top: 16px;
        }
        .author-avatar {
            width: 44px;
            height: 44px;
            border-radius: 50%;
            background: #2563eb;
            color: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 18px;
        }
        .video-description-box {
            background: #f8fafc;
            border-radius: 8px;
            padding: 16px;
            margin-top: 16px;
            font-size: 14px;
            line-height: 1.6;
            color: #334155;
            white-space: pre-wrap;
        }
        .sidebar-col h3 {
            font-size: 18px;
            color: #1e3a8a;
            margin: 0 0 16px 0;
        }
        .sidebar-video-item {
            display: flex;
            gap: 12px;
            margin-bottom: 16px;
            text-decoration: none;
            color: inherit;
        }
        .sidebar-thumb {
            width: 120px;
            height: 68px;
            border-radius: 6px;
            object-fit: cover;
            background: #0f172a;
            flex-shrink: 0;
        }
        .sidebar-item-info {
            display: flex;
            flex-direction: column;
            justify-content: center;
        }
        .sidebar-item-title {
            font-size: 13px;
            font-weight: 600;
            color: #1e293b;
            line-height: 1.3;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            margin-bottom: 4px;
        }
        .sidebar-item-meta {
            font-size: 11px;
            color: #94a3b8;
        }
    </style>
</head>
<body>

    <jsp:include page="/views/partials/topbar.jsp"/>

    <main class="player-layout">
        <!-- Cột phát video và thông tin chi tiết -->
        <div class="main-player-col">
            <c:if test="${not empty sessionScope.flash_success}">
                <div class="alert alert-success">
                    <c:out value="${sessionScope.flash_success}"/>
                </div>
                <c:remove var="flash_success" scope="session"/>
            </c:if>

            <div class="video-player-wrap" style="position: relative;">
                <video id="mainVideoPlayer" controls playsinline preload="auto"
                       poster="${pageContext.request.contextPath}/media/thumbnail?fname=${video.thumbnail}"
                       style="width: 100%; display: block; max-height: 520px; background: #000;">
                    <source src="${pageContext.request.contextPath}/media/video?fname=${video.videoPath}" type="${not empty videoMime ? videoMime : 'video/mp4'}">
                    Trình duyệt của bạn không hỗ trợ thẻ phát video HTML5.
                </video>
                <div id="videoBigPlayBtn" style="position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; background: rgba(0,0,0,0.25); cursor: pointer; transition: opacity 0.25s, visibility 0.25s;">
                    <div style="width: 72px; height: 72px; border-radius: 50%; background: #2563eb; color: #fff; display: flex; align-items: center; justify-content: center; box-shadow: 0 6px 20px rgba(0,0,0,0.5); transition: transform 0.2s, background 0.2s;">
                        <svg width="36" height="36" viewBox="0 0 24 24" fill="#ffffff" style="margin-left: 4px;">
                            <path d="M8 5v14l11-7z"/>
                        </svg>
                    </div>
                </div>
            </div>

            <div class="video-meta-box">
                <h1><c:out value="${video.title}"/></h1>

                <div class="video-stats-row">
                    <div style="display: flex; align-items: center; gap: 8px;">
                        <span style="display: inline-flex; align-items: center; gap: 4px;">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>
                            <strong>${video.views}</strong> lượt xem
                        </span>
                        <span>&bull;</span>
                        <span>Ngày đăng: <strong><fmt:formatDate value="${video.uploadDate}" pattern="dd/MM/yyyy HH:mm"/></strong></span>
                    </div>

                    <c:if test="${isOwnerOrAdmin}">
                        <div style="display: flex; gap: 8px;">
                            <a href="${pageContext.request.contextPath}/video/edit?id=${video.videoId}" class="btn btn-secondary" style="padding: 6px 14px; font-size: 13px;">
                                Chỉnh sửa video
                            </a>
                            <form action="${pageContext.request.contextPath}/video/delete" method="post" style="display: inline;"
                                  onsubmit="return confirm('Bạn có chắc chắn muốn xóa video này không?');">
                                <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrf_token}'/>"/>
                                <input type="hidden" name="csrf_token" value="<c:out value='${sessionScope.csrf_token}'/>"/>
                                <input type="hidden" name="id" value="${video.videoId}"/>
                                <button type="submit" class="btn btn-danger" style="padding: 6px 14px; font-size: 13px;">
                                    Xóa video
                                </button>
                            </form>
                        </div>
                    </c:if>
                </div>

                <div class="author-info">
                    <div class="author-avatar">
                        <c:out value="${not empty video.uploaderUsername ? video.uploaderUsername.substring(0, 1).toUpperCase() : 'U'}"/>
                    </div>
                    <div>
                        <strong style="font-size: 15px; color: #1e293b;"><c:out value="${video.uploaderName}"/></strong>
                        <div style="font-size: 12px; color: #64748b;">@<c:out value="${video.uploaderUsername}"/></div>
                    </div>
                </div>

                <div class="video-description-box">
                    <c:out value="${video.description}"/>
                </div>
            </div>
        </div>

        <!-- Cột danh sách video đề xuất / mới nhất -->
        <div class="sidebar-col">
            <h3>Video Gợi Ý Khác</h3>
            <c:forEach var="rel" items="${relatedVideos}">
                <a href="${pageContext.request.contextPath}/video/detail?id=${rel.videoId}" class="sidebar-video-item">
                    <img src="${pageContext.request.contextPath}/media/thumbnail?fname=${rel.thumbnail}"
                         alt="<c:out value='${rel.title}'/>"
                         class="sidebar-thumb"
                         onerror="this.src='${pageContext.request.contextPath}/assets/images/categories/default-category.png';" />
                    <div class="sidebar-item-info">
                        <div class="sidebar-item-title"><c:out value="${rel.title}"/></div>
                        <div class="sidebar-item-meta" style="display: flex; align-items: center; gap: 4px;">
                            <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>
                            ${rel.views} lượt xem
                        </div>
                    </div>
                </a>
            </c:forEach>
            <a href="${pageContext.request.contextPath}/video" class="btn btn-secondary btn-block" style="text-align: center; margin-top: 16px;">
                Xem toàn bộ kho video &rarr;
            </a>
        </div>
    </main>

    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const video = document.getElementById('mainVideoPlayer');
            const bigBtn = document.getElementById('videoBigPlayBtn');
            if (video && bigBtn) {
                bigBtn.addEventListener('click', function (e) {
                    e.stopPropagation();
                    if (video.paused) {
                        video.play();
                    }
                });
                video.addEventListener('play', function () {
                    bigBtn.style.opacity = '0';
                    bigBtn.style.visibility = 'hidden';
                });
                video.addEventListener('pause', function () {
                    bigBtn.style.opacity = '1';
                    bigBtn.style.visibility = 'visible';
                });
                video.addEventListener('ended', function () {
                    bigBtn.style.opacity = '1';
                    bigBtn.style.visibility = 'visible';
                });
            }
        });
    </script>
</body>
</html>
