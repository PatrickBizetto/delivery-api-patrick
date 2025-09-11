package com.delivery_api.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicrometerConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
            // Adiciona tags padrão a todas as métricas
            .commonTags("application", "delivery-api", "environment", "development")
            // Filtra métricas que não queremos expor (ex: do próprio actuator)
            .meterFilter(MeterFilter.deny(id -> {
                String uri = id.getTag("uri");
                return uri != null && uri.startsWith("/actuator");
            }));
    }
}