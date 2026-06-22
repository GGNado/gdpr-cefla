-- =====================================================================
-- SCRIPT COMPLETO DI INSTALLAZIONE (PROD / QUALITY)
-- Progetto: Cancellazione Utente GDPR
-- =====================================================================

-- =====================================================================
-- 1. TABELLA DI LOG (DeleteLog)
-- =====================================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'DeleteLog')
BEGIN
    CREATE TABLE [dbo].[DeleteLog] (
        [Id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [LogFileName] VARCHAR(255) NULL,
        [TableName] VARCHAR(100) NULL,
        [RecordId] VARCHAR(100) NULL,
        [DeletedAt] DATETIME DEFAULT GETDATE(),
        [RowsAffected] INT NULL,
        [ErrorMessage] VARCHAR(MAX) NULL,
        [Status] VARCHAR(50) NULL
    );
END
ELSE
BEGIN
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('DeleteLog') AND name = 'Status')
    BEGIN
        ALTER TABLE [dbo].[DeleteLog] ADD [Status] VARCHAR(50) NULL;
    END
END
GO

-- =====================================================================
-- 2. PROCEDURA DI CONTEGGIO DRY-RUN (CountUserRecords)
-- =====================================================================
CREATE OR ALTER PROCEDURE [dbo].[CountUserRecords]
    @idUtente UNIQUEIDENTIFIER
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @idAccountEndUser UNIQUEIDENTIFIER;
    
    SELECT @idAccountEndUser = IdAccountEndUser
    FROM dbo.portale_utente
    WHERE IdUtente = @idUtente;

    DECLARE @Results TABLE (TableName VARCHAR(100), RecordCount INT);

    IF @idAccountEndUser IS NOT NULL
    BEGIN
        INSERT INTO @Results (TableName, RecordCount)
        SELECT 'portale_utente_collegati_diva', COUNT(*) 
        FROM dbo.portale_utente 
        WHERE IdAccountEndUser = @idAccountEndUser 
          AND AbilitatoDiva = 1 
          AND (IsMasterDiva = 0 OR IsMasterDiva IS NULL)
          AND IdUtente <> @idUtente;
    END

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'account_portale_utente', COUNT(*) FROM dbo.account_portale_utente WHERE IdUtente = @idUtente;

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'portale_utente_macchina', COUNT(*) FROM dbo.portale_utente_macchina WHERE IdUtente = @idUtente;

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'portale_utente_pagina', COUNT(*) FROM dbo.portale_utente_pagina WHERE IdUtente = @idUtente;

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'portale_utente_pagina_azione', COUNT(*) FROM dbo.portale_utente_pagina_azione WHERE IdUtente = @idUtente;

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'portale_utente_permission', COUNT(*) FROM dbo.portale_utente_permission WHERE IdUtente = @idUtente;

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'portale_utente_ruolo', COUNT(*) FROM dbo.portale_utente_ruolo WHERE IdUtente = @idUtente;

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'portale_utente_account_pagina', COUNT(*) FROM dbo.portale_utente_account_pagina WHERE IdUtente = @idUtente;

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'portale_utente_account_pagina_azione', COUNT(*) FROM dbo.portale_utente_account_pagina_azione WHERE IdUtente = @idUtente;

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'portale_utente_account_ruolo', COUNT(*) FROM dbo.portale_utente_account_ruolo WHERE IdUtente = @idUtente;

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'licenza', COUNT(*) FROM dbo.licenza WHERE IdUtenteAcquisto = @idUtente;

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'macchina_componente', COUNT(*) FROM dbo.macchina_componente WHERE IdUtenteAutorizzazione = @idUtente;

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'ticket', COUNT(*) FROM dbo.ticket WHERE UserId = @idUtente;

    INSERT INTO @Results (TableName, RecordCount)
    SELECT 'portale_utente', COUNT(*) FROM dbo.portale_utente WHERE IdUtente = @idUtente;

    SELECT TableName, RecordCount FROM @Results WHERE RecordCount > 0;
END
GO

-- =====================================================================
-- 3. PROCEDURA SINGOLA DI CANCELLAZIONE (DeleteUser)
-- =====================================================================
CREATE OR ALTER PROCEDURE [dbo].[DeleteUser] 
    @idUtente UNIQUEIDENTIFIER 
AS 
BEGIN 
    SET NOCOUNT ON;
    
    DECLARE @idAccountEndUser UNIQUEIDENTIFIER;
    DECLARE @LogId VARCHAR(255) = 'DeleteUser_' + REPLACE(REPLACE(REPLACE(CONVERT(VARCHAR, GETDATE(), 120), '-', ''), ':', ''), ' ', '_');
    DECLARE @RowsAffected INT;

    BEGIN TRY
        BEGIN TRANSACTION;

        -- 1. Verifica esistenza
        SELECT @idAccountEndUser = IdAccountEndUser FROM dbo.portale_utente WHERE IdUtente = @idUtente;
        IF @idAccountEndUser IS NULL
        BEGIN
            THROW 50000, 'Utente non trovato.', 1;
        END

        -- 2. Rimozione utenti Diva Slave collegati
        DELETE FROM dbo.portale_utente 
        WHERE IdAccountEndUser = @idAccountEndUser 
          AND AbilitatoDiva = 1 
          AND (IsMasterDiva = 0 OR IsMasterDiva IS NULL)
          AND IdUtente <> @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'portale_utente_collegati_diva', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK');

        -- 3. Cancellazione 9 Tabelle figlie
        DELETE FROM dbo.account_portale_utente WHERE IdUtente = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'account_portale_utente', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK');

        DELETE FROM dbo.portale_utente_macchina WHERE IdUtente = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'portale_utente_macchina', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK');

        DELETE FROM dbo.portale_utente_pagina WHERE IdUtente = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'portale_utente_pagina', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK');

        DELETE FROM dbo.portale_utente_pagina_azione WHERE IdUtente = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'portale_utente_pagina_azione', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK');

        DELETE FROM dbo.portale_utente_permission WHERE IdUtente = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'portale_utente_permission', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK');

        DELETE FROM dbo.portale_utente_ruolo WHERE IdUtente = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'portale_utente_ruolo', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK');

        DELETE FROM dbo.portale_utente_account_pagina WHERE IdUtente = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'portale_utente_account_pagina', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK');

        DELETE FROM dbo.portale_utente_account_pagina_azione WHERE IdUtente = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'portale_utente_account_pagina_azione', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK');

        DELETE FROM dbo.portale_utente_account_ruolo WHERE IdUtente = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'portale_utente_account_ruolo', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK');

        -- 4. Anonimizzazione 3 Tabelle
        UPDATE dbo.licenza SET IdUtenteAcquisto = NULL WHERE IdUtenteAcquisto = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'licenza', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK_ANONIMIZED');

        UPDATE dbo.macchina_componente SET IdUtenteAutorizzazione = NULL WHERE IdUtenteAutorizzazione = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'macchina_componente', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK_ANONIMIZED');

        UPDATE dbo.ticket SET UserId = NULL WHERE UserId = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'ticket', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK_ANONIMIZED');

        -- 5. Cancellazione Record Principale
        DELETE FROM dbo.portale_utente WHERE IdUtente = @idUtente;
        SET @RowsAffected = @@ROWCOUNT;
        IF @RowsAffected > 0 INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, RowsAffected, Status) VALUES (@LogId, 'portale_utente', CAST(@idUtente AS VARCHAR(50)), @RowsAffected, 'OK');

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        INSERT INTO dbo.DeleteLog (LogFileName, TableName, RecordId, ErrorMessage, Status) 
        VALUES (@LogId, 'ERROR', CAST(@idUtente AS VARCHAR(50)), ERROR_MESSAGE(), 'ERROR');
        
        THROW;
    END CATCH
END
GO

-- =====================================================================
-- 4. PROCEDURA BATCH DI AUTO-PULIZIA (DeleteUserByLastLogin)
-- =====================================================================
CREATE OR ALTER PROCEDURE [dbo].[DeleteUserByLastLogin]
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @idUtente UNIQUEIDENTIFIER;
    DECLARE @successCount INT = 0;
    DECLARE @errorCount INT = 0;
    
    DECLARE cur_users CURSOR LOCAL FAST_FORWARD FOR
        SELECT pu.IdUtente
        FROM dbo.portale_utente pu
        OUTER APPLY (
            SELECT TOP 1 DataScadenza 
            FROM dbo.licenza l 
            WHERE l.IdUtenteAcquisto = pu.IdUtente 
            ORDER BY DataScadenza DESC
        ) lic
        WHERE (lic.DataScadenza IS NULL OR lic.DataScadenza < DATEADD(DAY, -1095, GETDATE()))
          AND (pu.IsMasterDiva = 1 OR pu.IsMasterDiva IS NULL);
          
    OPEN cur_users;
    FETCH NEXT FROM cur_users INTO @idUtente;
    
    WHILE @@FETCH_STATUS = 0
    BEGIN
        BEGIN TRY
            EXEC [dbo].[DeleteUser] @idUtente;
            SET @successCount = @successCount + 1;
        END TRY
        BEGIN CATCH
            SET @errorCount = @errorCount + 1;
            INSERT INTO [dbo].[DeleteLog] (LogFileName, TableName, RecordId, ErrorMessage, Status)
            VALUES ('Batch_DeleteUserByLastLogin', 'ERROR', CAST(@idUtente AS VARCHAR(50)), ERROR_MESSAGE(), 'ERROR');
        END CATCH
        
        FETCH NEXT FROM cur_users INTO @idUtente;
    END
    
    CLOSE cur_users;
    DEALLOCATE cur_users;
    
    DECLARE @msg VARCHAR(500) = 'Batch terminato. Successi: ' + CAST(@successCount AS VARCHAR) + ', Errori: ' + CAST(@errorCount AS VARCHAR);
    INSERT INTO [dbo].[DeleteLog] (LogFileName, TableName, RecordId, RowsAffected, Status, ErrorMessage)
    VALUES ('Batch_DeleteUserByLastLogin', 'BATCH_SUMMARY', 'BATCH', @successCount, IIF(@errorCount=0, 'OK', 'WARNING'), @msg);
END
GO

-- =====================================================================
-- 5. PROCEDURA BATCH DRY-RUN (PreviewBatchDeletion)
-- =====================================================================
CREATE OR ALTER PROCEDURE [dbo].[PreviewBatchDeletion]
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        pu.IdUtente, 
        pu.Nome,
        pu.Cognome,
        pu.Email,
        lic.DataScadenza AS UltimaScadenzaLicenza
    FROM dbo.portale_utente pu
    OUTER APPLY (
        SELECT TOP 1 DataScadenza 
        FROM dbo.licenza l 
        WHERE l.IdUtenteAcquisto = pu.IdUtente 
        ORDER BY DataScadenza DESC
    ) lic
    WHERE (lic.DataScadenza IS NULL OR lic.DataScadenza < DATEADD(DAY, -1095, GETDATE()))
      AND (pu.IsMasterDiva = 1 OR pu.IsMasterDiva IS NULL);
END
GO
