CREATE DATABASE cefladb_iot;
GO
USE cefladb_iot;
GO

CREATE TABLE DeleteLog ( 
    Id INT IDENTITY(1,1) PRIMARY KEY, 
    LogFileName VARCHAR(200), 
    TableName VARCHAR(100), 
    RecordId VARCHAR(100), 
    DeletedAt DATETIME, 
    RowsAffected INT, 
    ErrorMessage VARCHAR(MAX) NULL 
);
GO

CREATE TABLE portale_utente (
    IdUtente UNIQUEIDENTIFIER PRIMARY KEY,
    USERNAME NVARCHAR(400) NOT NULL,
    AD BIT NOT NULL DEFAULT 0,
    Password VARCHAR(200),
    Attivo BIT NOT NULL DEFAULT 1,
    DataCreazione_Utente DATE,
    DataModifica_Utente DATE,
    UtenteCreazione VARCHAR(200),
    UtenteModifica VARCHAR(200),
    Nome NVARCHAR(400),
    Cognome NVARCHAR(400),
    Email NVARCHAR(400),
    Culture VARCHAR(10),
    IdAccountDealer UNIQUEIDENTIFIER,
    Accountamministratore BIT NOT NULL DEFAULT 0
);
GO

INSERT INTO portale_utente (IdUtente, USERNAME, Nome, Cognome, Email, Attivo) VALUES
('60EBF6EE-813E-4D1C-A904-000C16A5DE23', 'joseluis.padilla@henryschein.es', 'Jose Luis', 'Padilla', 'joseluis.padilla@henryschein.es', 1),
('4031C5FB-769F-4492-AC0F-000C2F6B1D94', 'manager@hamiltonpediatrics.ca', 'Manager', 'Hamilton', 'manager@hamiltonpediatrics.ca', 1),
('99AF6920-9BBC-454A-AF67-002C4532AA89', 'nathalie.sack@henryschein.de', 'Nathalie', 'Sack', 'nathalie.sack@henryschein.de', 1),
('FAD921B5-8CA6-4C42-9679-0030FFE56927', 'tecniconavarro2@gmail.com', 'Tecnico', 'Navarro', 'tecniconavarro2@gmail.com', 1),
('550E8400-E29B-41D4-A716-446655440000', 'mario.rossi@cefla.it', 'Mario', 'Rossi', 'mario.rossi@cefla.it', 1);
GO


CREATE PROCEDURE [dbo].[DeleteUser] 
    @idUtente UNIQUEIDENTIFIER 
AS 
BEGIN 
    PRINT 'Mock DeleteUser eseguita con successo su utente: ' + CAST(@idUtente AS VARCHAR(50));
    INSERT INTO DeleteLog (LogFileName, TableName, RecordId, DeletedAt, RowsAffected, ErrorMessage) 
    VALUES ('Mock_DeleteUser.log', 'TEST_MOCK', CAST(@idUtente AS VARCHAR(50)), GETDATE(), 1, NULL);
END
GO

CREATE PROCEDURE [dbo].[DeleteUserByLastLogin] 
AS 
BEGIN 
    PRINT 'Mock DeleteUserByLastLogin (Batch) eseguita con successo';
    INSERT INTO DeleteLog (LogFileName, TableName, RecordId, DeletedAt, RowsAffected, ErrorMessage) 
    VALUES ('Mock_DeleteUserBatch.log', 'TEST_MOCK_BATCH', 'BATCH', GETDATE(), 5, NULL);
END
GO

CREATE PROCEDURE [dbo].[CountUserRecords]
    @idUtente UNIQUEIDENTIFIER
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 'portale_utente' AS TableName, 1 AS RecordCount
    UNION ALL
    SELECT 'account_portale_utente' AS TableName, 2 AS RecordCount
    UNION ALL
    SELECT 'portale_utente_macchina' AS TableName, 5 AS RecordCount
    UNION ALL
    SELECT 'licenza' AS TableName, 1 AS RecordCount;
END
GO

CREATE PROCEDURE [dbo].[PreviewBatchDeletion]
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        CAST('60EBF6EE-813E-4D1C-A904-000C16A5DE23' AS UNIQUEIDENTIFIER) AS IdUtente,
        'Jose Luis' AS Nome,
        'Padilla' AS Cognome,
        'joseluis.padilla@henryschein.es' AS Email,
        CAST('2022-01-01' AS DATETIME) AS UltimaScadenzaLicenza
    UNION ALL
    SELECT 
        CAST('99AF6920-9BBC-454A-AF67-002C4532AA89' AS UNIQUEIDENTIFIER) AS IdUtente,
        'Nathalie' AS Nome,
        'Sack' AS Cognome,
        'nathalie.sack@henryschein.de' AS Email,
        CAST('2021-05-15' AS DATETIME) AS UltimaScadenzaLicenza;
END
GO
