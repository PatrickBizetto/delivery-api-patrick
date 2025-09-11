package com.delivery_api.service.impl;

import com.delivery_api.service.MetricsService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementação do serviço de métricas.
 * Responsável por inicializar e gerenciar as métricas customizadas da aplicação.
 */
@Service
public class MetricsServiceImpl implements MetricsService {

    // Registrador central de métricas do Micrometer
    private final MeterRegistry meterRegistry;

    // Métricas
    private final Counter pedidosProcessados;
    private final Counter pedidosComSucesso;
    private final Counter pedidosComErro;
    private final Timer tempoProcessamentoPedido;
    private final AtomicInteger usuariosAtivos = new AtomicInteger(0);

    public MetricsServiceImpl(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // --- INICIALIZAÇÃO DAS MÉTRICAS ---

        // 1. Contadores (Counters): para valores que só aumentam.
        this.pedidosProcessados = Counter.builder("delivery_pedidos_total")
                .description("Total de pedidos processados")
                .register(meterRegistry);

        this.pedidosComSucesso = Counter.builder("delivery_pedidos_sucesso_total")
                .description("Pedidos processados com sucesso")
                .register(meterRegistry);

        this.pedidosComErro = Counter.builder("delivery_pedidos_erro_total")
                .description("Pedidos com erro no processamento")
                .register(meterRegistry);

        // 2. Temporizador (Timer): para medir a duração de eventos.
        this.tempoProcessamentoPedido = Timer.builder("delivery_pedido_processamento_seconds")
                .description("Tempo de processamento de pedidos")
                .register(meterRegistry);

        // 3. Medidor (Gauge): para valores que podem aumentar e diminuir (ex: usuários online).
        Gauge.builder("delivery_usuarios_ativos_total", usuariosAtivos, AtomicInteger::get)
                .description("Número de usuários ativos atualmente")
                .register(meterRegistry);
    }

    @Override
    public void incrementarPedidosProcessados() {
        this.pedidosProcessados.increment();
    }

    @Override
    public void incrementarPedidosComSucesso() {
        this.pedidosComSucesso.increment();
    }

    @Override
    public void incrementarPedidosComErro() {
        this.pedidosComErro.increment();
    }

    @Override
    public Timer.Sample iniciarTimerPedido() {
        return Timer.start(meterRegistry);
    }

    @Override
    public void finalizarTimerPedido(Timer.Sample sample) {
        sample.stop(this.tempoProcessamentoPedido);
    }

    @Override
    public void setUsuariosAtivos(int quantidade) {
        this.usuariosAtivos.set(quantidade);
    }
}