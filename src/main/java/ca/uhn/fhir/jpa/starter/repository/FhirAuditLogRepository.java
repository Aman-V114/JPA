package ca.uhn.fhir.jpa.starter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.uhn.fhir.entity.FhirAuditLog;

public interface FhirAuditLogRepository extends JpaRepository<FhirAuditLog, Long> {
}