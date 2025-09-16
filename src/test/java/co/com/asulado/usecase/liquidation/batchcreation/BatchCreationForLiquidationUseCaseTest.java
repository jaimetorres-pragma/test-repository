package co.com.asulado.usecase.liquidation.batchcreation;

import co.com.asulado.model.liquidation.params.BatchCreationModel;
import co.com.asulado.usecase.liquidation.batchcreation.util.ParticipantProcessingStep;
import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;
import co.com.asulado.usecase.liquidation.testdata.UseCaseTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de BatchCreationForLiquidationUseCase")
class BatchCreationForLiquidationUseCaseTest {

    @Mock
    private ParticipantProcessingStep step1;

    @Mock
    private ParticipantProcessingStep step2;

    @Mock
    private ParticipantProcessingStep step3;

    private BatchCreationForLiquidationUseCase useCase;
    private List<ParticipantProcessingStep> processingSteps;

    @BeforeEach
    void setUp() {
        processingSteps = List.of(step1, step2, step3);
        useCase = new BatchCreationForLiquidationUseCase(processingSteps);
    }

    @Test
    @DisplayName("Debe ejecutar todos los pasos exitosamente y retornar resultado de éxito")
    void shouldExecuteAllStepsSuccessfully() {
        BatchCreationModel request = UseCaseTestData.createDefaultBatchCreationModel();
        ProcessingContext initialContext = UseCaseTestData.createInitialProcessingContext();
        ProcessingContext finalContext = UseCaseTestData.createFinalProcessingContext();

        when(step1.process(any(ProcessingContext.class))).thenReturn(Mono.just(initialContext));
        when(step2.process(any(ProcessingContext.class))).thenReturn(Mono.just(initialContext));
        when(step3.process(any(ProcessingContext.class))).thenReturn(Mono.just(finalContext));

        StepVerifier.create(useCase.execute(request))
                .assertNext(result -> {
                    assertTrue(result.isSuccess());
                    assertEquals(UseCaseTestData.SUCCESS_MESSAGE, result.getMessage());
                    assertNotNull(result.getProcessedAt());
                })
                .verifyComplete();

        verify(step1).process(any(ProcessingContext.class));
        verify(step2).process(any(ProcessingContext.class));
        verify(step3).process(any(ProcessingContext.class));
    }

    @Test
    @DisplayName("Debe manejar falla de paso y retornar resultado de error")
    void shouldHandleStepFailureAndReturnErrorResult() {
        BatchCreationModel request = BatchCreationModel.defaultRequest();
        RuntimeException error = UseCaseTestData.createStepFailedException();

        when(step1.process(any(ProcessingContext.class))).thenReturn(Mono.error(error));

        StepVerifier.create(useCase.execute(request))
                .assertNext(result -> {
                    assertFalse(result.isSuccess());
                    assertEquals(UseCaseTestData.FAILURE_MESSAGE_PREFIX + UseCaseTestData.STEP_FAILED_MESSAGE, result.getMessage());
                    assertEquals(UseCaseTestData.TOTAL_PARTICIPANTS_0.intValue(), result.getTotalParticipantsProcessed());
                    assertEquals(UseCaseTestData.TOTAL_BATCHES_0, result.getBatchesCreated());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe construir resultado correcto desde el contexto de procesamiento")
    void shouldBuildCorrectResultFromProcessingContext() {
        BatchCreationModel request = BatchCreationModel.defaultRequest();
        ProcessingContext contextWithData = UseCaseTestData.createProcessingContextWithData();

        when(step1.process(any(ProcessingContext.class))).thenReturn(Mono.just(contextWithData));
        when(step2.process(any(ProcessingContext.class))).thenReturn(Mono.just(contextWithData));
        when(step3.process(any(ProcessingContext.class))).thenReturn(Mono.just(contextWithData));

        StepVerifier.create(useCase.execute(request))
                .assertNext(result -> {
                    assertTrue(result.isSuccess());
                    assertEquals(UseCaseTestData.TOTAL_PARTICIPANTS_0.intValue(), result.getTotalParticipantsProcessed());
                    assertEquals(UseCaseTestData.TOTAL_BATCHES_0, result.getBatchesCreated());
                    assertEquals(UseCaseTestData.TOTAL_HOLIDAYS_0, result.getHolidaysFound());
                    assertNotNull(result.getBatches());
                })
                .verifyComplete();
    }
}
