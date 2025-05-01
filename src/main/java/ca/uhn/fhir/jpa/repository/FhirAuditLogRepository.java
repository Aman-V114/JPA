package ca.uhn.fhir.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.uhn.fhir.jpa.entity.FhirAuditLog;

public interface FhirAuditLogRepository extends JpaRepository<FhirAuditLog, Long> {
}