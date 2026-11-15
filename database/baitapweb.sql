-- ============================================================
-- Script tạo Database, Bảng [User] & Bảng [Category] cho project baitapweb
-- Tương thích Microsoft SQL Server
-- ============================================================

IF DB_ID(N'baitapweb') IS NULL
BEGIN
    CREATE DATABASE [baitapweb];
END
GO

USE [baitapweb];
GO

-- 1. Bảng [User] phục vụ Login / Register
IF OBJECT_ID(N'dbo.[User]', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[User] (
        [id]          INT IDENTITY(1,1) NOT NULL,
        [email]       NVARCHAR(255)     NOT NULL,
        [username]    NVARCHAR(100)     NOT NULL,
        [fullname]    NVARCHAR(255)     NOT NULL,
        [password]    NVARCHAR(255)     NOT NULL,
        [avatar]      NVARCHAR(500)     NULL,
        [roleid]      INT               NOT NULL CONSTRAINT [DF_User_RoleId] DEFAULT (5),
        [phone]       NVARCHAR(20)      NOT NULL,
        [createddate] DATE              NOT NULL CONSTRAINT [DF_User_CreatedDate] DEFAULT (CAST(GETDATE() AS DATE)),

        CONSTRAINT [PK_User] PRIMARY KEY CLUSTERED ([id] ASC),
        CONSTRAINT [UQ_User_Email] UNIQUE NONCLUSTERED ([email] ASC),
        CONSTRAINT [UQ_User_Username] UNIQUE NONCLUSTERED ([username] ASC),
        CONSTRAINT [UQ_User_Phone] UNIQUE NONCLUSTERED ([phone] ASC)
    );
END
GO

IF NOT EXISTS (SELECT 1 FROM [dbo].[User] WHERE [username] = N'admin')
BEGIN
    INSERT INTO [dbo].[User] ([email], [username], [fullname], [password], [avatar], [roleid], [phone], [createddate])
    VALUES (N'admin@hcmute.edu.vn', N'admin', N'Quản Trị Viên', N'123456', NULL, 1, N'0901000001', CAST(GETDATE() AS DATE));
END
GO

IF NOT EXISTS (SELECT 1 FROM [dbo].[User] WHERE [username] = N'Nganh')
BEGIN
    INSERT INTO [dbo].[User] ([email], [username], [fullname], [password], [avatar], [roleid], [phone], [createddate])
    VALUES (N'nganh@hcmute.edu.vn', N'Nganh', N'Nganh', N'123', NULL, 2, N'0901000002', CAST(GETDATE() AS DATE));
END
GO

IF NOT EXISTS (SELECT 1 FROM [dbo].[User] WHERE [username] = N'student')
BEGIN
    INSERT INTO [dbo].[User] ([email], [username], [fullname], [password], [avatar], [roleid], [phone], [createddate])
    VALUES (N'student@hcmute.edu.vn', N'student', N'Sinh Viên UTE', N'123', NULL, 5, N'0901000003', CAST(GETDATE() AS DATE));
END
GO

-- 2. Bảng [Category] phục vụ CRUD Danh mục (theo 14_HD_Servlet_JDBC_CRUD.pdf)
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

SELECT [id], [email], [username], [fullname], [roleid], [phone], [createddate]
FROM [dbo].[User];
GO

SELECT [cate_id], [cate_name], [icons]
FROM [dbo].[Category];
GO
