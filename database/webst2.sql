-- ============================================================
-- Script SQL khởi tạo database webst2 và các bảng JPA (categories & Videos)
-- Hỗ trợ chuẩn JPA 3.0 / Hibernate 7.4.x cho SQL Server
-- ============================================================

IF DB_ID(N'webst2') IS NULL
BEGIN
    CREATE DATABASE [webst2];
END
GO

USE [webst2];
GO

-- 1. Bảng categories
IF OBJECT_ID(N'dbo.categories', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[categories] (
        [CategoryId]   INT IDENTITY(1,1) NOT NULL,
        [CategoryName] NVARCHAR(200)     NOT NULL,
        [Images]       NVARCHAR(500)     NULL,
        [status]       INT               DEFAULT 1,
        CONSTRAINT [PK_categories] PRIMARY KEY CLUSTERED ([CategoryId] ASC)
    );
END
GO

-- 2. Bảng Videos
IF OBJECT_ID(N'dbo.Videos', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[Videos] (
        [VideoId]      VARCHAR(255)      NOT NULL,
        [Active]       BIT               DEFAULT 1,
        [Description]  NVARCHAR(MAX)     NULL,
        [Poster]       NVARCHAR(500)     NULL,
        [Title]        NVARCHAR(500)     NULL,
        [Views]        INT               DEFAULT 0,
        [CategoryId]   INT               NULL,
        CONSTRAINT [PK_Videos] PRIMARY KEY CLUSTERED ([VideoId] ASC),
        CONSTRAINT [FK_Videos_categories] FOREIGN KEY ([CategoryId]) 
            REFERENCES [dbo].[categories] ([CategoryId])
            ON DELETE SET NULL
            ON UPDATE CASCADE
    );
END
GO

-- 3. Đảm bảo cấu trúc cột Description là NVARCHAR(MAX) và Active là BIT
IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_NAME = 'Videos' AND COLUMN_NAME = 'Description' AND CHARACTER_MAXIMUM_LENGTH <> -1
)
BEGIN
    ALTER TABLE [dbo].[Videos] ALTER COLUMN [Description] NVARCHAR(MAX) NULL;
END
GO

IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_NAME = 'Videos' AND COLUMN_NAME = 'Active' AND DATA_TYPE <> 'bit'
)
BEGIN
    ALTER TABLE [dbo].[Videos] ALTER COLUMN [Active] BIT NULL;
END
GO

-- Đảm bảo Foreign Key có hành vi ON DELETE SET NULL để tránh xung đột khi xóa Category
DECLARE @FkName NVARCHAR(128) = (
    SELECT TOP 1 f.name 
    FROM sys.foreign_keys f 
    WHERE OBJECT_NAME(f.parent_object_id) = 'Videos' 
      AND f.delete_referential_action_desc <> 'SET_NULL'
);
IF @FkName IS NOT NULL
BEGIN
    DECLARE @DropSql NVARCHAR(MAX) = N'ALTER TABLE [dbo].[Videos] DROP CONSTRAINT [' + @FkName + N'];';
    EXEC sp_executesql @DropSql;
    ALTER TABLE [dbo].[Videos] ADD CONSTRAINT [FK_Videos_categories] 
        FOREIGN KEY ([CategoryId]) REFERENCES [dbo].[categories] ([CategoryId]) 
        ON DELETE SET NULL ON UPDATE CASCADE;
END
GO

-- 4. Thêm dữ liệu mẫu ban đầu nếu bảng categories còn trống
IF NOT EXISTS (SELECT 1 FROM [dbo].[categories])
BEGIN
    INSERT INTO [dbo].[categories] ([CategoryName], [Images], [status]) VALUES
        (N'Iphone', N'1794678621391.jpeg', 1),
        (N'Áo Thun Nam JPA', N'1794678577254.jpeg', 1),
        (N'Áo Sơ Mi Nữ', N'1794678651369.jpeg', 1),
        (N'Giày thể thao', N'1794678672331.webp', 1);
END
GO

-- 5. Thêm video mẫu ban đầu nếu bảng Videos còn trống
IF NOT EXISTS (SELECT 1 FROM [dbo].[Videos])
BEGIN
    DECLARE @DefaultCateId INT = (SELECT TOP 1 [CategoryId] FROM [dbo].[categories] ORDER BY [CategoryId] ASC);
    INSERT INTO [dbo].[Videos] ([VideoId], [Active], [Description], [Poster], [Title], [Views], [CategoryId]) VALUES
        ('v01', 1, N'Video test giới thiệu Iphone', NULL, N'Video Test 01', 100, @DefaultCateId);
END
GO

-- Kiểm tra kết quả
SELECT 'categories' AS TableName, COUNT(*) AS TotalRows FROM [dbo].[categories]
UNION ALL
SELECT 'Videos' AS TableName, COUNT(*) AS TotalRows FROM [dbo].[Videos];
GO
