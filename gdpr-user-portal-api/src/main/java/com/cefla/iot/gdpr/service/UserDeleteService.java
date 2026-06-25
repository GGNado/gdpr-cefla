package com.cefla.iot.gdpr.service;

import java.util.List;

import com.cefla.iot.gdpr.entity.primary.UserDelete;

public interface UserDeleteService {
    void deleteUsersByLastLogin();
    void deleteUser(String idUtente);
    List<UserDelete> searchUsers(String query);
    List<com.cefla.iot.gdpr.dto.TableCountDto> dryRun(String idUtente);
    List<com.cefla.iot.gdpr.dto.BatchPreviewDto> dryRunBatch();
}