package ca.uhn.fhir.jpa.interceptor;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.jpa.entity.*;
import ca.uhn.fhir.jpa.repository.FhirAuditLogRepository;

import java.time.Instant;

@Interceptor
@Component
public class FhirAuditLogInterceptor extends InterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(FhirAuditLogInterceptor.class);
    
    @Autowired
    private FhirAuditLogRepository auditLogRepository;

    public FhirAuditLogInterceptor(FhirAuditLogRepository auditLogRepository) {
        
        this.auditLogRepository = auditLogRepository;
    }
    // Log all incoming requests
    @Transactional
    @Async
    @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_HANDLED)
    public void logRequest(RequestDetails theRequestDetails) {
        try {
            String operation = theRequestDetails.getRequestType() != null 
                ? theRequestDetails.getRequestType().name() 
                : "UNKNOWN";

            String resourceType = theRequestDetails.getResourceName() != null 
                ? theRequestDetails.getResourceName() 
                : extractJsonField(theRequestDetails.getRequestContentsIfLoaded(), "resourceType", "N/A");

            String resourceId = theRequestDetails.getId() != null 
                ? theRequestDetails.getId().getIdPart() 
                : extractJsonField(theRequestDetails.getRequestContentsIfLoaded(), "id", "N/A");

            String userAgent = theRequestDetails.getHeaders("User-Agent") != null 
                && !theRequestDetails.getHeaders("User-Agent").isEmpty() 
                ? theRequestDetails.getHeaders("User-Agent").get(0) 
                : "N/A";

            String clientIp = theRequestDetails.getCompleteUrl() != null 
                ? theRequestDetails.getCompleteUrl().split("/")[2] 
                : "N/A";

            logger.info("[FHIR AUDIT] {} {} | Resource: {}/{} | Client: {} | IP: {}",
            operation, 
            theRequestDetails.getCompleteUrl(),
            resourceType, 
            resourceId, 
            userAgent, 
            clientIp);

            // Save to database
            FhirAuditLog logEntry = new FhirAuditLog();
            logEntry.setOperation(operation);
            logEntry.setResourceType(resourceType);
            logEntry.setResourceId(resourceId);
            logEntry.setUserAgent(userAgent);
            logEntry.setclient_ip(clientIp);
            logEntry.setTimestamp(Instant.now());
            auditLogRepository.save(logEntry);

        } catch (Exception e) {
            logger.error("Failed to process audit log", e);
        }
        }

        private String extractJsonField(byte[] requestContents, String fieldName, String defaultValue) {
        if (requestContents == null) {
            return defaultValue;
        }
        try {
            String content = new String(requestContents);
            int fieldIndex = content.indexOf("\"" + fieldName + "\"");
            if (fieldIndex != -1) {
            int colonIndex = content.indexOf(":", fieldIndex);
            if (colonIndex != -1) {
                int startQuoteIndex = content.indexOf("\"", colonIndex);
                int endQuoteIndex = content.indexOf("\"", startQuoteIndex + 1);
                if (startQuoteIndex != -1 && endQuoteIndex != -1) {
                return content.substring(startQuoteIndex + 1, endQuoteIndex);
                }
            }
            }
        } catch (Exception e) {
            logger.warn("Failed to extract field '{}' from JSON content", fieldName, e);
        }
        return defaultValue;
        }
    
    @Transactional
    @Async
    @Hook(Pointcut.SERVER_HANDLE_EXCEPTION)
    public void logError(RequestDetails theRequestDetails, BaseServerResponseException theException) {
        
        logger.error(
            "[FHIR Audit] ERROR: {} | Message: {}",
            theRequestDetails.getCompleteUrl(),
            theException.getMessage()
        );

    }
}