package co.com.asulado.consumer.parameters;

import co.com.asulado.consumer.parameters.dto.response.LiquidationParameterResponse;
import co.com.asulado.consumer.parameters.mapper.ParameterMapper;
import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.error.BusinessException;
import co.com.asulado.model.liquidation.parameter.LiquidationParameter;
import co.com.asulado.validator.dto.response.ApiGenericResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas de ParameterAdapter")
class ParameterAdapterTest {

    private static ParameterAdapter adapter;
    private static MockWebServer server;
    private final ParameterMapper mapper = Mappers.getMapper(ParameterMapper.class);
    private ObjectMapper om;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        om = new ObjectMapper();

        var webClient = WebClient.builder()
                .baseUrl(server.url("/").toString())
                .build();

        String endpoint = server.url("/api/parameters").toString();
        adapter = new ParameterAdapter(endpoint, webClient, mapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }


    @Test
    @DisplayName("LIQUIDATION: devuelve dominio cuando la API responde success=true (usando clases reales para serializar)")
    void shouldReturnDomainWhenApiSuccess_liquidation() throws Exception {
        ApiGenericResponse<LiquidationParameterResponse> api = new ApiGenericResponse<>();
        api.setSuccess(true);
        api.setMessage("ok");
        api.setData(LiquidationParameterResponse.builder().build());

        String json = om.writeValueAsString(api);

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(json));

        String traceId = "t-1";

        StepVerifier.create(adapter.getParameters(Constants.LIQUIDATION_PARAMETER_KEY, traceId))
                .assertNext(p -> assertInstanceOf(LiquidationParameter.class, p))
                .verifyComplete();

        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/api/parameters", req.getPath());
        assertEquals(traceId, req.getHeader(Constants.TRACE_ID_HEADER));
        String body = req.getBody().readUtf8();
        assertTrue(body.contains(Constants.LIQUIDATION_PARAMETER_KEY));
        assertTrue(body.contains(Constants.GET_ACTION));
    }

    @Test
    @DisplayName("GENERAL: debe emitir BusinessException cuando success=false")
    void shouldErrorWithBusinessExceptionWhenSuccessFalse_general() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\":false,\"message\":\"service said no\",\"data\":null}"));

        StepVerifier.create(adapter.getParameters(Constants.GENERAL_PARAMETER_KEY, "t-2"))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    @DisplayName("HTTP 5xx: debe propagarse como BusinessException mapeada")
    void shouldMapHttp5xxToBusinessException() {
        server.enqueue(new MockResponse().setResponseCode(500).setBody("boom"));

        StepVerifier.create(adapter.getParameters(Constants.LIQUIDATION_PARAMETER_KEY, "t-3"))
                .expectError(BusinessException.class)
                .verify();
    }

}