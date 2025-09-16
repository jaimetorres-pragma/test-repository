package co.com.asulado.usecase.liquidation.batchclosing;

import co.com.asulado.model.liquidation.params.BatchClosingModel;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingContext;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingStep;
import co.com.asulado.usecase.liquidation.testdata.ClosingUseCaseTestData;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de BatchClosingForLiquidationUseCase")
class BatchClosingForLiquidationUseCaseTest {

    @Mock
    private ClosingProcessingStep step1;

    @Mock
    private ClosingProcessingStep step2;

    @Mock
    private ClosingProcessingStep step3;

    private BatchClosingForLiquidationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BatchClosingForLiquidationUseCase(List.of(step1, step2, step3));
    }

    @Test
    @DisplayName("Debe ejecutar pasos y retornar cierre completado con resúmenes")
    void shouldExecuteStepsAndReturnCompletedWithSummaries() {
        var request = BatchClosingModel.builder().currentDate(ClosingUseCaseTestData.CURRENT_DATE).build();
        var initial = ClosingProcessingContext.builder().currentDate(request.getCurrentDate()).build();
        var finalCtx = ClosingUseCaseTestData.createContextWithSummaries();

        when(step1.process(any(ClosingProcessingContext.class))).thenReturn(Mono.just(initial));
        when(step2.process(any(ClosingProcessingContext.class))).thenReturn(Mono.just(initial));
        when(step3.process(any(ClosingProcessingContext.class))).thenReturn(Mono.just(finalCtx));

        StepVerifier.create(useCase.execute(request))
                .assertNext(result -> {
                    assertTrue(result.isProcessingResult());
                    assertEquals("Cierre completado", result.getProcessingMessage());
                    assertNotNull(result.getProcessedAt());
                    assertEquals(finalCtx.getTotalParticipantsProcessed(), result.getTotalParticipantsProcessed());
                    assertEquals(finalCtx.getTotalThirdPartiesProcessed(), result.getTotalThirdPartiesProcessed());
                    assertEquals(finalCtx.getSummaries().size(), result.getBatchesProcessed());
                    assertEquals(finalCtx.getSummaries().size(), result.getBatchSummaries().size());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar error de paso y retornar procesamiento fallido")
    void shouldHandleStepErrorAndReturnFailedProcessing() {
        var request = BatchClosingModel.defaultRequest();
        when(step1.process(any(ClosingProcessingContext.class))).thenReturn(Mono.error(new RuntimeException("Fallo")));

        StepVerifier.create(useCase.execute(request))
                .assertNext(result -> {
                    assertFalse(result.isProcessingResult());
                    assertTrue(result.getProcessingMessage().startsWith("Processing failed: "));
                    assertEquals(0, result.getBatchesProcessed());
                    assertEquals(0, result.getTotalParticipantsProcessed());
                    assertEquals(0, result.getTotalThirdPartiesProcessed());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar mensaje sin lotes cuando no hay elegibles")
    void shouldReturnNoBatchesMessageWhenNoEligible() {
        var request = BatchClosingModel.builder().currentDate(ClosingUseCaseTestData.CURRENT_DATE).build();
        var emptyCtx = ClosingProcessingContext.builder().currentDate(request.getCurrentDate()).eligibleBatches(List.of()).build();

        when(step1.process(any(ClosingProcessingContext.class))).thenReturn(Mono.just(emptyCtx));
        when(step2.process(any(ClosingProcessingContext.class))).thenReturn(Mono.just(emptyCtx));
        when(step3.process(any(ClosingProcessingContext.class))).thenReturn(Mono.just(emptyCtx));

        StepVerifier.create(useCase.execute(request))
                .assertNext(result -> {
                    assertTrue(result.isProcessingResult());
                    assertEquals("No hay lotes para cerrar", result.getProcessingMessage());
                    assertEquals(0, result.getBatchesProcessed());
                })
                .verifyComplete();
    }
}

