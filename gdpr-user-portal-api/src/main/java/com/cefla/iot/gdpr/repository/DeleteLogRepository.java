package com.cefla.iot.gdpr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cefla.iot.gdpr.entity.DeleteLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeleteLogRepository extends JpaRepository<DeleteLog, Long> {
    List<DeleteLog> findAllByOrderByDeletedAtDesc();
}