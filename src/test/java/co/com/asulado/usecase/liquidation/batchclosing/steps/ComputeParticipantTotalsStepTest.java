package co.com.asulado.usecase.liquidation.batchclosing.steps;

import co.com.asulado.model.liquidation.gateways.LiquidationBatchRepository;
import co.com.asulado.model.liquidation.gateways.ParticipantRepository;
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
@DisplayName("Pruebas de ComputeParticipantTotalsStep")
class ComputeParticipantTotalsStepTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LiquidationBatchRepository liquidationBatchRepository;

    @InjectMocks
    private ComputeParticipantTotalsStep step;

    @Test
    @DisplayName("Debe calcular totales de participantes y actualizar contexto y lote")
    void shouldComputeParticipantTotalsAndUpdateContextAndBatch() {
        var context = ClosingUseCaseTestData.createContextWithEligibleBatches();
        when(participantRepository.findAllByBatchId(ClosingUseCaseTestData.BATCH_ID_1)).thenReturn(Flux.fromIterable(ClosingUseCaseTestData.createParticipantsForBatch1()));
        when(participantRepository.findAllByBatchId(ClosingUseCaseTestData.BATCH_ID_2)).thenReturn(Flux.fromIterable(ClosingUseCaseTestData.createParticipantsForBatch2()));
        when(liquidationBatchRepository.updateParticipantTotals(ClosingUseCaseTestData.BATCH_ID_1, ClosingUseCaseTestData.P_COUNT_B1, ClosingUseCaseTestData.P_GROSS_B1, ClosingUseCaseTestData.P_DED_B1, ClosingUseCaseTestData.P_NET_B1)).thenReturn(Mono.empty());
        when(liquidationBatchRepository.updateParticipantTotals(ClosingUseCaseTestData.BATCH_ID_2, ClosingUseCaseTestData.P_COUNT_B2, ClosingUseCaseTestData.P_GROSS_B2, ClosingUseCaseTestData.P_DED_B2, ClosingUseCaseTestData.P_NET_B2)).thenReturn(Mono.empty());

        StepVerifier.create(step.process(context))
                .assertNext(result -> {
                    assertEquals(ClosingUseCaseTestData.P_COUNT_B1 + ClosingUseCaseTestData.P_COUNT_B2, result.getTotalParticipantsProcessed());
                    var summaries = result.getSummaries();
                    var s1 = summaries.stream().filter(s -> ClosingUseCaseTestData.BATCH_ID_1 == s.getBatchId()).findFirst().orElseThrow();
                    var s2 = summaries.stream().filter(s -> ClosingUseCaseTestData.BATCH_ID_2 == s.getBatchId()).findFirst().orElseThrow();
                    assertEquals(ClosingUseCaseTestData.P_GROSS_B1, s1.getParticipantGrossTotal());
                    assertEquals(ClosingUseCaseTestData.P_DED_B1, s1.getParticipantDeductionsTotal());
                    assertEquals(ClosingUseCaseTestData.P_NET_B1, s1.getParticipantNetTotal());
                    assertEquals(ClosingUseCaseTestData.P_GROSS_B2, s2.getParticipantGrossTotal());
                    assertEquals(ClosingUseCaseTestData.P_DED_B2, s2.getParticipantDeductionsTotal());
                    assertEquals(ClosingUseCaseTestData.P_NET_B2, s2.getParticipantNetTotal());
                })
                .verifyComplete();

        verify(liquidationBatchRepository, times(1)).updateParticipantTotals(ClosingUseCaseTestData.BATCH_ID_1, ClosingUseCaseTestData.P_COUNT_B1, ClosingUseCaseTestData.P_GROSS_B1, ClosingUseCaseTestData.P_DED_B1, ClosingUseCaseTestData.P_NET_B1);
        verify(liquidationBatchRepository, times(1)).updateParticipantTotals(ClosingUseCaseTestData.BATCH_ID_2, ClosingUseCaseTestData.P_COUNT_B2, ClosingUseCaseTestData.P_GROSS_B2, ClosingUseCaseTestData.P_DED_B2, ClosingUseCaseTestData.P_NET_B2);
    }
}
