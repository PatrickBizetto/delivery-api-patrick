package com.delivery_api.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("externalService")
public class ExternalServiceHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Health health() {
        try {
            // Simula a chamada para um serviço externo (gateway de pagamento, etc.)
            String url = "https://jsonplaceholder.typicode.com/todos/1";;
            restTemplate.getForEntity(url, String.class);
            return Health.up()
                .withDetail("service", "Gateway de Pagamento (Simulado)")
                .withDetail("url", url)
                .withDetail("status", "Disponível")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("service", "Gateway de Pagamento (Simulado)")
                .withDetail("error", e.getMessage())
                .withDetail("status", "Indisponível")
                .build();
        }
    }
}