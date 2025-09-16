package co.com.asulado.usecase.liquidation.batchclosing.steps;

import co.com.asulado.model.liquidation.gateways.LiquidationBatchRepository;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingContext;
import co.com.asulado.usecase.liquidation.testdata.ClosingUseCaseTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de FindEligibleBatchesStep")
class FindEligibleBatchesStepTest {

    @Mock
    private LiquidationBatchRepository liquidationBatchRepository;

    @InjectMocks
    private FindEligibleBatchesStep step;

    @Test
    @DisplayName("Debe encontrar lotes elegibles a partir de la fecha actual")
    void shouldFindEligibleBatchesFromCurrentDate() {
        var context = ClosingProcessingContext.builder().currentDate(ClosingUseCaseTestData.CURRENT_DATE).build();
        var batches = ClosingUseCaseTestData.createEligibleBatches();
        when(liquidationBatchRepository.findByLiquidationDateGreaterOrEqual(any())).thenReturn(Flux.fromIterable(batches));

        StepVerifier.create(step.process(context))
                .assertNext(result -> assertEquals(batches, result.getEligibleBatches()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay lotes")
    void shouldReturnEmptyListWhenNoBatches() {
        var context = ClosingProcessingContext.builder().currentDate(ClosingUseCaseTestData.CURRENT_DATE).build();
        when(liquidationBatchRepository.findByLiquidationDateGreaterOrEqual(any())).thenReturn(Flux.fromIterable(List.of()));

        StepVerifier.create(step.process(context))
                .assertNext(result -> assertEquals(0, result.getEligibleBatches().size()))
                .verifyComplete();
    }
}

