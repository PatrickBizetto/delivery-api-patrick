package com.delivery_api.controller;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final MeterRegistry meterRegistry;

    public DashboardController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // Rota para servir a página HTML
    @GetMapping
    public String dashboardPage() {
        return "dashboard"; // Retorna o nome do arquivo "dashboard.html"
    }

    // API que o frontend vai chamar para buscar os dados
    @GetMapping("/api/metrics")
    @ResponseBody
    public Map<String, Object> getMetricsData() {
        Map<String, Object> metrics = new LinkedHashMap<>();

        metrics.put("pedidos_total", getCounterValue("delivery_pedidos_total"));
        metrics.put("pedidos_sucesso", getCounterValue("delivery_pedidos_sucesso_total"));
        metrics.put("pedidos_erro", getCounterValue("delivery_pedidos_erro_total"));
        metrics.put("tempo_medio_pedido_ms", getTimerMean("delivery_pedido_processamento_seconds"));
        metrics.put("usuarios_ativos", getGaugeValue("delivery_usuarios_ativos_total"));

        return metrics;
    }

    private double getCounterValue(String name) {
        return meterRegistry.find(name).counter() != null ? meterRegistry.find(name).counter().count() : 0.0;
    }

    private double getTimerMean(String name) {
        return meterRegistry.find(name).timer() != null ? meterRegistry.find(name).timer().mean(TimeUnit.MILLISECONDS) : 0.0;
    }

    private double getGaugeValue(String name) {
        return meterRegistry.find(name).gauge() != null ? meterRegistry.find(name).gauge().value() : 0.0;
    }
}