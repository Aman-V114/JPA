package ca.uhn.fhir.rest.server.interceptor;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ca.uhn.fhir.jpa.starter.repository.FhirAuditLogRepository; // Ensure this is the correct package
import java.time.Instant;
import ca.uhn.fhir.entity.*; // Ensure this is the correct package

@Interceptor
@Component
public class FhirAuditLogInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(FhirAuditLogInterceptor.class);
    private final FhirAuditLogRepository auditLogRepository;

    // Constructor injection
    public FhirAuditLogInterceptor(FhirAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // Log all incoming requests
    @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_HANDLED)
    public void logRequest(RequestDetails theRequestDetails) {
        // Extract request details
        String operation = theRequestDetails.getRequestType().name();
        String resourceType = theRequestDetails.getResourceName();
        String resourceId = theRequestDetails.getId() != null ? theRequestDetails.getId().getIdPart() : null;
        String userAgent = theRequestDetails.getHeader("User-Agent");
        String clientIp = theRequestDetails.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = theRequestDetails.getUserData().get("REMOTE_ADDR").toString();
        }

        // Log to console (SLF4J)
        logger.info(
            "[FHIR Audit] {} {} | Resource: {}/{} | UserAgent: {} | IP: {}",
            operation, theRequestDetails.getCompleteUrl(),
            resourceType, resourceId, userAgent, clientIp
        );

        // Save to database
        FhirAuditLog logEntry = new FhirAuditLog();
        logEntry.setOperation(operation);
        logEntry.setResourceType(resourceType);
        logEntry.setResourceId(resourceId);
        logEntry.setUserAgent(userAgent);
        logEntry.setClientIp(clientIp);
        logEntry.setTimestamp(Instant.now());
        
        auditLogRepository.save(logEntry);
    }

    // Log errors (optional)
    @Hook(Pointcut.SERVER_HANDLE_EXCEPTION)
    public void logError(RequestDetails theRequestDetails, BaseServerResponseException theException) {
        logger.error(
            "[FHIR Audit] ERROR: {} | Message: {}",
            theRequestDetails.getCompleteUrl(),
            theException.getMessage()
        );
    }
}