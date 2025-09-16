package co.com.asulado.r2dbc.config.liquibase;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.datasource")
public record JdbcConnectionProperties(
        String host,
        Integer port,
        String database,
        String schema,
        String username,
        String password,
        String driverClassName) {
}