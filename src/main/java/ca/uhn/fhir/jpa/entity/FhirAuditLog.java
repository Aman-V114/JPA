package ca.uhn.fhir.jpa.entity;

import jakarta.persistence.*;

import jakarta.persistence.Entity;

import java.time.Instant;

@Entity
@Table(name = "fhir_audit_log")
public class FhirAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String operation;          // e.g., "CREATE", "READ", "UPDATE"
    private String resourceType;       // e.g., "Patient", "Observation"
    private String resourceId;         // e.g., "123"
    private String userAgent;          // Client (e.g., "Postman/7.28.4")
    private String clientIp;           // e.g., "192.168.1.1"
    private Instant timestamp;         // Time of the request

    // Getters and setters (generate these in your IDE)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}