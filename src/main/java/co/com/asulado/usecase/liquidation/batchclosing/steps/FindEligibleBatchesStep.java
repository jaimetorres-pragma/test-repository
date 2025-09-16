package co.com.asulado.usecase.liquidation.batchclosing.steps;

import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.liquidation.gateways.LiquidationBatchRepository;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingContext;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindEligibleBatchesStep implements ClosingProcessingStep {

    private final LiquidationBatchRepository liquidationBatchRepository;

    @Override
    public Mono<ClosingProcessingContext> process(ClosingProcessingContext context) {
        String traceId = context.getHeaders() != null ? context.getHeaders().getTraceId() : null;
        log.info(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_FINDING_ELIGIBLE_BATCHES, traceId, context.getCurrentDate());
        return liquidationBatchRepository.findByLiquidationDateGreaterOrEqual(context.getCurrentDate())
            .collectList()
            .flatMap(batches -> {
                int found = batches == null ? 0 : batches.size();
                log.info(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_FOUND_ELIGIBLE_BATCHES, traceId, found);
                if (batches == null || batches.isEmpty()) {
                    return Mono.just(context.toBuilder()
                        .eligibleBatches(List.of())
                        .build());
                }
                return Mono.just(context.toBuilder()
                    .eligibleBatches(batches)
                    .build());
            });
    }
}
