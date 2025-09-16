package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.usecase.liquidation.testdata.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Pruebas de GroupByPaymentDateStep")
class GroupByPaymentDateStepTest {

    private GroupByPaymentDateStep step;

    @BeforeEach
    void setUp() {
        step = new GroupByPaymentDateStep();
    }

    @Test
    @DisplayName("Debe agrupar participantes por fecha de pago exitosamente")
    void shouldGroupParticipantsByPaymentDateSuccessfully() {
        // Given
        var context = TestData.createProcessingContextWithParticipants().toBuilder()
                .participants(TestData.createParticipantProjectionsWithPaymentDatesList())
                .build();

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getGroupedByPaymentDate());

                    assertFalse(resultContext.getGroupedByPaymentDate().isEmpty());

                    resultContext.getGroupedByPaymentDate().forEach((paymentDate, participants) -> {
                        assertNotNull(paymentDate);
                        assertNotNull(participants);
                        assertFalse(participants.isEmpty());

                        participants.forEach(participant ->
                            assertEquals(paymentDate, participant.getPaymentDate())
                        );
                    });
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar lista vacía de participantes")
    void shouldHandleEmptyParticipantList() {
        // Given
        var context = GroupByPaymentDateStepTestData.createContextWithEmptyParticipants();

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getGroupedByPaymentDate());
                    assertEquals(GroupByPaymentDateStepTestData.GROUPS_COUNT_0, resultContext.getGroupedByPaymentDate().size());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe crear múltiples grupos para diferentes fechas de pago")
    void shouldCreateMultipleGroupsForDifferentPaymentDates() {
        // Given
        var context = GroupByPaymentDateStepTestData.createContextWithMultiplePaymentDates();

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getGroupedByPaymentDate());
                    assertEquals(GroupByPaymentDateStepTestData.GROUPS_COUNT_3, resultContext.getGroupedByPaymentDate().size());

                    assertEquals(GroupByPaymentDateStepTestData.GROUP_SIZE_1,
                        resultContext.getGroupedByPaymentDate().get(GroupByPaymentDateStepTestData.PAYMENT_DATE_AUG_15).size());
                    assertEquals(GroupByPaymentDateStepTestData.GROUP_SIZE_2,
                        resultContext.getGroupedByPaymentDate().get(GroupByPaymentDateStepTestData.PAYMENT_DATE_AUG_20).size());
                    assertEquals(GroupByPaymentDateStepTestData.GROUP_SIZE_1,
                        resultContext.getGroupedByPaymentDate().get(GroupByPaymentDateStepTestData.PAYMENT_DATE_AUG_25).size());
                })
                .verifyComplete();
    }
}
