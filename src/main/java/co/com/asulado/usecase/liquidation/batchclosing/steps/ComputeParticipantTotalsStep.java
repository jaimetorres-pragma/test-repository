package co.com.asulado.usecase.liquidation.batchclosing.steps;

import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.model.liquidation.Participant;
import co.com.asulado.model.liquidation.gateways.LiquidationBatchRepository;
import co.com.asulado.model.liquidation.gateways.ParticipantRepository;
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
public class ComputeParticipantTotalsStep implements ClosingProcessingStep {

    private final ParticipantRepository participantRepository;
    private final LiquidationBatchRepository liquidationBatchRepository;

    @Override
    public Mono<ClosingProcessingContext> process(ClosingProcessingContext context) {
        if (context.getEligibleBatches() == null || context.getEligibleBatches().isEmpty()) {
            return Mono.just(context);
        }

        Map<Long, ClosingProcessingContext.BatchSummary> summaryByBatch = new HashMap<>();

        List<Long> batchIds = context.getEligibleBatches().stream().map(LiquidationBatch::getBatchId).toList();

        String traceId = context.getHeaders() != null ? context.getHeaders().getTraceId() : null;
        log.info(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_COMPUTING_PARTICIPANT_TOTALS, traceId, batchIds.size());

        return Flux.fromIterable(batchIds)
            .flatMap(batchId -> participantRepository.findAllByBatchId(batchId).collectList()
                .flatMap(participants -> {
                    int count = participants.size();
                    long gross = sum(participants, Participant::getGrossAmountCop);
                    long deductions = sum(participants, Participant::getDeductionAmountCop);
                    long net = gross - deductions;

                    summaryByBatch.compute(batchId, (k,v) -> {
                        if (v == null) v = ClosingProcessingContext.BatchSummary.builder().batchId(batchId).build();
                        v.setParticipantGrossTotal(gross);
                        v.setParticipantDeductionsTotal(deductions);
                        v.setParticipantNetTotal(net);
                        v.setParticipantCount(count);
                        return v;
                    });

                    log.info(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_UPDATED_PARTICIPANT_TOTALS_FOR_BATCH, traceId, batchId, count, gross, deductions, net);
                    return liquidationBatchRepository.updateParticipantTotals(batchId, count, gross, deductions, net)
                        .thenReturn(count);
                })
            )
            .collectList()
            .map(counts -> {
                int totalProcessed = counts.stream().mapToInt(Integer::intValue).sum();
                List<ClosingProcessingContext.BatchSummary> summaries = new ArrayList<>(summaryByBatch.values());
                return context.toBuilder()
                    .totalParticipantsProcessed(totalProcessed)
                    .summaries(mergeSummaries(context.getSummaries(), summaries))
                    .build();
            });
    }

    private long sum(List<Participant> list, Function<Participant, Long> getter) {
        return list.stream().map(getter).filter(Objects::nonNull).mapToLong(Long::longValue).sum();
    }

    private List<ClosingProcessingContext.BatchSummary> mergeSummaries(List<ClosingProcessingContext.BatchSummary> current,
                                                                       List<ClosingProcessingContext.BatchSummary> updates) {
        Map<Long, ClosingProcessingContext.BatchSummary> map = new HashMap<>();
        if (current != null) current.forEach(s -> map.put(s.getBatchId(), s));
        updates.forEach(s -> map.merge(s.getBatchId(), s, (a,b) -> {
            if (b.getParticipantGrossTotal() != null) a.setParticipantGrossTotal(b.getParticipantGrossTotal());
            if (b.getParticipantDeductionsTotal() != null) a.setParticipantDeductionsTotal(b.getParticipantDeductionsTotal());
            if (b.getParticipantNetTotal() != null) a.setParticipantNetTotal(b.getParticipantNetTotal());
            if (b.getParticipantCount() != null) a.setParticipantCount(b.getParticipantCount());
            return a;
        }));
        return new ArrayList<>(map.values());
    }
}
