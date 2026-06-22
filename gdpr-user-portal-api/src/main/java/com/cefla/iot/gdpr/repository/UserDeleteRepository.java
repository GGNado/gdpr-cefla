package com.cefla.iot.gdpr.repository;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cefla.iot.gdpr.entity.UserDelete;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeleteRepository extends JpaRepository<UserDelete, String> {
    @Modifying
    @Transactional
    @Query(value = "EXEC dbo.DeleteUser @idUtente = :idUtente", nativeQuery = true)
    void callDeleteUserSp(@Param("idUtente") String idUtente);

    // Esegue la SP batch schedulata
    @Modifying
    @Transactional
    @Query(value = "EXEC dbo.DeleteUserByLastLogin", nativeQuery = true)
    void callDeleteUserBatchSp();

    List<UserDelete> findTop10ByUsernameContainingIgnoreCaseOrNomeContainingIgnoreCaseOrCognomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String nome, String cognome, String email);

    @Query(value = "EXEC [dbo].[CountUserRecords] @idUtente = :idUtente", nativeQuery = true)
    List<com.cefla.iot.gdpr.dto.TableCountDto> callCountUserRecordsSp(@Param("idUtente") String idUtente);

    @Query(value = "EXEC [dbo].[PreviewBatchDeletion]", nativeQuery = true)
    List<Object[]> callPreviewBatchDeletionSp();
}