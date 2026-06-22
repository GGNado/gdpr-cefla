package com.cefla.iot.gdpr.service.impl;

import com.cefla.iot.gdpr.entity.DeleteLog;
import com.cefla.iot.gdpr.repository.DeleteLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.cefla.iot.gdpr.entity.UserDelete;
import com.cefla.iot.gdpr.repository.UserDeleteRepository;
import com.cefla.iot.gdpr.service.UserDeleteService;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDeleteServiceImpl implements UserDeleteService {
    private final UserDeleteRepository userRepository;
    private final DeleteLogRepository logRepository;

    @Override
    public void deleteUsersByLastLogin() {
        userRepository.callDeleteUserBatchSp();
    }

    @Override
    public void deleteUser(String idUtente) {
        userRepository.callDeleteUserSp(idUtente);
    }

    @Override
    public List<UserDelete> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return userRepository.findTop10ByUsernameContainingIgnoreCaseOrNomeContainingIgnoreCaseOrCognomeContainingIgnoreCaseOrEmailContainingIgnoreCase(
            query, query, query, query
        );
    }

    @Override
    public List<com.cefla.iot.gdpr.dto.TableCountDto> dryRun(String idUtente) {
        return userRepository.callCountUserRecordsSp(idUtente);
    }

    @Override
    public List<com.cefla.iot.gdpr.dto.BatchPreviewDto> dryRunBatch() {
        List<Object[]> rawResults = userRepository.callPreviewBatchDeletionSp();
        List<com.cefla.iot.gdpr.dto.BatchPreviewDto> dtos = new java.util.ArrayList<>();
        for (Object[] row : rawResults) {
            com.cefla.iot.gdpr.dto.BatchPreviewDto dto = new com.cefla.iot.gdpr.dto.BatchPreviewDto();
            dto.setIdUtente(row[0] != null ? row[0].toString() : null);
            dto.setNome(row[1] != null ? row[1].toString() : null);
            dto.setCognome(row[2] != null ? row[2].toString() : null);
            dto.setEmail(row[3] != null ? row[3].toString() : null);
            
            if (row[4] != null) {
                if (row[4] instanceof java.util.Date) {
                    dto.setUltimaScadenzaLicenza((java.util.Date) row[4]);
                } else if (row[4] instanceof java.time.LocalDateTime) {
                    dto.setUltimaScadenzaLicenza(java.sql.Timestamp.valueOf((java.time.LocalDateTime) row[4]));
                }
            }
            dtos.add(dto);
        }
        return dtos;
    }
}