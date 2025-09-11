package com.delivery_api.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;

@Component("database")
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up()
                    .withDetail("database", "H2")
                    .withDetail("status", "Conectado com sucesso")
                    .build();
            } else {
                 return Health.down()
                    .withDetail("database", "H2")
                    .withDetail("error", "A conexão não é mais válida.")
                    .build();
            }
        } catch (Exception e) {
            return Health.down(e)
                .withDetail("database", "H2")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}