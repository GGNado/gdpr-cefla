package com.cefla.iot.gdpr.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.cefla.iot.gdpr.entity.DeleteLog;
import com.cefla.iot.gdpr.repository.DeleteLogRepository;
import com.cefla.iot.gdpr.service.DeleteLogService;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteLogServiceImpl implements DeleteLogService {

    private final DeleteLogRepository deleteLogRepository;

    @Override
    public DeleteLog save(DeleteLog deleteLog) {
        return deleteLogRepository.save(deleteLog);
    }

    @Override
    public DeleteLog update(DeleteLog deleteLog) {
        return deleteLogRepository.save(deleteLog);
    }

    @Override
    public void deleteById(Long id) {
        deleteLogRepository.deleteById(id);
    }

    @Override
    public List<DeleteLog> findAll() {
        return deleteLogRepository.findAllByOrderByDeletedAtDesc();
    }

    @Override
    public DeleteLog findById(Long id) {
        return deleteLogRepository.findById(id).orElse(null);
    }
}