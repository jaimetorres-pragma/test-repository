package co.com.asulado.usecase.liquidation.batchcreation.services;

import co.com.asulado.usecase.liquidation.batchcreation.util.FrequencyCalculatorUtility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas de FrequencyCalculatorService")
class FrequencyCalculatorUtilityTest {

    @ParameterizedTest
    @CsvSource({
        "20CADAMES, 2025-08-27, 2025-08-20",
        "5CADAMES, 2025-08-27, 2025-08-05",
        "31MESACTUAL, 2025-02-15, 2025-02-28",
        "31UNICOPAGO, 2025-04-10, 2025-04-30",
        "1CADA3MESES, 2025-08-27, 2025-08-01"
    })
    @DisplayName("Debe calcular correctamente la fecha de pago para frecuencias válidas")
    void shouldCalculatePaymentDateCorrectly(String frequency, String currentDateStr, String expectedDateStr) {
        // Given
        LocalDate currentDate = LocalDate.parse(currentDateStr);
        LocalDate expectedDate = LocalDate.parse(expectedDateStr);

        // When
        LocalDate result = FrequencyCalculatorUtility.calculatePaymentDate(frequency, currentDate);

        // Then
        assertEquals(expectedDate, result);
    }

    @Test
    @DisplayName("Debe retornar null para frecuencia nula")
    void shouldReturnNullForNullFrequency() {
        // When
        LocalDate result = FrequencyCalculatorUtility.calculatePaymentDate(null,
            FrequencyCalculatorServiceTestData.CURRENT_DATE_AUG_27_2025);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Debe retornar null para frecuencia vacía")
    void shouldReturnNullForEmptyFrequency() {
        // When
        LocalDate result = FrequencyCalculatorUtility.calculatePaymentDate(
            FrequencyCalculatorServiceTestData.FREQUENCY_EMPTY,
            FrequencyCalculatorServiceTestData.CURRENT_DATE_AUG_27_2025);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Debe lanzar excepción para formato de frecuencia inválido")
    void shouldThrowExceptionForInvalidFrequencyFormat() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> FrequencyCalculatorUtility.calculatePaymentDate(
                FrequencyCalculatorServiceTestData.FREQUENCY_INVALID,
                FrequencyCalculatorServiceTestData.CURRENT_DATE_AUG_27_2025)
        );

        assertTrue(exception.getMessage().contains(FrequencyCalculatorServiceTestData.ERROR_MESSAGE_INVALID_FREQUENCY_FORMAT));
        assertTrue(exception.getMessage().contains(FrequencyCalculatorServiceTestData.FREQUENCY_INVALID));
    }

    @Test
    @DisplayName("Debe manejar correctamente febrero en año bisiesto")
    void shouldHandleLeapYearFebruaryCorrectly() {
        // When
        LocalDate result = FrequencyCalculatorUtility.calculatePaymentDate(
            FrequencyCalculatorServiceTestData.FREQUENCY_29_EACH_MONTH,
            FrequencyCalculatorServiceTestData.CURRENT_DATE_FEB_15_2024);

        // Then
        assertEquals(FrequencyCalculatorServiceTestData.EXPECTED_DATE_FEB_29_2024, result);
    }

    @Test
    @DisplayName("Debe ajustar el día para meses con menos días")
    void shouldAdjustDayForMonthsWithFewerDays() {
        // When
        LocalDate result = FrequencyCalculatorUtility.calculatePaymentDate(
            FrequencyCalculatorServiceTestData.FREQUENCY_31_EACH_MONTH,
            FrequencyCalculatorServiceTestData.CURRENT_DATE_SEP_15_2025);

        // Then
        assertEquals(FrequencyCalculatorServiceTestData.EXPECTED_DATE_SEP_30_2025, result);
    }
}
