package com.greenops.sentinel.repository;

import com.greenops.sentinel.entity.AuditResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<AuditResult, Long> {


}