package com.greenops.sentinel.controller;

import com.greenops.sentinel.entity.AuditResult;
import com.greenops.sentinel.repository.AuditRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audits")
public class AuditController {

    private final AuditRepository auditRepository;

    // Inject the repository so we can read from the database
    public AuditController(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    // This creates an endpoint at http://localhost:8080/api/audits
    @GetMapping
    public List<AuditResult> getAllAudits() {
        return auditRepository.findAll();
    }
}