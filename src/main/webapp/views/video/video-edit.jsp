<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeMenu" value="video" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh Sửa Video - Trường Đại học Công nghệ Kỹ thuật</title>
    <style>
        .form-container {
            max-width: 760px;
            margin: 32px auto;
            background: #ffffff;
            padding: 32px;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        }
        .field-error-msg {
            color: #dc2626;
            font-size: 13px;
            margin-top: 4px;
            display: none;
            text-align: left;
        }
        .input-error {
            border-color: #dc2626 !important;
            background-color: #fef2f2 !important;
        }
    </style>
</head>
<body>

    <div class="container py-4">
        <div class="form-container">
            <h2 class="fw-bold text-primary mb-1">
                <i class="bi bi-pencil-square me-2"></i> Chỉnh Sửa Video
            </h2>
            <p class="text-muted small mb-4">
                Cập nhật thông tin chi tiết hoặc thay đổi file cho video ID #<strong><c:out value="${video.videoId}"/></strong>
            </p>

            <c:if test="${not empty alert}">
                <div class="alert alert-danger mb-4 shadow-sm">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <form id="videoEditForm" action="${pageContext.request.contextPath}/video/edit" method="post" enctype="multipart/form-data" novalidate>
                <input type="hidden" name="id" value="${video.videoId}" />
                <input type="hidden" name="csrf_token" value="<c:out value='${sessionScope.csrf_token}'/>"/>
                <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrf_token}'/>"/>

                <div class="mb-3 text-start">
                    <label for="title" class="form-label fw-semibold">
                        Tiêu đề video <span class="text-danger">*</span>
                    </label>
                    <input type="text"
                           class="form-control"
                           id="title"
                           name="title"
                           value="<c:out value='${video.title}'/>"
                           maxlength="255"
                           required
                           autofocus />
                    <div id="titleError" class="field-error-msg">Tiêu đề video không được để trống (từ 3 đến 255 ký tự)!</div>
                </div>

                <div class="mb-3 text-start">
                    <label for="description" class="form-label fw-semibold">Mô tả nội dung</label>
                    <textarea class="form-control"
                              id="description"
                              name="description"
                              rows="4"
                              maxlength="2000"><c:out value="${video.description}"/></textarea>
                    <small class="text-muted" style="font-size: 12px;">Tối đa 2000 ký tự.</small>
                </div>

                <div class="mb-3 text-start">
                    <label for="status" class="form-label fw-semibold">Trạng thái hiển thị</label>
                    <select id="status" name="status" class="form-select">
                        <option value="1" <c:if test="${video.status == 1}">selected</c:if>>Hiển thị công khai (Hoạt động)</option>
                        <option value="0" <c:if test="${video.status == 0}">selected</c:if>>Tạm khóa / Ẩn video</option>
                    </select>
                </div>

                <div class="mb-3 text-start">
                    <label class="form-label fw-semibold">Ảnh Thumbnail hiện tại:</label>
                    <div class="mb-2">
                        <img src="${pageContext.request.contextPath}/media/thumbnail?fname=${video.thumbnail}"
                             alt="Thumbnail"
                             style="width: 200px; height: 112px; object-fit: cover; border-radius: 8px; border: 1px solid #cbd5e1;"
                             onerror="this.src='${pageContext.request.contextPath}/assets/images/categories/default-category.png';" />
                    </div>
                    <label for="thumbnailFile" class="form-label small text-muted">Chọn ảnh Thumbnail mới (để trống nếu giữ nguyên):</label>
                    <input class="form-control form-control-sm"
                           type="file"
                           id="thumbnailFile"
                           name="thumbnailFile"
                           accept="image/png,image/jpeg,image/webp,image/gif" />
                    <div id="thumbError" class="field-error-msg">Ảnh thumbnail không hợp lệ hoặc vượt quá dung lượng 5MB!</div>
                </div>

                <div class="mb-4 text-start">
                    <label class="form-label fw-semibold">File Video hiện tại:</label>
                    <div class="p-2 bg-light border rounded mb-2 small text-truncate">
                        <i class="bi bi-file-earmark-play me-1 text-primary"></i> <code><c:out value="${video.videoPath}"/></code>
                    </div>
                    <label for="videoFile" class="form-label small text-muted">Chọn file Video mới thay thế (để trống nếu giữ nguyên):</label>
                    <input class="form-control form-control-sm"
                           type="file"
                           id="videoFile"
                           name="videoFile"
                           accept="video/mp4,video/webm,video/mkv,video/avi" />
                    <div id="videoError" class="field-error-msg">File video không hợp lệ hoặc vượt quá dung lượng 100MB!</div>
                </div>

                <div class="d-flex gap-2 pt-2 border-top">
                    <button type="submit" id="btnSave" class="btn btn-primary px-4 fw-semibold">
                        <i class="bi bi-save me-1"></i> Lưu Thay Đổi
                    </button>
                    <a href="${pageContext.request.contextPath}/video/detail?id=${video.videoId}" class="btn btn-outline-secondary px-3">Hủy</a>
                </div>
            </form>
        </div>
    </div>

    <script>
    document.addEventListener("DOMContentLoaded", function () {
        const form = document.getElementById("videoEditForm");
        const titleInput = document.getElementById("title");
        const videoInput = document.getElementById("videoFile");
        const thumbInput = document.getElementById("thumbnailFile");

        const titleError = document.getElementById("titleError");
        const videoError = document.getElementById("videoError");
        const thumbError = document.getElementById("thumbError");
        const btnSave = document.getElementById("btnSave");

        function checkTitle() {
            const val = titleInput.value.trim();
            if (val.length < 3 || val.length > 255) {
                titleError.style.display = "block";
                titleInput.classList.add("input-error");
                return false;
            }
            titleError.style.display = "none";
            titleInput.classList.remove("input-error");
            return true;
        }

        function checkVideo() {
            const file = videoInput.files[0];
            if (!file) {
                videoError.style.display = "none";
                videoInput.classList.remove("input-error");
                return true;
            }
            if (file.size > 100 * 1024 * 1024) {
                videoError.textContent = "Dung lượng video thay thế vượt quá 100MB!";
                videoError.style.display = "block";
                videoInput.classList.add("input-error");
                return false;
            }
            const name = file.name.toLowerCase();
            const valid = name.endsWith(".mp4") || name.endsWith(".webm") || name.endsWith(".mkv") || name.endsWith(".avi");
            if (!valid) {
                videoError.textContent = "Định dạng file video không hợp lệ (chỉ chấp nhận MP4, WEBM, MKV, AVI)!";
                videoError.style.display = "block";
                videoInput.classList.add("input-error");
                return false;
            }
            videoError.style.display = "none";
            videoInput.classList.remove("input-error");
            return true;
        }

        function checkThumb() {
            const file = thumbInput.files[0];
            if (!file) {
                thumbError.style.display = "none";
                thumbInput.classList.remove("input-error");
                return true;
            }
            if (file.size > 5 * 1024 * 1024) {
                thumbError.textContent = "Dung lượng ảnh thumbnail vượt quá 5MB!";
                thumbError.style.display = "block";
                thumbInput.classList.add("input-error");
                return false;
            }
            const name = file.name.toLowerCase();
            const valid = name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp");
            if (!valid) {
                thumbError.textContent = "Định dạng ảnh thumbnail không hợp lệ (chỉ chấp nhận JPG, PNG, WEBP)!";
                thumbError.style.display = "block";
                thumbInput.classList.add("input-error");
                return false;
            }
            thumbError.style.display = "none";
            thumbInput.classList.remove("input-error");
            return true;
        }

        titleInput.addEventListener("input", checkTitle);
        videoInput.addEventListener("change", checkVideo);
        thumbInput.addEventListener("change", checkThumb);

        form.addEventListener("submit", function (e) {
            const ok = checkTitle() & checkVideo() & checkThumb();
            if (!ok) {
                e.preventDefault();
                return;
            }
            btnSave.disabled = true;
            btnSave.innerHTML = '<span class="spinner-border spinner-border-sm me-1" role="status"></span> Đang lưu thay đổi...';
            form.submit();
        });
    });
    </script>
</body>
</html>
