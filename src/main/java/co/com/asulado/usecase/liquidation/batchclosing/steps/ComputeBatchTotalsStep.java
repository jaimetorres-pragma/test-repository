package co.com.asulado.usecase.liquidation.batchclosing.steps;

import co.com.asulado.model.liquidation.gateways.LiquidationBatchRepository;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingContext;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ComputeBatchTotalsStep implements ClosingProcessingStep {

    private final LiquidationBatchRepository liquidationBatchRepository;

    @Override
    public Mono<ClosingProcessingContext> process(ClosingProcessingContext context) {
        if (context.getSummaries() == null || context.getSummaries().isEmpty()) {
            return Mono.just(context);
        }
        List<ClosingProcessingContext.BatchSummary> updated = context.getSummaries().stream()
                .map(s -> {
                    long pg = nvl(s.getParticipantGrossTotal());
                    long pd = nvl(s.getParticipantDeductionsTotal());
                    long tg = nvl(s.getThirdPartyGrossTotal());
                    long td = nvl(s.getThirdPartyDeductionsTotal());
                    long bg = pg + tg;
                    long bd = pd + td;
                    long bn = bg - bd;
                    return s.toBuilder()
                            .batchGrossTotal(bg)
                            .batchDeductionsTotal(bd)
                            .batchNetTotal(bn)
                            .build();
                })
                .toList();

        return Flux.fromIterable(updated)
                .flatMap(s -> liquidationBatchRepository.recomputeBatchTotals(s.getBatchId()).thenReturn(s))
                .collectList()
                .map(list -> context.toBuilder().summaries(list).build());
    }

    private long nvl(Long v) { return v == null ? 0L : v; }
}

