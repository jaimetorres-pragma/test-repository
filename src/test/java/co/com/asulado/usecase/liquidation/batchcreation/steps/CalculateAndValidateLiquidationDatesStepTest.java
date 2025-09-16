package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.usecase.liquidation.testdata.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de CalculateAndValidateLiquidationDatesStep")
class CalculateAndValidateLiquidationDatesStepTest {


    @InjectMocks
    private CalculateAndValidateLiquidationDatesStep step;

    @Test
    @DisplayName("Debe calcular y validar fechas de liquidación exitosamente")
    void shouldCalculateAndValidateLiquidationDatesSuccessfully() {
        // Given
        var context = TestData.createProcessingContextWithGroupedData();

        var expectedLiquidationDate1 = CalculateAndValidateLiquidationDatesStepTestData.ADJUSTED_LIQUIDATION_DATE_AUG_15;
        var expectedLiquidationDate2 = CalculateAndValidateLiquidationDatesStepTestData.calculateExpectedLiquidationDate(
                CalculateAndValidateLiquidationDatesStepTestData.PAYMENT_DATE_AUG_25, context.getSla());
        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getLiquidationDates());
                    assertEquals(CalculateAndValidateLiquidationDatesStepTestData.LIQUIDATION_DATES_COUNT_2, resultContext.getLiquidationDates().size());

                    assertEquals(expectedLiquidationDate1, resultContext.getLiquidationDates().get(CalculateAndValidateLiquidationDatesStepTestData.PAYMENT_DATE_AUG_20));
                    assertEquals(expectedLiquidationDate2, resultContext.getLiquidationDates().get(CalculateAndValidateLiquidationDatesStepTestData.PAYMENT_DATE_AUG_25));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe ajustar fechas de liquidación cuando caen en festivos")
    void shouldAdjustLiquidationDatesWhenTheyFallOnHolidays() {
        // Given
        var context = TestData.createProcessingContextWithGroupedData();

        var initialLiquidationDate2 = CalculateAndValidateLiquidationDatesStepTestData.calculateExpectedLiquidationDate(
            CalculateAndValidateLiquidationDatesStepTestData.PAYMENT_DATE_AUG_25, context.getSla());

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getLiquidationDates());
                    assertEquals(CalculateAndValidateLiquidationDatesStepTestData.LIQUIDATION_DATES_COUNT_2, resultContext.getLiquidationDates().size());

                    assertEquals(CalculateAndValidateLiquidationDatesStepTestData.ADJUSTED_LIQUIDATION_DATE_AUG_15,
                        resultContext.getLiquidationDates().get(CalculateAndValidateLiquidationDatesStepTestData.PAYMENT_DATE_AUG_20));
                    assertEquals(initialLiquidationDate2,
                        resultContext.getLiquidationDates().get(CalculateAndValidateLiquidationDatesStepTestData.PAYMENT_DATE_AUG_25));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar participantes agrupados vacíos")
    void shouldHandleEmptyGroupedParticipants() {
        // Given
        var context = CalculateAndValidateLiquidationDatesStepTestData.createContextWithEmptyGroupedParticipants();

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getLiquidationDates());
                    assertEquals(CalculateAndValidateLiquidationDatesStepTestData.LIQUIDATION_DATES_COUNT_0, resultContext.getLiquidationDates().size());
                })
                .verifyComplete();
    }
}
