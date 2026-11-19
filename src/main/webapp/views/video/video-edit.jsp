<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeMenu" value="video" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh Sửa Video - Trường Đại học Công nghệ Kỹ thuật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .form-container {
            max-width: 720px;
            margin: 40px auto;
            background: #ffffff;
            padding: 32px;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
        }
    </style>
</head>
<body>

    <jsp:include page="/views/partials/topbar.jsp"/>

    <main class="page-container">
        <div class="form-container">
            <h1 style="font-size: 24px; color: #1e3a8a; margin: 0 0 6px 0;">Chỉnh Sửa Video</h1>
            <p style="color: #64748b; margin: 0 0 24px 0; font-size: 14px;">
                Cập nhật thông tin chi tiết hoặc thay đổi file video ID #${video.videoId}
            </p>

            <c:if test="${not empty alert}">
                <div class="alert alert-danger" style="margin-bottom: 20px;">
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/video/edit" method="post" enctype="multipart/form-data">
                <input type="hidden" name="id" value="${video.videoId}" />
                <input type="hidden" name="csrf_token" value="<c:out value='${sessionScope.csrf_token}'/>"/>
                <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrf_token}'/>"/>

                <div class="form-group">
                    <label for="title">Tiêu đề video <span style="color: #dc2626;">*</span></label>
                    <input type="text"
                           id="title"
                           name="title"
                           value="<c:out value='${video.title}'/>"
                           required
                           autofocus />
                </div>

                <div class="form-group">
                    <label for="description">Mô tả nội dung</label>
                    <textarea id="description"
                              name="description"
                              rows="4"><c:out value="${video.description}"/></textarea>
                </div>

                <div class="form-group">
                    <label for="status">Trạng thái hiển thị</label>
                    <select id="status" name="status" class="form-control" style="padding: 10px; width: 100%; border: 1px solid #cbd5e1; border-radius: 6px;">
                        <option value="1" <c:if test="${video.status == 1}">selected</c:if>>Hiển thị công khai (Hoạt động)</option>
                        <option value="0" <c:if test="${video.status == 0}">selected</c:if>>Tạm khóa / Ẩn video</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>Ảnh Thumbnail hiện tại:</label>
                    <div style="margin: 8px 0;">
                        <img src="${pageContext.request.contextPath}/media/thumbnail?fname=${video.thumbnail}"
                             alt="Thumbnail"
                             style="width: 180px; height: 100px; object-fit: cover; border-radius: 6px; border: 1px solid #cbd5e1;"
                             onerror="this.src='${pageContext.request.contextPath}/assets/images/categories/default-category.png';" />
                    </div>
                    <label for="thumbnailFile">Chọn ảnh Thumbnail mới (để trống nếu không đổi):</label>
                    <input type="file"
                           id="thumbnailFile"
                           name="thumbnailFile"
                           accept="image/png,image/jpeg,image/webp,image/gif" />
                </div>

                <div class="form-group">
                    <label>File Video hiện tại: <code><c:out value="${video.videoPath}"/></code></label>
                    <label for="videoFile" style="display: block; margin-top: 8px;">Chọn file Video mới thay thế (để trống nếu giữ nguyên):</label>
                    <input type="file"
                           id="videoFile"
                           name="videoFile"
                           accept="video/mp4,video/webm,video/mkv,video/avi" />
                </div>

                <div style="display: flex; gap: 12px; margin-top: 28px;">
                    <button type="submit" class="btn btn-primary" style="padding: 10px 24px;">Lưu Thay Đổi</button>
                    <a href="${pageContext.request.contextPath}/video/detail?id=${video.videoId}" class="btn btn-secondary" style="padding: 10px 20px;">Hủy</a>
                </div>
            </form>
        </div>
    </main>

</body>
</html>
