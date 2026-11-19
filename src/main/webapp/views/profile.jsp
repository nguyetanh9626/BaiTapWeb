<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hồ Sơ Cá Nhân - Quản Lý Tài Khoản</title>
    <style>
        .profile-avatar-box {
            position: relative;
            width: 160px;
            height: 160px;
            margin: 0 auto 16px auto;
        }
        .profile-avatar-img {
            width: 160px;
            height: 160px;
            object-fit: cover;
            border-radius: 50%;
            border: 4px solid #3b82f6;
            box-shadow: 0 4px 12px rgba(0,0,0,0.12);
            background-color: #f8fafc;
        }
        .card-custom {
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            margin-bottom: 24px;
        }
        .form-label-bold {
            font-weight: 600;
            color: #334155;
            margin-bottom: 6px;
        }
        .field-error-msg {
            font-size: 13px;
            color: #dc2626;
            margin-top: 4px;
            display: none;
        }
        .step-badge {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 26px;
            height: 26px;
            border-radius: 50%;
            background-color: #2563eb;
            color: #ffffff;
            font-size: 13px;
            font-weight: bold;
            margin-right: 8px;
        }
    </style>
</head>
<body>

<div class="container py-4">

    <!-- Breadcrumb điều hướng -->
    <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none">Trang chủ</a></li>
            <li class="breadcrumb-item active" aria-current="page">Hồ sơ cá nhân</li>
        </ol>
    </nav>

    <div class="row g-4">
        <!-- CỘT TRÁI: ẢNH ĐẠI DIỆN CLOUDINARY & TÓM TẮT TÀI KHOẢN -->
        <div class="col-lg-4">
            <div class="card card-custom p-4 text-center">
                <h5 class="fw-bold text-primary mb-3">Ảnh Đại Diện</h5>

                <div class="profile-avatar-box">
                    <c:choose>
                        <c:when test="${not empty user.avatar}">
                            <img id="avatarPreview"
                                 src="<c:out value='${user.avatar}'/>"
                                 alt="Avatar"
                                 class="profile-avatar-img"
                                 onerror="this.src='${pageContext.request.contextPath}/assets/images/default-avatar.png';"/>
                        </c:when>
                        <c:otherwise>
                            <img id="avatarPreview"
                                 src="${pageContext.request.contextPath}/assets/images/default-avatar.png"
                                 alt="Avatar"
                                 class="profile-avatar-img"/>
                        </c:otherwise>
                    </c:choose>
                </div>

                <h5 class="fw-bold mb-1" id="displayFullname"><c:out value="${user.fullName}"/></h5>
                <p class="text-muted small mb-2">@<c:out value="${user.userName}"/></p>

                <div class="mb-3">
                    <span class="badge bg-primary-subtle text-primary border border-primary-subtle px-3 py-2 rounded-pill">
                        <c:choose>
                            <c:when test="${user.roleid == 1}"><i class="bi bi-shield-shaded me-1"></i> Quản trị viên</c:when>
                            <c:when test="${user.roleid == 2}"><i class="bi bi-person-gear me-1"></i> Quản lý</c:when>
                            <c:otherwise><i class="bi bi-person me-1"></i> Thành viên</c:otherwise>
                        </c:choose>
                    </span>
                </div>

                <hr class="my-3"/>

                <!-- FORM UPLOAD ẢNH ĐẠI DIỆN LÊN CLOUDINARY BẰNG MULTIPART -->
                <form id="avatarUploadForm"
                      action="${pageContext.request.contextPath}/profile/upload-avatar"
                      method="post"
                      enctype="multipart/form-data">
                    <input type="hidden" name="csrf_token" value="<c:out value='${sessionScope.csrf_token}'/>"/>
                    <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrf_token}'/>"/>

                    <div class="mb-3 text-start">
                        <label for="avatarFile" class="form-label form-label-bold small">
                            <i class="bi bi-cloud-arrow-up me-1 text-primary"></i> Đổi ảnh mới (Cloudinary):
                        </label>
                        <input class="form-control form-control-sm"
                               type="file"
                               id="avatarFile"
                               name="avatarFile"
                               accept="image/jpeg,image/png,image/webp"
                               required />
                        <div id="avatarFileError" class="field-error-msg">Vui lòng chọn file ảnh JPG, PNG hoặc WEBP (tối đa 5MB).</div>
                        <small class="text-muted d-block mt-1" style="font-size: 11.5px;">
                            Hỗ trợ JPG, PNG, WEBP. Tối đa 5MB. Lưu trực tiếp trên Cloudinary.
                        </small>
                    </div>

                    <button type="submit" id="btnUploadAvatar" class="btn btn-outline-primary btn-sm w-100" disabled>
                        <i class="bi bi-upload me-1"></i> Tải Lên Cloudinary
                    </button>
                </form>

                <c:if test="${not cloudinaryConfigured}">
                    <div class="alert alert-warning p-2 mt-3 small text-start mb-0">
                        <i class="bi bi-exclamation-triangle-fill me-1"></i>
                        Máy chủ chưa cấu hình API Cloudinary (CLOUDINARY_CLOUD_NAME).
                    </div>
                </c:if>
            </div>

            <!-- Tóm tắt tài khoản -->
            <div class="card card-custom p-3 text-start">
                <h6 class="fw-bold text-secondary mb-2"><i class="bi bi-info-circle me-1"></i> Thông Tin Hệ Thống</h6>
                <ul class="list-unstyled small mb-0 text-muted">
                    <li class="py-1"><strong>Mã tài khoản ID:</strong> #<c:out value="${user.id}"/></li>
                    <li class="py-1"><strong>Ngày tham gia:</strong> <c:out value="${user.createdDate}"/></li>
                    <li class="py-1"><strong>Trạng thái:</strong>
                        <span class="text-success fw-bold"><i class="bi bi-check-circle-fill"></i> Đã kích hoạt</span>
                    </li>
                </ul>
            </div>
        </div>

        <!-- CỘT PHẢI: FORM CẬP NHẬT THÔNG TIN VÀ ĐỔI EMAIL BẰNG OTP -->
        <div class="col-lg-8">

            <!-- CARD 1: CẬP NHẬT HỌ VÀ TÊN & SỐ ĐIỆN THOẠI -->
            <div class="card card-custom p-4">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="fw-bold text-primary mb-0">
                        <i class="bi bi-person-lines-fill me-2"></i> Thông Tin Cá Nhân
                    </h5>
                    <button type="button" class="btn btn-outline-secondary btn-sm" id="btnToggleEdit">
                        <i class="bi bi-pencil-square me-1"></i> Chỉnh sửa
                    </button>
                </div>

                <form id="profileInfoForm" action="${pageContext.request.contextPath}/profile/update-info" method="post">
                    <input type="hidden" name="csrf_token" value="<c:out value='${sessionScope.csrf_token}'/>"/>
                    <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrf_token}'/>"/>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="usernameField" class="form-label form-label-bold">Tên đăng nhập (Username)</label>
                            <input type="text"
                                   id="usernameField"
                                   class="form-control bg-light"
                                   value="<c:out value='${user.userName}'/>"
                                   readonly />
                            <small class="text-muted" style="font-size: 12px;">Tên đăng nhập là duy nhất và cố định.</small>
                        </div>

                        <div class="col-md-6">
                            <label for="fullnameInput" class="form-label form-label-bold">
                                Họ và tên <span class="text-danger">*</span>
                            </label>
                            <input type="text"
                                   class="form-control"
                                   id="fullnameInput"
                                   name="fullname"
                                   value="<c:out value='${user.fullName}'/>"
                                   required
                                   maxlength="100"
                                   disabled />
                            <div id="fullnameError" class="field-error-msg">Họ và tên không được để trống (từ 2 đến 100 ký tự).</div>
                        </div>

                        <div class="col-md-6">
                            <label for="phoneInput" class="form-label form-label-bold">
                                Số điện thoại <span class="text-danger">*</span>
                            </label>
                            <input type="tel"
                                   class="form-control"
                                   id="phoneInput"
                                   name="phone"
                                   value="<c:out value='${user.phone}'/>"
                                   required
                                   maxlength="11"
                                   disabled />
                            <div id="phoneError" class="field-error-msg">Số điện thoại không hợp lệ (10 chữ số bắt đầu bằng số 0, VD: 0901234567).</div>
                        </div>

                        <div class="col-md-6">
                            <label for="currentEmailDisplay" class="form-label form-label-bold">Email hiện tại</label>
                            <input type="email"
                                   id="currentEmailDisplay"
                                   class="form-control bg-light"
                                   value="<c:out value='${user.email}'/>"
                                   readonly />
                            <small class="text-muted" style="font-size: 12px;">Đổi email tại mục bên dưới bằng mã xác thực OTP.</small>
                        </div>
                    </div>

                    <div id="infoFormButtons" class="mt-4 pt-2 border-top text-end" style="display: none;">
                        <button type="button" class="btn btn-light btn-sm me-2" id="btnCancelEdit">Hủy</button>
                        <button type="submit" class="btn btn-primary btn-sm px-4" id="btnSaveInfo">
                            <i class="bi bi-save me-1"></i> Lưu Thay Đổi
                        </button>
                    </div>
                </form>
            </div>

            <!-- CARD 2: CẬP NHẬT EMAIL BẰNG MÃ XÁC THỰC OTP -->
            <div class="card card-custom p-4">
                <div class="mb-3">
                    <h5 class="fw-bold text-primary mb-1">
                        <i class="bi bi-envelope-check me-2"></i> Đổi Địa Chỉ Email Bằng Mã OTP
                    </h5>
                    <p class="text-muted small mb-0">
                        Để bảo đảm an toàn, hệ thống sẽ gửi mã OTP gồm 6 chữ số tới <strong>Email mới</strong> của bạn (Hiệu lực 5 phút).
                    </p>
                </div>

                <div class="row g-4">
                    <!-- BƯỚC 1: GỬI MÃ OTP -->
                    <div class="col-md-6 border-end-md">
                        <div class="p-2">
                            <h6 class="fw-bold mb-3">
                                <span class="step-badge">1</span> Nhập Email Mới & Nhận OTP
                            </h6>

                            <form id="sendOtpForm" action="${pageContext.request.contextPath}/profile/send-email-otp" method="post">
                                <input type="hidden" name="csrf_token" value="<c:out value='${sessionScope.csrf_token}'/>"/>
                                <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrf_token}'/>"/>

                                <div class="mb-3">
                                    <label for="newEmailInput" class="form-label form-label-bold small">
                                        Địa chỉ Email mới <span class="text-danger">*</span>
                                    </label>
                                    <input type="email"
                                           class="form-control form-control-sm"
                                           id="newEmailInput"
                                           name="newEmail"
                                           placeholder="nhap-email-moi@domain.com"
                                           required />
                                    <div id="newEmailError" class="field-error-msg">Email không đúng định dạng hoặc trùng email hiện tại.</div>
                                </div>

                                <button type="submit" class="btn btn-outline-primary btn-sm w-100" id="btnSendOtp">
                                    <i class="bi bi-send me-1"></i> Gửi Mã OTP Đến Email Mới
                                </button>
                            </form>
                        </div>
                    </div>

                    <!-- BƯỚC 2: XÁC THỰC OTP VÀ CẬP NHẬT -->
                    <div class="col-md-6">
                        <div class="p-2">
                            <h6 class="fw-bold mb-3">
                                <span class="step-badge">2</span> Xác Thực Mã OTP & Cập Nhật
                            </h6>

                            <c:if test="${not empty pendingEmail}">
                                <div class="alert alert-info py-2 px-3 small mb-3">
                                    <i class="bi bi-clock-history me-1"></i>
                                    Đang chờ xác thực cho email: <strong><c:out value="${pendingEmail}"/></strong>
                                </div>
                            </c:if>

                            <form id="verifyOtpForm" action="${pageContext.request.contextPath}/profile/verify-email-otp" method="post">
                                <input type="hidden" name="csrf_token" value="<c:out value='${sessionScope.csrf_token}'/>"/>
                                <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrf_token}'/>"/>

                                <div class="mb-3">
                                    <label for="otpInput" class="form-label form-label-bold small">
                                        Mã OTP (6 chữ số) <span class="text-danger">*</span>
                                    </label>
                                    <input type="text"
                                           class="form-control form-control-sm text-center fw-bold letter-spacing"
                                           id="otpInput"
                                           name="otp"
                                           maxlength="6"
                                           placeholder="------"
                                           pattern="[0-9]{6}"
                                           required />
                                    <div id="otpError" class="field-error-msg">Vui lòng nhập chính xác mã OTP 6 chữ số.</div>
                                </div>

                                <button type="submit" class="btn btn-success btn-sm w-100 fw-bold" id="btnVerifyOtp">
                                    <i class="bi bi-check2-circle me-1"></i> Xác Thực & Hoàn Tất Đổi Email
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

<!-- JAVASCRIPT VALIDATION VÀ TƯƠNG TÁC GIAO DIỆN PROFILE -->
<script>
document.addEventListener("DOMContentLoaded", function () {
    // 1. Quản lý xem trước và kiểm tra file ảnh avatar
    const avatarInput = document.getElementById("avatarFile");
    const avatarPreview = document.getElementById("avatarPreview");
    const btnUploadAvatar = document.getElementById("btnUploadAvatar");
    const avatarFileError = document.getElementById("avatarFileError");

    if (avatarInput) {
        avatarInput.addEventListener("change", function () {
            const file = this.files[0];
            avatarFileError.style.display = "none";
            btnUploadAvatar.disabled = true;

            if (!file) return;

            // Kiểm tra dung lượng tối đa 5MB
            if (file.size > 5 * 1024 * 1024) {
                avatarFileError.textContent = "Dung lượng ảnh vượt quá 5MB. Vui lòng chọn ảnh nhỏ hơn!";
                avatarFileError.style.display = "block";
                this.value = "";
                return;
            }

            // Kiểm tra định dạng đuôi file
            const allowed = ["image/jpeg", "image/png", "image/webp", "image/jpg"];
            if (!allowed.includes(file.type.toLowerCase())) {
                avatarFileError.textContent = "Định dạng file không hợp lệ! Chỉ chấp nhận ảnh JPG, PNG hoặc WEBP.";
                avatarFileError.style.display = "block";
                this.value = "";
                return;
            }

            // Xem trước ảnh trực tiếp (Client-side FileReader)
            const reader = new FileReader();
            reader.onload = function (e) {
                avatarPreview.src = e.target.result;
            };
            reader.readAsDataURL(file);
            btnUploadAvatar.disabled = false;
        });
    }

    // 2. Chế độ Chỉnh sửa Họ tên & Số điện thoại
    const btnToggleEdit = document.getElementById("btnToggleEdit");
    const btnCancelEdit = document.getElementById("btnCancelEdit");
    const fullnameInput = document.getElementById("fullnameInput");
    const phoneInput = document.getElementById("phoneInput");
    const infoFormButtons = document.getElementById("infoFormButtons");
    const profileInfoForm = document.getElementById("profileInfoForm");

    const initialFullname = fullnameInput.value;
    const initialPhone = phoneInput.value;

    btnToggleEdit.addEventListener("click", function () {
        const isEditing = !fullnameInput.disabled;
        if (isEditing) {
            cancelEdit();
        } else {
            fullnameInput.disabled = false;
            phoneInput.disabled = false;
            infoFormButtons.style.display = "block";
            fullnameInput.focus();
            this.innerHTML = '<i class="bi bi-x-circle me-1"></i> Hủy chỉnh sửa';
            this.classList.replace("btn-outline-secondary", "btn-secondary");
        }
    });

    btnCancelEdit.addEventListener("click", cancelEdit);

    function cancelEdit() {
        fullnameInput.value = initialFullname;
        phoneInput.value = initialPhone;
        fullnameInput.disabled = true;
        phoneInput.disabled = true;
        infoFormButtons.style.display = "none";
        document.getElementById("fullnameError").style.display = "none";
        document.getElementById("phoneError").style.display = "none";
        btnToggleEdit.innerHTML = '<i class="bi bi-pencil-square me-1"></i> Chỉnh sửa';
        btnToggleEdit.classList.replace("btn-secondary", "btn-outline-secondary");
    }

    // Validation form thông tin cá nhân
    profileInfoForm.addEventListener("submit", function (e) {
        let valid = true;
        const fnErr = document.getElementById("fullnameError");
        const phErr = document.getElementById("phoneError");
        fnErr.style.display = "none";
        phErr.style.display = "none";

        const fn = fullnameInput.value.trim();
        if (fn.length < 2 || fn.length > 100) {
            fnErr.style.display = "block";
            valid = false;
        }

        const ph = phoneInput.value.trim();
        const phoneRegex = /^(0[3|5|7|8|9])[0-9]{8}$|^0[0-9]{9,10}$/;
        if (!phoneRegex.test(ph)) {
            phErr.style.display = "block";
            valid = false;
        }

        if (!valid) {
            e.preventDefault();
        }
    });

    // 3. Validation form gửi OTP đổi email
    const sendOtpForm = document.getElementById("sendOtpForm");
    const newEmailInput = document.getElementById("newEmailInput");
    const currentEmail = document.getElementById("currentEmailDisplay").value.trim().toLowerCase();
    const newEmailError = document.getElementById("newEmailError");
    const btnSendOtp = document.getElementById("btnSendOtp");

    sendOtpForm.addEventListener("submit", function (e) {
        newEmailError.style.display = "none";
        const email = newEmailInput.value.trim().toLowerCase();
        const emailRegex = /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$/;

        if (!emailRegex.test(email)) {
            newEmailError.textContent = "Địa chỉ email không đúng định dạng!";
            newEmailError.style.display = "block";
            e.preventDefault();
            return;
        }

        if (email === currentEmail) {
            newEmailError.textContent = "Email mới phải khác với email hiện tại của bạn!";
            newEmailError.style.display = "block";
            e.preventDefault();
            return;
        }

        btnSendOtp.disabled = true;
        btnSendOtp.innerHTML = '<span class="spinner-border spinner-border-sm me-1" role="status"></span> Đang gửi mã OTP...';
    });

    // 4. Validation form xác thực OTP
    const verifyOtpForm = document.getElementById("verifyOtpForm");
    const otpInput = document.getElementById("otpInput");
    const otpError = document.getElementById("otpError");

    verifyOtpForm.addEventListener("submit", function (e) {
        otpError.style.display = "none";
        const otp = otpInput.value.trim();
        if (!/^[0-9]{6}$/.test(otp)) {
            otpError.style.display = "block";
            e.preventDefault();
        }
    });
});
</script>

</body>
</html>
