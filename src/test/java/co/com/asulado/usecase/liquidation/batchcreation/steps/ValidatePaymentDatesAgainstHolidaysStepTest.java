package co.com.asulado.usecase.liquidation.batchcreation.steps;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de ValidatePaymentDatesAgainstHolidaysStep")
class ValidatePaymentDatesAgainstHolidaysStepTest {

    @InjectMocks
    private ValidatePaymentDatesAgainstHolidaysStep step;


    @Test
    @DisplayName("Debe validar fechas de pago contra festivos exitosamente")
    void shouldValidatePaymentDatesAgainstHolidaysSuccessfully() {
        // Given
        var context = ValidatePaymentDatesAgainstHolidaysStepTestData.createContextWithParticipantsWithPaymentDates();

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getParticipants());
                    assertEquals(context.getParticipants().size(), resultContext.getParticipants().size());

                    resultContext.getParticipants().forEach(participant -> {
                        assertNotNull(participant.getPaymentDate());
                    });
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe ajustar fechas de pago cuando caen en festivos")
    void shouldAdjustPaymentDatesWhenTheyFallOnHolidays() {
        // Given
        var context = ValidatePaymentDatesAgainstHolidaysStepTestData.createContextWithHolidayParticipant();

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getParticipants());
                    assertEquals(ValidatePaymentDatesAgainstHolidaysStepTestData.PARTICIPANTS_COUNT_1, resultContext.getParticipants().size());

                    assertEquals(ValidatePaymentDatesAgainstHolidaysStepTestData.ADJUSTED_DATE_AUG_15,
                        resultContext.getParticipants().getFirst().getPaymentDate());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar lista vacía de participantes")
    void shouldHandleEmptyParticipantList() {
        // Given
        var context = ValidatePaymentDatesAgainstHolidaysStepTestData.createContextWithEmptyParticipants();

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getParticipants());
                    assertEquals(ValidatePaymentDatesAgainstHolidaysStepTestData.PARTICIPANTS_COUNT_0, resultContext.getParticipants().size());
                })
                .verifyComplete();
    }
}
