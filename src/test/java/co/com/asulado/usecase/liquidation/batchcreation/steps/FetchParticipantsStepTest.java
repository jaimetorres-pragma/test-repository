package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.model.payments.gateways.ParticipantProjectionRepository;
import co.com.asulado.usecase.liquidation.testdata.UseCaseTestData;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de FetchParticipantsStep")
class FetchParticipantsStepTest {

    @Mock
    private ParticipantProjectionRepository participantProjectionRepository;

    private FetchParticipantsStep step;

    @BeforeEach
    void setUp() {
        step = new FetchParticipantsStep(participantProjectionRepository);
    }

    @Test
    @DisplayName("Debe obtener participantes exitosamente")
    void shouldFetchParticipantsSuccessfully() {
        // Given
        var context = UseCaseTestData.createInitialProcessingContext();

        when(participantProjectionRepository.findAllParticipantProjectionsExcludingExistingInLiquidationBatch())
                .thenReturn(Flux.fromIterable(UseCaseTestData.createParticipantProjectionsList()));

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getParticipants());
                    assertEquals(FetchParticipantsStepTestData.PARTICIPANTS_COUNT_4, resultContext.getParticipants().size());

                    assertEquals(context.getSla(), resultContext.getSla());
                    assertEquals(context.getMonth(), resultContext.getMonth());
                    assertEquals(context.getYear(), resultContext.getYear());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar lista vacía de participantes")
    void shouldHandleEmptyParticipantList() {
        // Given
        var context = UseCaseTestData.createInitialProcessingContext();

        when(participantProjectionRepository.findAllParticipantProjectionsExcludingExistingInLiquidationBatch())
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> {
                    assertNotNull(resultContext.getParticipants());
                    assertEquals(FetchParticipantsStepTestData.PARTICIPANTS_COUNT_0, resultContext.getParticipants().size());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar error del repositorio")
    void shouldHandleRepositoryError() {
        // Given
        var context = UseCaseTestData.createInitialProcessingContext();

        when(participantProjectionRepository.findAllParticipantProjectionsExcludingExistingInLiquidationBatch())
                .thenReturn(Flux.error(FetchParticipantsStepTestData.createConnectionException()));

        // When & Then
        StepVerifier.create(step.process(context))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe mantener integridad de datos de participante")
    void shouldMaintainParticipantDataIntegrity() {
        // Given
        var context = UseCaseTestData.createInitialProcessingContext();

        when(participantProjectionRepository.findAllParticipantProjectionsExcludingExistingInLiquidationBatch())
                .thenReturn(Flux.fromIterable(UseCaseTestData.createParticipantProjectionsList()));

        // When & Then
        StepVerifier.create(step.process(context))
                .assertNext(resultContext -> resultContext.getParticipants().forEach(participant -> {
                    assertNotNull(participant.getParticipantId());
                    assertNotNull(participant.getPersonId());
                    assertNotNull(participant.getProductName());
                    assertNotNull(participant.getPaymentRequestId());
                    assertNotNull(participant.getProductFrequency());
                }))
                .verifyComplete();
    }
}
