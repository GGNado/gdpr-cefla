package com.cefla.iot.gdpr.controller;

import com.cefla.iot.gdpr.entity.primary.DeleteLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.cefla.iot.gdpr.entity.primary.UserDelete;
import com.cefla.iot.gdpr.service.UserDeleteService;

@RestController
@RequestMapping("/api/userDeletes")
@RequiredArgsConstructor
public class UserDeleteController {
    private final UserDeleteService userDeleteService;

    @DeleteMapping("/delete/{idUtente}")
    public ResponseEntity<String> deleteUser(@PathVariable String idUtente) {
        try {
            userDeleteService.deleteUser(idUtente);
            return ResponseEntity.ok("Utente eliminato con successo.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Errore: " + e.getMessage());
        }
    }
    // TODO: Magari anche eliminazione per Username

    @GetMapping("/search")
    public ResponseEntity<List<UserDelete>> searchUsers(@RequestParam String q) {
        try {
            return ResponseEntity.ok(userDeleteService.searchUsers(q));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/dry-run/{idUtente}")
    public ResponseEntity<List<com.cefla.iot.gdpr.dto.TableCountDto>> dryRun(@PathVariable String idUtente) {
        try {
            return ResponseEntity.ok(userDeleteService.dryRun(idUtente));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/dry-run-batch")
    public ResponseEntity<List<com.cefla.iot.gdpr.dto.BatchPreviewDto>> dryRunBatch() {
        try {
            return ResponseEntity.ok(userDeleteService.dryRunBatch());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete-batch")
    public ResponseEntity<String> runBatchDeletion() {
        try {
            userDeleteService.deleteUsersByLastLogin();
            return ResponseEntity.ok("Batch eseguito.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Errore: " + e.getMessage());
        }
    }
}