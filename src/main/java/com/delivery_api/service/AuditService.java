package com.delivery_api.service;

/**
 * Interface para o serviço de auditoria.
 * Define o contrato para registrar eventos críticos de segurança e ações de usuários.
 */
public interface AuditService {

    /**
     * Registra uma ação realizada por um usuário no sistema.
     *
     * @param userId ID do usuário que realizou a ação.
     * @param action Ação realizada (ex: "CREATE", "UPDATE", "DELETE").
     * @param resource O recurso que foi afetado (ex: "Restaurante", "Pedido").
     * @param details Detalhes adicionais sobre o evento, geralmente o objeto ou dados envolvidos.
     */
    void logUserAction(String userId, String action, String resource, Object details);

    /**
     * Registra um evento de segurança, como uma tentativa de login ou acesso negado.
     *
     * @param event Descrição do evento de segurança (ex: "LOGIN_ATTEMPT", "ACCESS_DENIED").
     * @param details Detalhes contextuais sobre o evento.
     * @param success Indica se o evento foi bem-sucedido (ex: login com sucesso).
     */
    void logSecurityEvent(String event, String details, boolean success);
}