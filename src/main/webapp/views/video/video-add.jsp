<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeMenu" value="video-add" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Tải Video Mới - Trường Đại học Công nghệ Kỹ thuật</title>
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
            <h1 style="font-size: 24px; color: #1e3a8a; margin: 0 0 6px 0;">Đăng Tải Video Mới</h1>
            <p style="color: #64748b; margin: 0 0 24px 0; font-size: 14px;">
                Chia sẻ video bài giảng hoặc tư liệu học tập lên cổng thông tin trường
            </p>

            <c:if test="${not empty alert}">
                <div class="alert alert-danger" style="margin-bottom: 20px;">
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/video/add" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="title">Tiêu đề video <span style="color: #dc2626;">*</span></label>
                    <input type="text"
                           id="title"
                           name="title"
                           value="<c:out value='${valTitle}'/>"
                           placeholder="Nhập tiêu đề video rõ ràng..."
                           required
                           autofocus />
                </div>

                <div class="form-group">
                    <label for="description">Mô tả nội dung</label>
                    <textarea id="description"
                              name="description"
                              rows="4"
                              placeholder="Nhập tóm tắt nội dung video, mục tiêu bài học..."><c:out value="${valDescription}"/></textarea>
                </div>

                <div class="form-group">
                    <label for="videoFile">File Video (Tối đa 100MB) <span style="color: #dc2626;">*</span></label>
                    <input type="file"
                           id="videoFile"
                           name="videoFile"
                           accept="video/mp4,video/webm,video/mkv,video/avi"
                           required />
                    <small style="color: #64748b; display: block; margin-top: 4px;">Hỗ trợ các định dạng: MP4, WEBM, MKV, AVI.</small>
                </div>

                <div class="form-group">
                    <label for="thumbnailFile">Ảnh đại diện Thumbnail (Tùy chọn, tối đa 5MB)</label>
                    <input type="file"
                           id="thumbnailFile"
                           name="thumbnailFile"
                           accept="image/png,image/jpeg,image/webp,image/gif" />
                    <small style="color: #64748b; display: block; margin-top: 4px;">Hỗ trợ: PNG, JPG, JPEG, WEBP.</small>
                </div>

                <div style="display: flex; gap: 12px; margin-top: 28px;">
                    <button type="submit" class="btn btn-primary" style="padding: 10px 24px;">Tải Lên Video</button>
                    <a href="${pageContext.request.contextPath}/video" class="btn btn-secondary" style="padding: 10px 20px;">Hủy</a>
                </div>
            </form>
        </div>
    </main>

</body>
</html>
