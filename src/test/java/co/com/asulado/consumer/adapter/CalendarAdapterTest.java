package co.com.asulado.consumer.adapter;

import co.com.asulado.consumer.mapper.CalendarMapper;
import co.com.asulado.model.paymentservice.calendar.Calendar;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DisplayName("Pruebas de CalendarAdapter")
class CalendarAdapterTest {
    private MockWebServer mockWebServer;
    private CalendarAdapter adapter;
    private ObjectMapper objectMapper;
    private final CalendarMapper mapper = Mappers.getMapper(CalendarMapper.class);

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        adapter = new CalendarAdapter(webClient, mapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Debe retornar lista de calendario cuando la respuesta de la API es exitosa")
    void shouldReturnCalendarListWhenApiResponseIsSuccessful() throws Exception {
        // Given
        var apiResponse = CalendarAdapterTestData.createSuccessfulApiResponse();
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        mockWebServer.enqueue(CalendarAdapterTestData.createJsonMockResponse(jsonResponse));

        // When
        Flux<Calendar> result = adapter.findByMonthYearAndType(
            CalendarAdapterTestData.MONTH_AUGUST,
            CalendarAdapterTestData.YEAR_2025,
            CalendarAdapterTestData.CALENDAR_TYPE_HOLIDAYS);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(calendar -> calendar.eventId().equals(CalendarAdapterTestData.EVENT_ID_10))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar error cuando la respuesta de la API no es exitosa")
    void shouldReturnErrorWhenApiResponseIsUnsuccessful() throws Exception {
        // Given
        var apiResponse = CalendarAdapterTestData.createUnsuccessfulApiResponse();
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        mockWebServer.enqueue(CalendarAdapterTestData.createJsonMockResponse(jsonResponse));

        // When
        Flux<Calendar> result = adapter.findByMonthYearAndType(
            CalendarAdapterTestData.MONTH_AUGUST,
            CalendarAdapterTestData.YEAR_2025,
            CalendarAdapterTestData.CALENDAR_TYPE_HOLIDAYS);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(businessException -> businessException.getMessage().contains(CalendarAdapterTestData.ERROR_MESSAGE_EXTERNAL_SERVICE))
                .verify();
    }

    @Test
    @DisplayName("Debe retornar error cuando no se encuentra el recurso")
    void shouldReturnErrorWhenNotFound() {
        // Given
        mockWebServer.enqueue(CalendarAdapterTestData.createNotFoundMockResponse());

        // When
        Flux<Calendar> result = adapter.findByMonthYearAndType(
            CalendarAdapterTestData.MONTH_AUGUST,
            CalendarAdapterTestData.YEAR_2025,
            CalendarAdapterTestData.CALENDAR_TYPE_HOLIDAYS);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable.getMessage().contains(CalendarAdapterTestData.ERROR_MESSAGE_NOT_FOUND))
                .verify();
    }
}
