package com.delivery_api.service.impl;

import com.delivery_api.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuditServiceImpl implements AuditService {

    // Logger específico "AUDIT" que configuramos no logback-spring.xml
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void logUserAction(String userId, String action, String resource, Object details) {
        Map<String, Object> auditEvent = new LinkedHashMap<>();
        auditEvent.put("timestamp", Instant.now().toString());
        auditEvent.put("type", "USER_ACTION");
        auditEvent.put("userId", userId);
        auditEvent.put("action", action);
        auditEvent.put("resource", resource);
        auditEvent.put("details", details);
        auditEvent.put("correlationId", MDC.get("correlationId"));

        log(auditEvent);
    }

    @Override
    public void logSecurityEvent(String event, String details, boolean success) {
        Map<String, Object> securityEvent = new LinkedHashMap<>();
        securityEvent.put("timestamp", Instant.now().toString());
        securityEvent.put("type", "SECURITY_EVENT");
        securityEvent.put("event", event);
        securityEvent.put("details", details);
        securityEvent.put("success", success);
        securityEvent.put("correlationId", MDC.get("correlationId"));

        log(securityEvent);
    }

    /**
     * Método privado auxiliar para converter o evento em JSON e registrar no log.
     * @param event O mapa contendo os dados do evento de auditoria.
     */
    private void log(Map<String, Object> event) {
        try {
            String jsonLog = objectMapper.writeValueAsString(event);
            auditLogger.info(jsonLog);
        } catch (Exception e) {
            // Loga um erro no logger padrão caso a serialização para JSON falhe
            LoggerFactory.getLogger(AuditServiceImpl.class).error("Erro ao registrar evento de auditoria", e);
        }
    }
}