-- Виконайте цей скрипт у SQL Server Management Studio (SSMS): File -> Open -> File..., потім Execute (F5)

IF DB_ID('dean_office') IS NULL
    CREATE DATABASE dean_office;
GO

USE dean_office;
GO

-- Групи: первинний ключ генерується autoincrement-полем IDENTITY
IF OBJECT_ID('dbo.student_group') IS NULL
CREATE TABLE dbo.student_group (
    id       BIGINT IDENTITY(1,1) PRIMARY KEY,
    name     NVARCHAR(20) NOT NULL UNIQUE,
    capacity INT NOT NULL CHECK (capacity > 0)
);
GO

-- Студенти: первинний ключ теж IDENTITY, зовнішній ключ на групу
IF OBJECT_ID('dbo.student') IS NULL
CREATE TABLE dbo.student (
    id       BIGINT IDENTITY(1,1) PRIMARY KEY,
    surname  NVARCHAR(50) NOT NULL,
    name     NVARCHAR(50) NOT NULL,
    group_id BIGINT NOT NULL REFERENCES dbo.student_group (id)
);
GO

-- Тестові дані
IF NOT EXISTS (SELECT 1 FROM dbo.student_group)
INSERT INTO dbo.student_group (name, capacity) VALUES
    (N'ІК-21', 25), (N'ІК-22', 25), (N'ІК-23', 3), (N'ІК-24', 30), (N'ІК-25', 30);
GO

IF NOT EXISTS (SELECT 1 FROM dbo.student)
INSERT INTO dbo.student (surname, name, group_id)
SELECT v.sn, v.n, g.id
FROM (VALUES
    (N'Шевченко',   N'Олена',   N'ІК-21'),
    (N'Бондаренко', N'Марія',   N'ІК-21'),
    (N'Мельник',    N'Тарас',   N'ІК-21'),
    (N'Коваленко',  N'Андрій',  N'ІК-22'),
    (N'Ткаченко',   N'Дмитро',  N'ІК-22'),
    (N'Крупко',     N'Софія',   N'ІК-23'),
    (N'Петренко',   N'Іван',    N'ІК-23'),
    (N'Кравченко',  N'Наталія', N'ІК-24')
) AS v(sn, n, gn)
JOIN dbo.student_group g ON g.name = v.gn;
GO

SELECT * FROM dbo.student_group;
SELECT * FROM dbo.student;
