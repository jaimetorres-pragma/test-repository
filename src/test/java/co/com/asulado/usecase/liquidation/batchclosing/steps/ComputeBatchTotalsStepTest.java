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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de ComputeBatchTotalsStep")
class ComputeBatchTotalsStepTest {

    @Mock
    private LiquidationBatchRepository liquidationBatchRepository;

    @InjectMocks
    private ComputeBatchTotalsStep step;

    @Test
    @DisplayName("Debe calcular totales del lote y persistirlos")
    void shouldComputeBatchTotalsAndPersist() {
        var context = ClosingUseCaseTestData.createContextWithSummaries();
        when(liquidationBatchRepository.recomputeBatchTotals(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(step.process(context))
                .assertNext(result -> {
                    var s1 = result.getSummaries().stream().filter(s -> s.getBatchId().equals(ClosingUseCaseTestData.BATCH_ID_1)).findFirst().orElseThrow();
                    var s2 = result.getSummaries().stream().filter(s -> s.getBatchId().equals(ClosingUseCaseTestData.BATCH_ID_2)).findFirst().orElseThrow();
                    assertEquals(ClosingUseCaseTestData.P_GROSS_B1 + ClosingUseCaseTestData.T_GROSS_B1, s1.getBatchGrossTotal());
                    assertEquals(ClosingUseCaseTestData.P_DED_B1 + ClosingUseCaseTestData.T_DED_B1, s1.getBatchDeductionsTotal());
                    assertEquals((ClosingUseCaseTestData.P_GROSS_B1 + ClosingUseCaseTestData.T_GROSS_B1) - (ClosingUseCaseTestData.P_DED_B1 + ClosingUseCaseTestData.T_DED_B1), s1.getBatchNetTotal());
                    assertEquals(ClosingUseCaseTestData.P_GROSS_B2 + ClosingUseCaseTestData.T_GROSS_B2, s2.getBatchGrossTotal());
                    assertEquals(ClosingUseCaseTestData.P_DED_B2 + ClosingUseCaseTestData.T_DED_B2, s2.getBatchDeductionsTotal());
                    assertEquals((ClosingUseCaseTestData.P_GROSS_B2 + ClosingUseCaseTestData.T_GROSS_B2) - (ClosingUseCaseTestData.P_DED_B2 + ClosingUseCaseTestData.T_DED_B2), s2.getBatchNetTotal());
                })
                .verifyComplete();

        verify(liquidationBatchRepository, times(1)).recomputeBatchTotals(ClosingUseCaseTestData.BATCH_ID_1);
        verify(liquidationBatchRepository, times(1)).recomputeBatchTotals(ClosingUseCaseTestData.BATCH_ID_2);
    }

    @Test
    @DisplayName("Debe retornar contexto sin cambios cuando no hay resúmenes")
    void shouldReturnContextWhenNoSummaries() {
        var context = ClosingProcessingContext.builder().currentDate(ClosingUseCaseTestData.CURRENT_DATE).build();
        StepVerifier.create(step.process(context))
                .assertNext(r -> assertEquals(context, r))
                .verifyComplete();
    }
}

