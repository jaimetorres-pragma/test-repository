package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.model.paymentservice.calendar.gateway.CalendarGateway;
import co.com.asulado.usecase.liquidation.testdata.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de FetchCalendarStep")
class FetchCalendarStepTest {

    @Mock
    private CalendarGateway calendarGateway;

    private FetchCalendarStep step;

    @BeforeEach
    void setUp() {
        step = new FetchCalendarStep(calendarGateway);
    }

    @Test
    @DisplayName("Debe obtener eventos del calendario exitosamente")
    void shouldFetchCalendarEventsSuccessfully() {
        // Given
        var context = TestData.createDefaultProcessingContext();

        when(calendarGateway.findByMonthYearAndType(
            FetchCalendarStepTestData.MONTH_AUGUST,
            FetchCalendarStepTestData.YEAR_2025,
            FetchCalendarStepTestData.CALENDAR_TYPE_HOLIDAYS_AND_WEEKEND))
                .thenReturn(Flux.fromIterable(TestData.createHolidaysList()));

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getHolidays());
                    assertEquals(FetchCalendarStepTestData.HOLIDAYS_COUNT_8, resultContext.getHolidays().size());

                    assertEquals(context.getMonth(), resultContext.getMonth());
                    assertEquals(context.getYear(), resultContext.getYear());
                    assertEquals(context.getCalendarType(), resultContext.getCalendarType());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar respuesta vacía del calendario")
    void shouldHandleEmptyCalendarResponse() {
        // Given
        var context = TestData.createDefaultProcessingContext();

        when(calendarGateway.findByMonthYearAndType(anyInt(), anyInt(), anyString()))
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getHolidays());
                    assertEquals(FetchCalendarStepTestData.HOLIDAYS_COUNT_0, resultContext.getHolidays().size());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar error del gateway de calendario")
    void shouldHandleCalendarGatewayError() {
        // Given
        var context = TestData.createDefaultProcessingContext();

        when(calendarGateway.findByMonthYearAndType(anyInt(), anyInt(), anyString()))
                .thenReturn(Flux.error(new RuntimeException(FetchCalendarStepTestData.EXTERNAL_SERVICE_ERROR_MESSAGE)));

        // When & Then
        StepVerifier.create(step.process(context))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe pasar parámetros correctos al gateway de calendario")
    void shouldPassCorrectParametersToCalendarGateway() {
        // Given
        var context = FetchCalendarStepTestData.createCustomContext();

        when(calendarGateway.findByMonthYearAndType(
            FetchCalendarStepTestData.MONTH_DECEMBER,
            FetchCalendarStepTestData.YEAR_2024,
            FetchCalendarStepTestData.CALENDAR_TYPE_HOLIDAYS))
                .thenReturn(Flux.fromIterable(TestData.createHolidaysList()));

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getHolidays());
                    assertEquals(FetchCalendarStepTestData.MONTH_DECEMBER, resultContext.getMonth());
                    assertEquals(FetchCalendarStepTestData.YEAR_2024, resultContext.getYear());
                    assertEquals(FetchCalendarStepTestData.CALENDAR_TYPE_HOLIDAYS, resultContext.getCalendarType());
                })
                .verifyComplete();
    }
}
