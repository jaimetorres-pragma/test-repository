package co.com.asulado.consumer.util;

import co.com.asulado.model.error.BusinessException;
import co.com.asulado.model.error.ErrorCode;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestUtilTest {

    private MockWebServer server;

    @BeforeEach
    void setup() throws Exception {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    @DisplayName("mapWebClientException: 404 -> ERROR_NOT_FOUND")
    void mapWebClientException_notFound() {
        String traceId = "t-404";
        WebClientResponseException ex = WebClientResponseException.create(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                new HttpHeaders(),
                "not-found".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        BusinessException be = RestUtil.mapWebClientException(ex, traceId);

        assertNotNull(be);
        assertEquals(ErrorCode.ERROR_NOT_FOUND, be.getErrorCode());
    }

    @Test
    @DisplayName("mapWebClientException: 4xx (ej. 400) -> VALIDATION_ERROR")
    void mapWebClientException_4xx() {
        String traceId = "t-400";
        WebClientResponseException ex = WebClientResponseException.create(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                new HttpHeaders(),
                "bad-request".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        BusinessException be = RestUtil.mapWebClientException(ex, traceId);

        assertNotNull(be);
        assertEquals(ErrorCode.VALIDATION_ERROR, be.getErrorCode());
    }

    @Test
    @DisplayName("mapWebClientException: 5xx (ej. 500) -> ERROR_INTERNAL")
    void mapWebClientException_5xx() {
        String traceId = "t-500";
        WebClientResponseException ex = WebClientResponseException.create(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                new HttpHeaders(),
                "boom".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        BusinessException be = RestUtil.mapWebClientException(ex, traceId);

        assertNotNull(be);
        assertEquals(ErrorCode.ERROR_INTERNAL, be.getErrorCode());
    }

    @Test
    @DisplayName("mapWebClientException: otros (ej. 302) -> ERROR_INTERNAL (default)")
    void mapWebClientException_otherDefaultsToInternal() {
        String traceId = "t-302";
        WebClientResponseException ex = WebClientResponseException.create(
                HttpStatus.FOUND.value(),
                HttpStatus.FOUND.getReasonPhrase(),
                new HttpHeaders(),
                "redirect".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        BusinessException be = RestUtil.mapWebClientException(ex, traceId);

        assertNotNull(be);
        assertEquals(ErrorCode.ERROR_INTERNAL, be.getErrorCode());
    }

    @Test
    @DisplayName("buildWebClient: aplica baseUrl y default headers (se envían en la request)")
    void buildWebClient_appliesBaseUrlAndHeaders() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "text/plain")
                .setBody("ok"));

        HttpHeaders defaults = new HttpHeaders();
        defaults.add("X-Trace-Id", "t-1");

        WebClient client = RestUtil.buildWebClient(
                server.url("/").toString(),
                defaults,
                1_000, 1_000, 1_000
        );

        String body = client.get()
                .uri("/ping")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        assertEquals("ok", body);

        RecordedRequest req = server.takeRequest(1, TimeUnit.SECONDS);
        assertNotNull(req);
        assertEquals("GET", req.getMethod());
        assertEquals("/ping", req.getPath());
        assertEquals("t-1", req.getHeader("X-Trace-Id"));

    }

    @Test
    @DisplayName("getClientHttpConnector: devuelve ReactorClientHttpConnector configurado")
    void getClientHttpConnector_returnsReactorConnector() {
        ClientHttpConnector connector = RestUtil.getClientHttpConnector(500, 500, 500);
        assertNotNull(connector);
        assertInstanceOf(ReactorClientHttpConnector.class, connector);
    }
}