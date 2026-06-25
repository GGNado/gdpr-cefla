package com.cefla.iot.gdpr.service;

import java.util.List;

import com.cefla.iot.gdpr.entity.primary.DeleteLog;

public interface DeleteLogService {
    DeleteLog save(DeleteLog deleteLog);

    DeleteLog update(DeleteLog deleteLog);

    void deleteById(Long id);

    List<DeleteLog> findAll();

    DeleteLog findById(Long id);
}