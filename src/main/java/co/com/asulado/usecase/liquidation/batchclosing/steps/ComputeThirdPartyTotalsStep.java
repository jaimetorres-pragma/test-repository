package co.com.asulado.usecase.liquidation.batchclosing.steps;

import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.model.liquidation.ThirdParty;
import co.com.asulado.model.liquidation.gateways.LiquidationBatchRepository;
import co.com.asulado.model.liquidation.gateways.ThirdPartyRepository;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingContext;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class ComputeThirdPartyTotalsStep implements ClosingProcessingStep {

    private final ThirdPartyRepository thirdPartyRepository;
    private final LiquidationBatchRepository liquidationBatchRepository;

    @Override
    public Mono<ClosingProcessingContext> process(ClosingProcessingContext context) {
        if (context.getEligibleBatches() == null || context.getEligibleBatches().isEmpty()) {
            return Mono.just(context);
        }

        Map<Long, ClosingProcessingContext.BatchSummary> summaryByBatch = new HashMap<>();
        List<Long> batchIds = context.getEligibleBatches().stream().map(LiquidationBatch::getBatchId).toList();

        String traceId = context.getHeaders() != null ? context.getHeaders().getTraceId() : null;
        log.info(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_COMPUTING_THIRD_PARTY_TOTALS, traceId, batchIds.size());

        return Flux.fromIterable(batchIds)
            .flatMap(batchId -> thirdPartyRepository.findAllByBatchId(batchId).collectList()
                .flatMap(thirdParties -> {
                    int count = thirdParties.size();
                    long gross = sum(thirdParties, ThirdParty::getGrossAmountCop);
                    long deductions = sum(thirdParties, ThirdParty::getDeductionAmountCop);
                    long net = gross - deductions;

                    summaryByBatch.compute(batchId, (k,v) -> {
                        if (v == null) v = ClosingProcessingContext.BatchSummary.builder().batchId(batchId).build();
                        v.setThirdPartyGrossTotal(gross);
                        v.setThirdPartyDeductionsTotal(deductions);
                        v.setThirdPartyNetTotal(net);
                        v.setThirdPartyCount(count);
                        return v;
                    });

                    log.info(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_UPDATED_THIRD_PARTY_TOTALS_FOR_BATCH, traceId, batchId, count, gross, deductions, net);
                    return liquidationBatchRepository.updateThirdPartyTotals(batchId, count, gross, deductions, net)
                        .thenReturn(count);
                })
            )
            .collectList()
            .map(counts -> {
                int totalProcessed = counts.stream().mapToInt(Integer::intValue).sum();
                List<ClosingProcessingContext.BatchSummary> summaries = new ArrayList<>(summaryByBatch.values());
                return context.toBuilder()
                    .totalThirdPartiesProcessed(totalProcessed)
                    .summaries(mergeSummaries(context.getSummaries(), summaries))
                    .build();
            });
    }

    private long sum(List<ThirdParty> list, Function<ThirdParty, Long> getter) {
        return list.stream().map(getter).filter(Objects::nonNull).mapToLong(Long::longValue).sum();
    }

    private List<ClosingProcessingContext.BatchSummary> mergeSummaries(List<ClosingProcessingContext.BatchSummary> current,
                                                                       List<ClosingProcessingContext.BatchSummary> updates) {
        Map<Long, ClosingProcessingContext.BatchSummary> map = new HashMap<>();
        if (current != null) current.forEach(s -> map.put(s.getBatchId(), s));
        updates.forEach(s -> map.merge(s.getBatchId(), s, (a,b) -> {
            if (b.getThirdPartyGrossTotal() != null) a.setThirdPartyGrossTotal(b.getThirdPartyGrossTotal());
            if (b.getThirdPartyDeductionsTotal() != null) a.setThirdPartyDeductionsTotal(b.getThirdPartyDeductionsTotal());
            if (b.getThirdPartyNetTotal() != null) a.setThirdPartyNetTotal(b.getThirdPartyNetTotal());
            if (b.getThirdPartyCount() != null) a.setThirdPartyCount(b.getThirdPartyCount());
            return a;
        }));
        return new ArrayList<>(map.values());
    }
}
