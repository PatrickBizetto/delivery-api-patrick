package com.delivery_api.service.impl;

import com.delivery_api.service.AlertService;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);
    
    // Injeção da dependência para acessar as métricas da aplicação.
    private final MeterRegistry meterRegistry;

    // Constantes privadas que definem os limites (thresholds) para os alertas.
    private static final double ERROR_RATE_THRESHOLD = 0.1; // 10%
    private static final double RESPONSE_TIME_THRESHOLD_MS = 1000; // 1 segundo

    public AlertServiceImpl(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Implementação do método agendado. A anotação @Scheduled fica aqui,
     * pois o agendamento é um detalhe da implementação.
     */
    @Override
    @Scheduled(fixedRate = 60000) // Roda a cada 60 segundos
    public void verificarAlertas() {
        logger.info("Executando verificação de alertas...");
        verificarTaxaDeErro();
        verificarTempoDeResposta();
    }

    /**
     * Lógica privada para verificar a taxa de erro.
     * Não faz parte do contrato público.
     */
    private void verificarTaxaDeErro() {
        double totalRequests = getCounterValue("delivery_pedidos_total");
        double errorRequests = getCounterValue("delivery_pedidos_erro_total");

        if (totalRequests > 10) { // Só alerta se houver um volume mínimo de requisições.
            double errorRate = errorRequests / totalRequests;
            if (errorRate > ERROR_RATE_THRESHOLD) {
                enviarAlerta("CRITICAL", "HIGH_ERROR_RATE",
                    String.format("Taxa de erro de %.2f%% excede o limite de %.2f%%",
                        errorRate * 100, ERROR_RATE_THRESHOLD * 100));
            }
        }
    }
    
    /**
     * Lógica privada para verificar o tempo médio de resposta.
     */
    private void verificarTempoDeResposta() {
        // O nome da métrica foi ajustado para buscar segundos, conforme o gabarito.
        double avgResponseTimeInMillis = getTimerMean("delivery_pedido_processamento_seconds"); 
        if (avgResponseTimeInMillis > RESPONSE_TIME_THRESHOLD_MS) {
         enviarAlerta("WARNING", "HIGH_RESPONSE_TIME",
                String.format("Tempo médio de resposta de %.2fms excede o limite de %.0fms",
                    avgResponseTimeInMillis, RESPONSE_TIME_THRESHOLD_MS));
    }
    }

    /**
     * Método privado responsável por formatar e logar o alerta.
     */
    private void enviarAlerta(String severidade, String tipo, String mensagem) {
        logger.warn("ALERTA DISPARADO! Severidade: [{}], Tipo: [{}], Mensagem: {}", severidade, tipo, mensagem);
        // Em um cenário real, aqui você integraria com PagerDuty, Slack, E-mail, etc.
    }

    // Métodos auxiliares privados para buscar os valores das métricas.
    private double getCounterValue(String name) {
        // O `find(name).counter()` pode retornar null se a métrica não existir.
        return meterRegistry.find(name).counter() != null ? meterRegistry.find(name).counter().count() : 0.0;
    }
    
    private double getTimerMean(String name) {
        // Retorna a média em segundos, a unidade base do Timer.
        return meterRegistry.find(name).timer() != null ? meterRegistry.find(name).timer().mean(java.util.concurrent.TimeUnit.MILLISECONDS) : 0.0;
    }
}