USE [baitapweb];
GO

-- ====================================================================
-- Script Migration v3: Bổ sung lưu trữ Cloudinary Avatar cho bảng [User]
-- An toàn: Kiểm tra trước khi thêm cột, không làm mất bất kỳ dữ liệu cũ nào
-- ====================================================================

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[User]') AND name = 'avatarPublicId')
BEGIN
    ALTER TABLE [dbo].[User] ADD [avatarPublicId] NVARCHAR(255) NULL;
    PRINT 'Đã bổ sung cột avatarPublicId vào bảng dbo.[User] thành công!';
END
ELSE
BEGIN
    PRINT 'Cột avatarPublicId đã tồn tại trong bảng dbo.[User].';
END
GO
