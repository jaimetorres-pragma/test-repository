package co.com.asulado.consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.webclients.payments")
public record PaymentsWebClientProperties(
        String url
) {}

