package co.com.asulado.usecase.liquidation.batchclosing;

import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.liquidation.params.BatchClosingModel;
import co.com.asulado.model.liquidation.params.BatchClosingResultModel;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingContext;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class BatchClosingForLiquidationUseCase {

    public static final String CLOSING_PROCESS_COMPLETED_MESSAGE = "Cierre completado";
    public static final String NO_BATCHES_TO_CLOSE_MESSAGE = "No hay lotes para cerrar";
    public static final String PROCESSING_FAILED_MESSAGE = "Processing failed: ";
    private final List<ClosingProcessingStep> processingSteps;

    public BatchClosingForLiquidationUseCase(@Qualifier("orderedClosingSteps") List<ClosingProcessingStep> processingSteps) {
        this.processingSteps = processingSteps;
    }

    public Mono<BatchClosingResultModel> execute(BatchClosingModel request) {
        ClosingProcessingContext initial = ClosingProcessingContext.builder()
            .headers(request.getHeaders())
            .currentDate(request.getCurrentDate())
            .build();

        return processSteps(initial, 0)
            .map(this::buildResult)
            .onErrorResume(this::handleError);
    }

    private Mono<ClosingProcessingContext> processSteps(ClosingProcessingContext context, int idx) {
        String traceId = context.getHeaders() != null ? context.getHeaders().getTraceId() : null;
        if (idx >= processingSteps.size()) {
            return Mono.just(context);
        }
        ClosingProcessingStep step = processingSteps.get(idx);
        String stepName = step.getClass().getSimpleName();
        log.debug(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_EXECUTING_STEP, traceId, idx + 1, processingSteps.size(), stepName);
        return step.process(context)
            .doOnNext(c -> log.debug(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_STEP_COMPLETED_SUCCESSFULLY, traceId, idx + 1, stepName))
            .flatMap(c -> processSteps(c, idx + 1));
    }

    private BatchClosingResultModel buildResult(ClosingProcessingContext ctx) {
        int batches = ctx.getEligibleBatches() == null ? 0 : ctx.getEligibleBatches().size();
        String message = batches > 0 ? CLOSING_PROCESS_COMPLETED_MESSAGE : NO_BATCHES_TO_CLOSE_MESSAGE;
        return BatchClosingResultModel.builder()
            .processingResult(true)
            .processingMessage(message)
            .processedAt(LocalDateTime.now())
            .totalParticipantsProcessed(ctx.getTotalParticipantsProcessed())
            .totalThirdPartiesProcessed(ctx.getTotalThirdPartiesProcessed())
            .batchesProcessed(batches)
            .batchSummaries(ctx.getSummaries() == null ? List.of() : ctx.getSummaries().stream()
                .map(s -> BatchClosingResultModel.BatchSummary.builder()
                    .batchId(s.getBatchId())
                    .participantTotalGrossAmountCop(s.getParticipantGrossTotal())
                    .participantTotalDeductionAmountCop(s.getParticipantDeductionsTotal())
                    .participantTotalNetAmountCop(s.getParticipantNetTotal())
                    .thirdPartyTotalGrossAmountCop(s.getThirdPartyGrossTotal())
                    .thirdPartyTotalDeductionAmountCop(s.getThirdPartyDeductionsTotal())
                    .thirdPartyTotalNetAmountCop(s.getThirdPartyNetTotal())
                    .batchTotalGrossAmountCop(s.getBatchGrossTotal())
                    .batchTotalDeductionAmountCop(s.getBatchDeductionsTotal())
                    .batchTotalNetAmountCop(s.getBatchNetTotal())
                    .paymentsCount(((s.getParticipantCount() == null ? 0 : s.getParticipantCount())
                        + (s.getThirdPartyCount() == null ? 0 : s.getThirdPartyCount())))
                    .build())
                .toList())
            .build();
    }

    private Mono<BatchClosingResultModel> handleError(Throwable error) {
        log.error(Constants.LOG_PROCESSING_FAILED_WITH_ERROR, error.getMessage(), error);
        return Mono.just(BatchClosingResultModel.builder()
            .processingResult(false)
            .processingMessage(PROCESSING_FAILED_MESSAGE + error.getMessage())
            .processedAt(LocalDateTime.now())
            .totalParticipantsProcessed(0)
            .totalThirdPartiesProcessed(0)
            .batchesProcessed(0)
            .batchSummaries(List.of())
            .build());
    }
}
