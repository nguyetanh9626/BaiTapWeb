USE [baitapweb];
GO

-- Cập nhật lại tiếng Việt chuẩn Unicode cho 12 video mẫu
UPDATE [dbo].[Videos] SET 
    [title] = N'Giới thiệu Trường Đại học Công nghệ Kỹ thuật',
    [description] = N'Video tổng quan về các ngành đào tạo, cơ sở vật chất và môi trường học tập hiện đại tại trường.'
WHERE [videoId] = 1 OR [videoPath] = 'sample-video-1.mp4';

UPDATE [dbo].[Videos] SET 
    [title] = N'Hướng dẫn Lập trình Web với Servlet và JSP',
    [description] = N'Bài giảng chi tiết về mô hình MVC 3 tầng, vòng đời của Servlet và cách truyền dữ liệu sang JSP.'
WHERE [videoId] = 2 OR [videoPath] = 'sample-video-2.mp4';

UPDATE [dbo].[Videos] SET 
    [title] = N'Lập trình Cơ sở dữ liệu với JDBC và SQL Server',
    [description] = N'Kết nối CSDL an toàn, sử dụng PreparedStatement chống SQL Injection, quản lý Connection pool.'
WHERE [videoId] = 3 OR [videoPath] = 'sample-video-3.mp4';

UPDATE [dbo].[Videos] SET 
    [title] = N'Tìm hiểu Hibernate & JPA trong ứng dụng Enterprise',
    [description] = N'Ánh xạ Object-Relational Mapping (ORM), quản lý EntityManager, Transaction và Named Query.'
WHERE [videoId] = 4 OR [videoPath] = 'sample-video-4.mp4';

UPDATE [dbo].[Videos] SET 
    [title] = N'Bảo mật Mật khẩu người dùng với thuật toán BCrypt',
    [description] = N'Tại sao không nên dùng MD5/SHA-1? Cơ chế Salt ngẫu nhiên và kiểm tra mật khẩu an toàn với BCrypt.'
WHERE [videoId] = 5 OR [videoPath] = 'sample-video-5.mp4';

UPDATE [dbo].[Videos] SET 
    [title] = N'Xác thực tài khoản hai bước qua OTP Email',
    [description] = N'Tích hợp Jakarta Mail, sinh mã OTP an toàn với SecureRandom và cơ chế hết hạn chống spam.'
WHERE [videoId] = 6 OR [videoPath] = 'sample-video-6.mp4';

UPDATE [dbo].[Videos] SET 
    [title] = N'Upload File & Video với MultipartConfig trong Servlet',
    [description] = N'Xử lý upload an toàn: kiểm tra MIME type, kích thước file, tạo tên UUID ngẫu nhiên và chống path traversal.'
WHERE [videoId] = 7 OR [videoPath] = 'sample-video-7.mp4';

UPDATE [dbo].[Videos] SET 
    [title] = N'Thiết kế giao diện Responsive với Bootstrap & CSS Grid',
    [description] = N'Tối ưu trải nghiệm người dùng trên máy tính và thiết bị di động, thiết kế Card giao diện video đẹp mắt.'
WHERE [videoId] = 8 OR [videoPath] = 'sample-video-8.mp4';

UPDATE [dbo].[Videos] SET 
    [title] = N'Phân trang dữ liệu hiệu năng cao với OFFSET FETCH NEXT',
    [description] = N'Tối ưu truy vấn SQL Server cho các tập dữ liệu lớn thay vì lấy toàn bộ danh sách về bộ nhớ ứng dụng.'
WHERE [videoId] = 9 OR [videoPath] = 'sample-video-9.mp4';

UPDATE [dbo].[Videos] SET 
    [title] = N'Chống tấn công CSRF & XSS trong ứng dụng Web Java',
    [description] = N'Tạo và xác thực CSRF Token trên Form POST, mã hóa đầu ra với c:out để bảo vệ ứng dụng.'
WHERE [videoId] = 10 OR [videoPath] = 'sample-video-10.mp4';

UPDATE [dbo].[Videos] SET 
    [title] = N'Quản lý Phiên làm việc (Session & Cookie) an toàn',
    [description] = N'Cấu hình HttpOnly, SameSite, Secure và ngăn chặn hiện tượng Session Hijacking.'
WHERE [videoId] = 11 OR [videoPath] = 'sample-video-11.mp4';

UPDATE [dbo].[Videos] SET 
    [title] = N'Xây dựng RESTful API và Tích hợp dịch vụ bên thứ ba',
    [description] = N'Định dạng JSON, thiết kế endpoint chuẩn REST và xử lý phản hồi HTTP Status Code.'
WHERE [videoId] = 12 OR [videoPath] = 'sample-video-12.mp4';

PRINT N'Đã cập nhật tiếng Việt chuẩn cho toàn bộ 12 video trong database baitapweb!';
GO
