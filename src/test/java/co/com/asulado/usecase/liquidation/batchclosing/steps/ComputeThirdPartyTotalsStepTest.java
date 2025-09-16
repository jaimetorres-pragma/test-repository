package co.com.asulado.usecase.liquidation.batchclosing.steps;

import co.com.asulado.model.liquidation.gateways.LiquidationBatchRepository;
import co.com.asulado.model.liquidation.gateways.ThirdPartyRepository;
import co.com.asulado.usecase.liquidation.testdata.ClosingUseCaseTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de ComputeThirdPartyTotalsStep")
class ComputeThirdPartyTotalsStepTest {

    @Mock
    private ThirdPartyRepository thirdPartyRepository;

    @Mock
    private LiquidationBatchRepository liquidationBatchRepository;

    @InjectMocks
    private ComputeThirdPartyTotalsStep step;

    @Test
    @DisplayName("Debe calcular totales de terceros y actualizar contexto y lote")
    void shouldComputeThirdPartyTotalsAndUpdateContextAndBatch() {
        var context = ClosingUseCaseTestData.createContextWithEligibleBatches();
        when(thirdPartyRepository.findAllByBatchId(ClosingUseCaseTestData.BATCH_ID_1)).thenReturn(Flux.fromIterable(ClosingUseCaseTestData.createThirdPartiesForBatch1()));
        when(thirdPartyRepository.findAllByBatchId(ClosingUseCaseTestData.BATCH_ID_2)).thenReturn(Flux.fromIterable(ClosingUseCaseTestData.createThirdPartiesForBatch2()));
        when(liquidationBatchRepository.updateThirdPartyTotals(ClosingUseCaseTestData.BATCH_ID_1, ClosingUseCaseTestData.T_COUNT_B1, ClosingUseCaseTestData.T_GROSS_B1, ClosingUseCaseTestData.T_DED_B1, ClosingUseCaseTestData.T_NET_B1)).thenReturn(Mono.empty());
        when(liquidationBatchRepository.updateThirdPartyTotals(ClosingUseCaseTestData.BATCH_ID_2, ClosingUseCaseTestData.T_COUNT_B2, ClosingUseCaseTestData.T_GROSS_B2, ClosingUseCaseTestData.T_DED_B2, ClosingUseCaseTestData.T_NET_B2)).thenReturn(Mono.empty());

        StepVerifier.create(step.process(context))
                .assertNext(result -> {
                    assertEquals(ClosingUseCaseTestData.T_COUNT_B1 + ClosingUseCaseTestData.T_COUNT_B2, result.getTotalThirdPartiesProcessed());
                    var summaries = result.getSummaries();
                    var s1 = summaries.stream().filter(s -> ClosingUseCaseTestData.BATCH_ID_1 == s.getBatchId()).findFirst().orElseThrow();
                    var s2 = summaries.stream().filter(s -> ClosingUseCaseTestData.BATCH_ID_2 == s.getBatchId()).findFirst().orElseThrow();
                    assertEquals(ClosingUseCaseTestData.T_GROSS_B1, s1.getThirdPartyGrossTotal());
                    assertEquals(ClosingUseCaseTestData.T_DED_B1, s1.getThirdPartyDeductionsTotal());
                    assertEquals(ClosingUseCaseTestData.T_NET_B1, s1.getThirdPartyNetTotal());
                    assertEquals(ClosingUseCaseTestData.T_GROSS_B2, s2.getThirdPartyGrossTotal());
                    assertEquals(ClosingUseCaseTestData.T_DED_B2, s2.getThirdPartyDeductionsTotal());
                    assertEquals(ClosingUseCaseTestData.T_NET_B2, s2.getThirdPartyNetTotal());
                })
                .verifyComplete();

        verify(liquidationBatchRepository, times(1)).updateThirdPartyTotals(ClosingUseCaseTestData.BATCH_ID_1, ClosingUseCaseTestData.T_COUNT_B1, ClosingUseCaseTestData.T_GROSS_B1, ClosingUseCaseTestData.T_DED_B1, ClosingUseCaseTestData.T_NET_B1);
        verify(liquidationBatchRepository, times(1)).updateThirdPartyTotals(ClosingUseCaseTestData.BATCH_ID_2, ClosingUseCaseTestData.T_COUNT_B2, ClosingUseCaseTestData.T_GROSS_B2, ClosingUseCaseTestData.T_DED_B2, ClosingUseCaseTestData.T_NET_B2);
    }
}
