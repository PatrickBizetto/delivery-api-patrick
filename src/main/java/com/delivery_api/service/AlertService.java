package com.delivery_api.service;

/**
 * Interface para o serviço de monitoramento e envio de alertas.
 * Define o contrato para as operações de verificação de métricas e disparo de alertas.
 */
public interface AlertService {

    /**
     * Método principal que é executado periodicamente para verificar
     * todas as regras de alerta configuradas na aplicação, como taxa de erro
     * e tempo de resposta.
     */
    void verificarAlertas();

}