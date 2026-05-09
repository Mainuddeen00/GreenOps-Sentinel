package com.greenops.sentinel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AuditResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String instanceId;
    private Double cpuUsage;

    @Column(length = 1000)
    private String aiRecommendation;

    private LocalDateTime auditTime;

    // Default constructor
    public AuditResult() {}

    // Parameterized constructor
    public AuditResult(String instanceId, Double cpuUsage, String aiRecommendation) {
        this.instanceId = instanceId;
        this.cpuUsage = cpuUsage;
        this.aiRecommendation = aiRecommendation;
        this.auditTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public String getAiRecommendation() {
        return aiRecommendation;
    }

    public void setAiRecommendation(String aiRecommendation) {
        this.aiRecommendation = aiRecommendation;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }
}