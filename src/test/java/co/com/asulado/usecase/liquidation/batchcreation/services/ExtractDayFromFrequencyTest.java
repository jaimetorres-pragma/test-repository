package co.com.asulado.usecase.liquidation.batchcreation.services;

import co.com.asulado.usecase.liquidation.batchcreation.util.FrequencyCalculatorUtility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas parametrizadas del método extractDayFromFrequency")
class ExtractDayFromFrequencyTest {

    @ParameterizedTest(name = "Debe extraer día {1} de frecuencia ''{0}'' ({2})")
    @MethodSource("co.com.asulado.usecase.liquidation.batchcreation.services.ExtractDayFromFrequencyTestData#validFrequencyTestCases")
    @DisplayName("Debe extraer día correctamente de frecuencias válidas")
    void shouldExtractDayFromValidFrequencies(String frequency, int expectedDay, String description) {
        // When
        int result = FrequencyCalculatorUtility.extractDayFromFrequency(frequency);

        // Then
        assertEquals(expectedDay, result);
    }

    @ParameterizedTest(name = "Debe lanzar excepción para frecuencia ''{0}'' ({2}) con mensaje que contiene ''{1}''")
    @MethodSource("co.com.asulado.usecase.liquidation.batchcreation.services.ExtractDayFromFrequencyTestData#invalidFrequencyTestCases")
    @DisplayName("Debe lanzar excepción para frecuencias inválidas")
    void shouldThrowExceptionForInvalidFrequencies(String frequency, String expectedErrorMessage, String description) {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> FrequencyCalculatorUtility.extractDayFromFrequency(frequency));
        assertTrue(exception.getMessage().contains(expectedErrorMessage),
            String.format("El mensaje de error debería contener '%s'. Mensaje actual: %s",
                expectedErrorMessage, exception.getMessage()));
    }

    @ParameterizedTest(name = "Debe incluir texto esperado en mensaje de error para frecuencia ''{0}''")
    @MethodSource("co.com.asulado.usecase.liquidation.batchcreation.services.ExtractDayFromFrequencyTestData#errorMessageValidationTestCases")
    @DisplayName("Debe incluir frecuencia original y ejemplos en mensaje de error")
    void shouldIncludeOriginalFrequencyAndExamplesInErrorMessage(String frequency, String[] expectedTexts, String description) {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> FrequencyCalculatorUtility.extractDayFromFrequency(frequency));

        String errorMessage = exception.getMessage();
        for (String expectedText : expectedTexts) {
            assertTrue(errorMessage.contains(expectedText),
                String.format("El mensaje de error debería contener '%s'. Mensaje actual: %s",
                    expectedText, errorMessage));
        }
    }

    @Test
    @DisplayName("Debe lanzar excepción específica para formato inválido con ejemplo de frecuencias válidas")
    void shouldThrowExceptionWithValidExamplesForInvalidFormat() {
        // Given
        String invalidFrequency = ExtractDayFromFrequencyTestData.INVALID_FREQUENCY_NO_DIGITS;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> FrequencyCalculatorUtility.extractDayFromFrequency(invalidFrequency));

        String errorMessage = exception.getMessage();
        assertTrue(errorMessage.contains(invalidFrequency));
        assertTrue(errorMessage.contains("20CADAMES"));
        assertTrue(errorMessage.contains("1CADA3MESES"));
        assertTrue(errorMessage.contains("30MESACTUAL"));
        assertTrue(errorMessage.contains("20UNICOPAGO"));
    }
}
