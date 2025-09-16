package co.com.asulado.consumer.util;

import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.error.BusinessException;
import co.com.asulado.model.error.ErrorCode;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@UtilityClass
@Slf4j
public class RestUtil {

    public WebClient buildWebClient(
            String host, HttpHeaders headers, int connectionTimeout, int readTimeout, int writeTimeout) {

        return WebClient.builder()
                .baseUrl(host)
                .defaultHeaders(httpHeaders -> httpHeaders.addAll(headers))
                .clientConnector(getClientHttpConnector(connectionTimeout, readTimeout, writeTimeout))
                .build();
    }

    public ClientHttpConnector getClientHttpConnector(
            int connectionTimeout, int readTimeout, int writeTimeout) {

        return new ReactorClientHttpConnector(
                HttpClient.create()
                        .compress(true)
                        .keepAlive(true)
                        .option(CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                        .doOnConnected(
                                connection -> {
                                    connection.addHandlerLast(new ReadTimeoutHandler(readTimeout, MILLISECONDS));
                                    connection.addHandlerLast(new WriteTimeoutHandler(writeTimeout, MILLISECONDS));
                                }));
    }

    public BusinessException mapWebClientException(WebClientResponseException ex, String traceId) {
        log.error(Constants.LOG_WEBCLIENT_ERROR, ex.getStatusCode(), ex.getResponseBodyAsString());

        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new BusinessException(ErrorCode.ERROR_NOT_FOUND, traceId);
        } else if (ex.getStatusCode().is4xxClientError()) {
            return new BusinessException(ErrorCode.VALIDATION_ERROR, traceId);
        } else if (ex.getStatusCode().is5xxServerError()) {
            return new BusinessException(ErrorCode.ERROR_INTERNAL, traceId);
        }

        return new BusinessException(ErrorCode.ERROR_INTERNAL, traceId);
    }
}
