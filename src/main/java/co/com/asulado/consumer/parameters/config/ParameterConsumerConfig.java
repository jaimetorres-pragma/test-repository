package co.com.asulado.consumer.parameters.config;

import co.com.asulado.consumer.parameters.ParameterAdapter;
import co.com.asulado.consumer.parameters.mapper.ParameterMapper;
import co.com.asulado.consumer.util.RestUtil;
import co.com.asulado.model.liquidation.gateways.ParameterRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class ParameterConsumerConfig {

    @Bean
    public ParameterRepository parameterRestConsumer(
            @Value("${adapters.webclients.parameters.uri}") String uri,
            @Qualifier("parametersWebClient") WebClient webClient, ParameterMapper mapper) {
        return new ParameterAdapter(uri, webClient, mapper);
    }

    @Bean(name = "parametersWebClient")
    public WebClient parametersWebClient(
            @Value("${adapters.webclients.host}") String host,
            @Value("${adapters.webclients.parameters.connection-time-out}") int connectionTimeout,
            @Value("${adapters.webclients.parameters.read-timeout}") int readTimeout,
            @Value("${adapters.webclients.parameters.write-timeout}") int writeTimeout) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return RestUtil.buildWebClient(host, headers, connectionTimeout,
                readTimeout, writeTimeout);
    }

}
