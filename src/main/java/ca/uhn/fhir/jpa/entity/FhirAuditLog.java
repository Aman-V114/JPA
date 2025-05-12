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
    private String resource_type;       // e.g., "Patient", "Observation"
    private String resource_id;         // e.g., "123"
    private String user_agent;          // Client (e.g., "Postman/7.28.4")
    private String client_ip;           // e.g., "192.168.1.1"
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
        return resource_type;
    }

    public void setResourceType(String resourceType) {
        this.resource_type = resourceType;
    }

    public String getResourceId() {
        return resource_id;
    }

    public void setResourceId(String resource_id) {
        this.resource_id = resource_id;
    }

    public String getUserAgent() {
        return user_agent;
    }

    public void setUserAgent(String user_agent) {
        this.user_agent = user_agent;
    }

    public String getclient_ip() {
        return client_ip;
    }

    public void setclient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}