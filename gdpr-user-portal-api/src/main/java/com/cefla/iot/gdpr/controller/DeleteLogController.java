package com.cefla.iot.gdpr.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.cefla.iot.gdpr.entity.DeleteLog;
import com.cefla.iot.gdpr.service.DeleteLogService;

@RestController
@RequestMapping("/api/deleteLogs")
@RequiredArgsConstructor
public class DeleteLogController {
    private final DeleteLogService deleteLogService;

    @GetMapping("/logs")
    public ResponseEntity<List<DeleteLog>> getLogs() {
        return ResponseEntity.ok(deleteLogService.findAll());
    }
}