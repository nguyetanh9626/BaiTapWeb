<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeMenu" value="video-add" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Tải Video Mới - Trường Đại học Công nghệ Kỹ thuật</title>
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
                <i class="bi bi-cloud-arrow-up me-2"></i> Đăng Tải Video Mới
            </h2>
            <p class="text-muted small mb-4">
                Chia sẻ video bài giảng hoặc tư liệu học tập lên cổng thông tin trường
            </p>

            <c:if test="${not empty alert}">
                <div class="alert alert-danger mb-4 shadow-sm">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    <c:out value="${alert}"/>
                </div>
            </c:if>

            <form id="videoAddForm" action="${pageContext.request.contextPath}/video/add" method="post" enctype="multipart/form-data" novalidate>
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
                           value="<c:out value='${valTitle}'/>"
                           placeholder="Nhập tiêu đề video rõ ràng (từ 3 đến 255 ký tự)..."
                           required
                           maxlength="255"
                           autofocus />
                    <div id="titleError" class="field-error-msg">Tiêu đề video không được để trống (từ 3 đến 255 ký tự)!</div>
                </div>

                <div class="mb-3 text-start">
                    <label for="description" class="form-label fw-semibold">Mô tả nội dung</label>
                    <textarea class="form-control"
                              id="description"
                              name="description"
                              rows="4"
                              maxlength="2000"
                              placeholder="Nhập tóm tắt nội dung video, mục tiêu bài học..."><c:out value="${valDescription}"/></textarea>
                    <small class="text-muted" style="font-size: 12px;">Tối đa 2000 ký tự.</small>
                </div>

                <div class="mb-3 text-start">
                    <label for="videoFile" class="form-label fw-semibold">
                        File Video (Tối đa 100MB) <span class="text-danger">*</span>
                    </label>
                    <input class="form-control"
                           type="file"
                           id="videoFile"
                           name="videoFile"
                           accept="video/mp4,video/webm,video/mkv,video/avi"
                           required />
                    <div id="videoFileError" class="field-error-msg">Vui lòng chọn file video hợp lệ (MP4, WEBM, MKV, AVI) dung lượng không quá 100MB!</div>
                    <small class="text-muted d-block mt-1" style="font-size: 12px;">Hỗ trợ: MP4, WEBM, MKV, AVI. Dung lượng tối đa: 100MB.</small>
                </div>

                <div class="mb-4 text-start">
                    <label for="thumbnailFile" class="form-label fw-semibold">
                        Ảnh Thumbnail (Tùy chọn, tối đa 5MB)
                    </label>
                    <input class="form-control"
                           type="file"
                           id="thumbnailFile"
                           name="thumbnailFile"
                           accept="image/png,image/jpeg,image/webp,image/gif" />
                    <div id="thumbFileError" class="field-error-msg">Ảnh thumbnail không hợp lệ hoặc vượt quá dung lượng 5MB (chỉ chấp nhận JPG, PNG, WEBP)!</div>
                    <small class="text-muted d-block mt-1" style="font-size: 12px;">Hỗ trợ: PNG, JPG, JPEG, WEBP. Tối đa 5MB.</small>
                </div>

                <div class="d-flex gap-2 pt-2 border-top">
                    <button type="submit" id="btnUpload" class="btn btn-primary px-4 fw-semibold">
                        <i class="bi bi-upload me-1"></i> Tải Lên Video
                    </button>
                    <a href="${pageContext.request.contextPath}/video" class="btn btn-outline-secondary px-3">Hủy</a>
                </div>
            </form>
        </div>
    </div>

    <script>
    document.addEventListener("DOMContentLoaded", function () {
        const form = document.getElementById("videoAddForm");
        const titleInput = document.getElementById("title");
        const videoInput = document.getElementById("videoFile");
        const thumbInput = document.getElementById("thumbnailFile");

        const titleError = document.getElementById("titleError");
        const videoError = document.getElementById("videoFileError");
        const thumbError = document.getElementById("thumbFileError");
        const btnUpload = document.getElementById("btnUpload");

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
                videoError.textContent = "Vui lòng chọn một file video để tải lên!";
                videoError.style.display = "block";
                videoInput.classList.add("input-error");
                return false;
            }
            if (file.size > 100 * 1024 * 1024) {
                videoError.textContent = "Dung lượng video vượt quá giới hạn 100MB!";
                videoError.style.display = "block";
                videoInput.classList.add("input-error");
                return false;
            }
            const name = file.name.toLowerCase();
            const valid = name.endsWith(".mp4") || name.endsWith(".webm") || name.endsWith(".mkv") || name.endsWith(".avi");
            if (!valid) {
                videoError.textContent = "Định dạng file video không hợp lệ! Chỉ chấp nhận MP4, WEBM, MKV, AVI.";
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
                thumbError.textContent = "Dung lượng ảnh thumbnail vượt quá giới hạn 5MB!";
                thumbError.style.display = "block";
                thumbInput.classList.add("input-error");
                return false;
            }
            const name = file.name.toLowerCase();
            const valid = name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp");
            if (!valid) {
                thumbError.textContent = "Định dạng ảnh thumbnail không hợp lệ! Chỉ chấp nhận JPG, PNG, WEBP.";
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
            btnUpload.disabled = true;
            btnUpload.innerHTML = '<span class="spinner-border spinner-border-sm me-1" role="status"></span> Đang tải lên và xử lý...';
            form.submit();
        });
    });
    </script>
</body>
</html>
