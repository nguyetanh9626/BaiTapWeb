USE [baitapweb];
GO

-- 1. Bổ sung các cột phục vụ kích hoạt OTP và đăng ký an toàn cho bảng [User]
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[User]') AND name = 'isActive')
BEGIN
    ALTER TABLE [dbo].[User] ADD [isActive] BIT NOT NULL CONSTRAINT DF_User_isActive DEFAULT 0;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[User]') AND name = 'otpHash')
BEGIN
    ALTER TABLE [dbo].[User] ADD [otpHash] VARCHAR(255) NULL;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[User]') AND name = 'otpExpiry')
BEGIN
    ALTER TABLE [dbo].[User] ADD [otpExpiry] DATETIME NULL;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[User]') AND name = 'otpAttempts')
BEGIN
    ALTER TABLE [dbo].[User] ADD [otpAttempts] INT NOT NULL CONSTRAINT DF_User_otpAttempts DEFAULT 0;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[User]') AND name = 'otpLastSentAt')
BEGIN
    ALTER TABLE [dbo].[User] ADD [otpLastSentAt] DATETIME NULL;
END
GO

-- Chỉ kích hoạt sẵn các tài khoản mẫu ban đầu của hệ thống
-- TUYỆT ĐỐI KHÔNG tự động kích hoạt tài khoản mới đăng ký chưa xác thực OTP
UPDATE [dbo].[User] 
SET [isActive] = 1 
WHERE [username] IN ('admin', 'Nganh', 'student', '24110004');
GO

-- 2. Tạo bảng Videos có quan hệ 1-N với [User] trong database baitapweb
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Videos]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[Videos] (
        [videoId] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [title] NVARCHAR(255) NOT NULL,
        [description] NVARCHAR(MAX) NULL,
        [videoPath] NVARCHAR(500) NOT NULL,
        [thumbnail] NVARCHAR(500) NULL,
        [uploadDate] DATETIME NOT NULL DEFAULT GETDATE(),
        [views] INT NOT NULL DEFAULT 0,
        [status] INT NOT NULL DEFAULT 1,
        [userId] INT NOT NULL,
        CONSTRAINT [FK_Videos_User] FOREIGN KEY ([userId])
            REFERENCES [dbo].[User]([id]) ON DELETE CASCADE
    );

    CREATE INDEX IX_Videos_uploadDate ON [dbo].[Videos]([uploadDate] DESC);
    CREATE INDEX IX_Videos_userId ON [dbo].[Videos]([userId]);
    PRINT 'Đã tạo bảng dbo.Videos trong database baitapweb thành công!';
END
ELSE
BEGIN
    PRINT 'Bảng dbo.Videos đã tồn tại trong database baitapweb.';
END
GO

-- 3. Chèn 12 video mẫu gắn với các tài khoản để phục vụ test Top 10 và Phân trang 6 video/trang
IF NOT EXISTS (SELECT 1 FROM [dbo].[Videos])
BEGIN
    -- Lấy userId hợp lệ từ bảng User
    DECLARE @adminId INT = (SELECT TOP 1 id FROM [dbo].[User] WHERE username = 'admin');
    DECLARE @nganhId INT = (SELECT TOP 1 id FROM [dbo].[User] WHERE username = 'Nganh');
    DECLARE @studentId INT = (SELECT TOP 1 id FROM [dbo].[User] WHERE username = 'student');
    DECLARE @anhId INT = (SELECT TOP 1 id FROM [dbo].[User] WHERE username = '24110004');

    IF @adminId IS NULL SET @adminId = (SELECT TOP 1 id FROM [dbo].[User]);
    IF @nganhId IS NULL SET @nganhId = @adminId;
    IF @studentId IS NULL SET @studentId = @adminId;
    IF @anhId IS NULL SET @anhId = @adminId;

    INSERT INTO [dbo].[Videos] ([title], [description], [videoPath], [thumbnail], [uploadDate], [views], [status], [userId])
    VALUES
    (N'Giới thiệu Trường Đại học Công nghệ Kỹ thuật', N'Video tổng quan về các ngành đào tạo, cơ sở vật chất và môi trường học tập hiện đại tại trường.', N'sample-video-1.mp4', N'thumb-1.jpg', DATEADD(minute, -10, GETDATE()), 350, 1, @adminId),
    (N'Hướng dẫn Lập trình Web với Servlet và JSP', N'Bài giảng chi tiết về mô hình MVC 3 tầng, vòng đời của Servlet và cách truyền dữ liệu sang JSP.', N'sample-video-2.mp4', N'thumb-2.jpg', DATEADD(minute, -20, GETDATE()), 120, 1, @nganhId),
    (N'Lập trình Cơ sở dữ liệu với JDBC và SQL Server', N'Kết nối CSDL an toàn, sử dụng PreparedStatement chống SQL Injection, quản lý Connection pool.', N'sample-video-3.mp4', N'thumb-3.jpg', DATEADD(minute, -30, GETDATE()), 88, 1, @adminId),
    (N'Tìm hiểu Hibernate & JPA trong ứng dụng Enterprise', N'Ánh xạ Object-Relational Mapping (ORM), quản lý EntityManager, Transaction và Named Query.', N'sample-video-4.mp4', N'thumb-4.jpg', DATEADD(minute, -40, GETDATE()), 215, 1, @nganhId),
    (N'Bảo mật Mật khẩu người dùng với thuật toán BCrypt', N'Tại sao không nên dùng MD5/SHA-1? Cơ chế Salt ngẫu nhiên và kiểm tra mật khẩu an toàn với BCrypt.', N'sample-video-5.mp4', N'thumb-5.jpg', DATEADD(minute, -50, GETDATE()), 450, 1, @studentId),
    (N'Xác thực tài khoản hai bước qua OTP Email', N'Tích hợp Jakarta Mail, sinh mã OTP an toàn với SecureRandom và cơ chế hết hạn chống spam.', N'sample-video-6.mp4', N'thumb-6.jpg', DATEADD(minute, -60, GETDATE()), 310, 1, @anhId),
    (N'Upload File & Video với MultipartConfig trong Servlet', N'Xử lý upload an toàn: kiểm tra MIME type, kích thước file, tạo tên UUID ngẫu nhiên và chống path traversal.', N'sample-video-7.mp4', N'thumb-7.jpg', DATEADD(minute, -70, GETDATE()), 175, 1, @adminId),
    (N'Thiết kế giao diện Responsive với Bootstrap & CSS Grid', N'Tối ưu trải nghiệm người dùng trên máy tính và thiết bị di động, thiết kế Card giao diện video đẹp mắt.', N'sample-video-8.mp4', N'thumb-8.jpg', DATEADD(minute, -80, GETDATE()), 95, 1, @studentId),
    (N'Phân trang dữ liệu hiệu năng cao với OFFSET FETCH NEXT', N'Tối ưu truy vấn SQL Server cho các tập dữ liệu lớn thay vì lấy toàn bộ danh sách về bộ nhớ ứng dụng.', N'sample-video-9.mp4', N'thumb-9.jpg', DATEADD(minute, -90, GETDATE()), 60, 1, @nganhId),
    (N'Chống tấn công CSRF & XSS trong ứng dụng Web Java', N'Tạo và xác thực CSRF Token trên Form POST, mã hóa đầu ra với c:out để bảo vệ ứng dụng.', N'sample-video-10.mp4', N'thumb-10.jpg', DATEADD(minute, -100, GETDATE()), 280, 1, @anhId),
    (N'Quản lý Phiên làm việc (Session & Cookie) an toàn', N'Cấu hình HttpOnly, SameSite, Secure và ngăn chặn hiện tượng Session Hijacking.', N'sample-video-11.mp4', N'thumb-11.jpg', DATEADD(minute, -110, GETDATE()), 140, 1, @adminId),
    (N'Xây dựng RESTful API và Tích hợp dịch vụ bên thứ ba', N'Định dạng JSON, thiết kế endpoint chuẩn REST và xử lý phản hồi HTTP Status Code.', N'sample-video-12.mp4', N'thumb-12.jpg', DATEADD(minute, -120, GETDATE()), 190, 1, @studentId);

    PRINT 'Đã chèn 12 video mẫu thành công!';
END
GO
