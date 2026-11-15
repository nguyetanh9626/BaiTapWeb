-- ============================================================
-- Script SQL tạo bảng Category & dữ liệu mẫu (SQL Server)
-- Chạy trên database hiện có [baitapweb] (không ảnh hưởng bảng [User])
-- ============================================================

IF DB_ID(N'baitapweb') IS NULL
BEGIN
    CREATE DATABASE [baitapweb];
END
GO

USE [baitapweb];
GO

IF OBJECT_ID(N'dbo.Category', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[Category] (
        [cate_id]   INT IDENTITY(1,1) NOT NULL,
        [cate_name] NVARCHAR(255)     NOT NULL,
        [icons]     NVARCHAR(255)     NULL,
        PRIMARY KEY CLUSTERED ([cate_id] ASC)
    );
END
GO

-- Dữ liệu mẫu (chỉ thêm khi bảng chưa có dữ liệu)
IF NOT EXISTS (SELECT 1 FROM [dbo].[Category])
BEGIN
    INSERT INTO [dbo].[Category] ([cate_name], [icons]) VALUES
        (N'Áo Thun Nam', N'ao-thun.png'),
        (N'Áo Sơ Mi Nữ', N'ao-so-mi.png'),
        (N'Quần Jean Thời Trang', N'quan-jean.png'),
        (N'Giày Thể Thao', N'giay-the-thao.png'),
        (N'Phụ Kiện & Balo', N'phu-kien.png');
END
GO

-- Câu lệnh kiểm tra dữ liệu Category
SELECT [cate_id], [cate_name], [icons]
FROM [dbo].[Category]
ORDER BY [cate_id] ASC;
GO

-- Câu lệnh kiểm tra tìm kiếm theo tên danh mục
SELECT [cate_id], [cate_name], [icons]
FROM [dbo].[Category]
WHERE [cate_name] LIKE N'%Áo%';
GO
