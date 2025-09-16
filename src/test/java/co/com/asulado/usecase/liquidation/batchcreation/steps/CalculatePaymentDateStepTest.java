package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.model.payments.ParticipantProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de CalculatePaymentDateStep")
class CalculatePaymentDateStepTest {

    @InjectMocks
    private CalculatePaymentDateStep calculatePaymentDateStep;

    @Test
    @DisplayName("Debe calcular fechas de pago para participantes con fechas nulas")
    void shouldCalculatePaymentDatesForParticipantsWithNullDates() {
        // Given
        var participant1 = CalculatePaymentDateStepTestData.createParticipantWithNullDate();
        var participant2 = CalculatePaymentDateStepTestData.createParticipantWithExistingDate();
        var context = CalculatePaymentDateStepTestData.createContextWithParticipants(List.of(participant1, participant2));
        // When & Then
        StepVerifier.create(calculatePaymentDateStep.process(context))
            .assertNext(resultContext -> {
                assertNotNull(resultContext.getParticipants());
                assertEquals(CalculatePaymentDateStepTestData.PARTICIPANTS_COUNT_2, resultContext.getParticipants().size());

                ParticipantProjection updatedParticipant1 = resultContext.getParticipants().getFirst();
                assertEquals(CalculatePaymentDateStepTestData.CALCULATED_DATE_AUG_20, updatedParticipant1.getPaymentDate());

                ParticipantProjection updatedParticipant2 = resultContext.getParticipants().get(1);
                assertEquals(CalculatePaymentDateStepTestData.EXISTING_DATE_AUG_15, updatedParticipant2.getPaymentDate());
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar lista vacía de participantes")
    void shouldHandleEmptyParticipantsList() {
        // Given
        var context = CalculatePaymentDateStepTestData.createEmptyContext();

        // When & Then
        StepVerifier.create(calculatePaymentDateStep.process(context))
            .assertNext(resultContext -> {
                assertNotNull(resultContext.getParticipants());
                assertEquals(CalculatePaymentDateStepTestData.PARTICIPANTS_COUNT_0, resultContext.getParticipants().size());
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("No debe modificar participantes que ya tienen fechas de pago")
    void shouldNotModifyParticipantsWithExistingPaymentDates() {
        // Given
        var participant = CalculatePaymentDateStepTestData.createParticipantWithSpecificDate(
            CalculatePaymentDateStepTestData.EXISTING_DATE_AUG_25);
        var context = CalculatePaymentDateStepTestData.createContextWithParticipants(List.of(participant));

        // When & Then
        StepVerifier.create(calculatePaymentDateStep.process(context))
            .assertNext(resultContext -> {
                ParticipantProjection result = resultContext.getParticipants().getFirst();
                assertEquals(CalculatePaymentDateStepTestData.EXISTING_DATE_AUG_25, result.getPaymentDate());
                assertEquals(CalculatePaymentDateStepTestData.PARTICIPANT_ID_1, result.getParticipantId());
            })
            .verifyComplete();
    }
}
