package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.model.liquidation.Participant;
import co.com.asulado.model.liquidation.gateways.LiquidationBatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de CreateLiquidationBatchesStep")
class CreateLiquidationBatchesStepTest {

    @Mock
    private LiquidationBatchRepository liquidationBatchRepository;

    private CreateLiquidationBatchesStep step;

    @BeforeEach
    void setUp() {
        step = new CreateLiquidationBatchesStep(liquidationBatchRepository);
    }

    @Test
    @DisplayName("Debe crear lotes de liquidación exitosamente")
    void shouldCreateLiquidationBatchesSuccessfully() {
        // Given
        var context = CreateLiquidationBatchesStepTestData.createProcessingContextWithGroupedData();

        var expectedBatch1 = CreateLiquidationBatchesStepTestData.createExpectedBatch1();
        var expectedBatch2 = CreateLiquidationBatchesStepTestData.createExpectedBatch2();

        when(liquidationBatchRepository.findByLiquidationDate(any(LocalDate.class)))
                .thenReturn(Flux.empty());

        when(liquidationBatchRepository.saveLiquidationBatchWithParticipants(any(LiquidationBatch.class), anyList()))
                .thenReturn(Mono.just(expectedBatch1))
                .thenReturn(Mono.just(expectedBatch2));

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getCreatedBatches());
                    assertEquals(CreateLiquidationBatchesStepTestData.BATCHES_COUNT_2, resultContext.getCreatedBatches().size());

                    resultContext.getCreatedBatches().forEach(batch -> {
                        assertNotNull(batch.getBatchId());
                        assertEquals(CreateLiquidationBatchesStepTestData.STATUS_NEW, batch.getStatus());
                        assertNotNull(batch.getLiquidationDate());
                        assertNotNull(batch.getDispersionDate());
                    });
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar error del repositorio correctamente")
    void shouldHandleRepositoryErrorGracefully() {
        // Given
        var context = CreateLiquidationBatchesStepTestData.createProcessingContextWithGroupedData();

        when(liquidationBatchRepository.findByLiquidationDate(any(LocalDate.class)))
                .thenReturn(Flux.empty());

        when(liquidationBatchRepository.saveLiquidationBatchWithParticipants(any(LiquidationBatch.class), anyList()))
                .thenReturn(Mono.error(CreateLiquidationBatchesStepTestData.createDatabaseException()));

        // When & Then
        StepVerifier.create(step.process(context))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe manejar participantes agrupados vacíos")
    void shouldHandleEmptyGroupedParticipants() {
        // Given
        var context = CreateLiquidationBatchesStepTestData.createEmptyGroupedContext();

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getCreatedBatches());
                    assertEquals(CreateLiquidationBatchesStepTestData.BATCHES_COUNT_0, resultContext.getCreatedBatches().size());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe crear detalles de participante correctos para cada lote")
    void shouldCreateCorrectParticipantDetailsForEachBatch() {
        // Given
        var context = CreateLiquidationBatchesStepTestData.createProcessingContextWithGroupedData();
        var expectedBatch1 = CreateLiquidationBatchesStepTestData.createExpectedBatch1();
        var expectedBatch2 = CreateLiquidationBatchesStepTestData.createExpectedBatch2();

        when(liquidationBatchRepository.findByLiquidationDate(any(LocalDate.class)))
                .thenReturn(Flux.empty());

        when(liquidationBatchRepository.saveLiquidationBatchWithParticipants(any(LiquidationBatch.class), anyList()))
                .thenAnswer(invocation -> {
                    LiquidationBatch batch = invocation.getArgument(0);
                    List<Participant> participants = invocation.getArgument(1);

                    participants.forEach(participant -> {
                        assertNotNull(participant.getParticipantPaymentId());
                        assertNotNull(participant.getPaymentRequestId());
                        assertNotNull(participant.getProduct());
                        assertNotNull(participant.getPersonId());
                        assertEquals(CreateLiquidationBatchesStepTestData.STATUS_ACTIVE, participant.getParticipantPaymentStatus());
                        assertEquals(CreateLiquidationBatchesStepTestData.STATUS_NEW, participant.getLiquidationStatus());
                        assertEquals(CreateLiquidationBatchesStepTestData.AMOUNT_ZERO, participant.getGrossAmountCop());
                        assertEquals(CreateLiquidationBatchesStepTestData.AMOUNT_ZERO, participant.getDeductionAmountCop());
                        assertEquals(CreateLiquidationBatchesStepTestData.AMOUNT_ZERO, participant.getNetAmountCop());

                        if (participant.getPersonId().equals(CreateLiquidationBatchesStepTestData.PERSON_ID_1) ||
                            participant.getPersonId().equals(CreateLiquidationBatchesStepTestData.PERSON_ID_2)) {
                            assertEquals(CreateLiquidationBatchesStepTestData.PRODUCT_NAME_PENSION, participant.getProduct());
                        }
                    });

                    if (batch.getDispersionDate().equals(CreateLiquidationBatchesStepTestData.PAYMENT_DATE_AUG_20)) {
                        return Mono.just(expectedBatch1);
                    } else {
                        return Mono.just(expectedBatch2);
                    }
                });

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getCreatedBatches());
                    assertEquals(CreateLiquidationBatchesStepTestData.BATCHES_COUNT_2, resultContext.getCreatedBatches().size());

                    resultContext.getCreatedBatches().forEach(batch -> {
                        assertNotNull(batch.getBatchId());
                        assertEquals(CreateLiquidationBatchesStepTestData.STATUS_NEW, batch.getStatus());
                        assertNotNull(batch.getLiquidationDate());
                        assertNotNull(batch.getDispersionDate());
                    });
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe asignar participantes a un lote existente cuando la fecha de liquidación es elegible")
    void shouldAssignParticipantsToExistingBatchWhenEligible() {
        // Given
        var context = CreateLiquidationBatchesStepTestData.createProcessingContextWithGroupedData();

        var existingBatchOld = LiquidationBatch.builder().batchId(5L).build();
        var existingBatchNew = LiquidationBatch.builder().batchId(9L).build();

        when(liquidationBatchRepository.findByLiquidationDate(any(LocalDate.class)))
                .thenReturn(Flux.just(existingBatchNew, existingBatchOld));

        when(liquidationBatchRepository.addParticipantsToBatch(any(Long.class), anyList()))
                .thenReturn(Mono.empty());

        when(liquidationBatchRepository.incrementParticipantCount(any(Long.class), any(Integer.class)))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getCreatedBatches());
                    assertEquals(CreateLiquidationBatchesStepTestData.BATCHES_COUNT_0, resultContext.getCreatedBatches().size());
                })
                .verifyComplete();

        ArgumentCaptor<Long> batchIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(liquidationBatchRepository, atLeastOnce()).addParticipantsToBatch(batchIdCaptor.capture(), anyList());
        assertTrue(batchIdCaptor.getAllValues().stream().allMatch(id -> id.equals(5L)));
        verify(liquidationBatchRepository, atLeastOnce()).incrementParticipantCount(eq(5L), any(Integer.class));
        verify(liquidationBatchRepository, never()).saveLiquidationBatchWithParticipants(any(), anyList());
    }

    @Test
    @DisplayName("No debe crear ni asignar cuando la fecha de liquidación es anterior a la fecha actual")
    void shouldSkipWhenLiquidationDateBeforeCurrentDate() {
        // Given
        var context = CreateLiquidationBatchesStepTestData.createEmptyGroupedContext().toBuilder()
                .groupedByPaymentDate(UseCaseDatesHelper.singleGroup())
                .liquidationDates(UseCaseDatesHelper.singlePastLiquidationDate())
                .build();

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getCreatedBatches());
                    assertEquals(CreateLiquidationBatchesStepTestData.BATCHES_COUNT_0, resultContext.getCreatedBatches().size());
                })
                .verifyComplete();

        verify(liquidationBatchRepository, never()).findByLiquidationDate(any(LocalDate.class));
        verify(liquidationBatchRepository, never()).saveLiquidationBatchWithParticipants(any(), anyList());
        verify(liquidationBatchRepository, never()).addParticipantsToBatch(anyLong(), anyList());
    }

    static class UseCaseDatesHelper {
        static java.util.Map<LocalDate, java.util.List<co.com.asulado.model.payments.ParticipantProjection>> singleGroup() {
            var pd = LocalDate.of(2025, 8, 25);
            return java.util.Map.of(pd, co.com.asulado.usecase.liquidation.testdata.UseCaseTestData.createGroupedParticipantsByPaymentDate().get(pd));
        }
        static java.util.Map<LocalDate, LocalDate> singlePastLiquidationDate() {
            var pd = LocalDate.of(2025, 8, 25);
            return java.util.Map.of(pd, LocalDate.of(2025, 8, 10));
        }
    }
}
