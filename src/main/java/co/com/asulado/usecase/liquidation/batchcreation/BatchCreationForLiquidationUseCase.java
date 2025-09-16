package co.com.asulado.usecase.liquidation.batchcreation;

import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.liquidation.params.BatchCreationModel;
import co.com.asulado.model.liquidation.params.BatchCreationResultModel;
import co.com.asulado.usecase.liquidation.batchcreation.util.ParticipantProcessingStep;
import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class BatchCreationForLiquidationUseCase {

    private final List<ParticipantProcessingStep> processingSteps;

    public BatchCreationForLiquidationUseCase(@Qualifier("orderedProcessingSteps") List<ParticipantProcessingStep> processingSteps) {
        this.processingSteps = processingSteps;
    }

    public Mono<BatchCreationResultModel> execute(BatchCreationModel request) {
        String traceId = request.getHeaders() != null ? request.getHeaders().getTraceId() : null;
        log.info(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_STARTING_PARTICIPANT_PROCESSING,
                traceId, request.getSla(), request.getMonth(), request.getYear());

        ProcessingContext initialContext = ProcessingContext.builder()
                .headers(request.getHeaders())
                .sla(request.getSla())
                .month(request.getMonth())
                .year(request.getYear())
                .calendarType(request.getCalendarType())
                .currentDate(request.getCurrentDate())
                .build();

        return processSteps(initialContext, 0)
                .map(this::buildResult)
                .doOnSuccess(result -> log.info(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_PROCESSING_COMPLETED_SUCCESSFULLY,
                        traceId, result.getBatchesCreated(), result.getTotalParticipantsProcessed()))
                .doOnError(error -> log.error(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_ERROR_DURING_PROCESSING, traceId, error))
                .onErrorResume(this::handleError);
    }

    private Mono<ProcessingContext> processSteps(ProcessingContext context, int stepIndex) {
        String traceId = context.getHeaders() != null ? context.getHeaders().getTraceId() : null;
        if (stepIndex >= processingSteps.size()) {
            log.info(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_ALL_PROCESSING_STEPS_COMPLETED, traceId);
            return Mono.just(context);
        }

        ParticipantProcessingStep currentStep = processingSteps.get(stepIndex);
        String stepName = currentStep.getClass().getSimpleName();

        log.debug(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_EXECUTING_STEP, traceId, stepIndex + 1, processingSteps.size(), stepName);

        return currentStep.process(context)
                .doOnNext(updatedContext -> log.debug(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_STEP_COMPLETED_SUCCESSFULLY, traceId, stepIndex + 1, stepName))
                .doOnError(error -> log.error(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_ERROR_IN_STEP, traceId, stepIndex + 1, stepName, error.getMessage()))
                .flatMap(updatedContext -> processSteps(updatedContext, stepIndex + 1));
    }

    private BatchCreationResultModel buildResult(ProcessingContext context) {
        List<BatchCreationResultModel.BatchSummary> batchSummaries = context.getCreatedBatches() != null
            ? context.getCreatedBatches().stream()
                .map(batch -> BatchCreationResultModel.BatchSummary.builder()
                        .batchId(batch.getBatchId())
                        .liquidationDate(batch.getLiquidationDate().toString())
                        .dispersionDate(batch.getDispersionDate().toString())
                        .participantCount(batch.getParticipantCount())
                        .build())
                .toList()
            : List.of();

        int totalParticipants = context.getParticipants() != null ? context.getParticipants().size() : 0;
        int batchesCreated = context.getCreatedBatches() != null ? context.getCreatedBatches().size() : 0;
        int holidaysFound = context.getHolidays() != null ? context.getHolidays().size() : 0;

        return BatchCreationResultModel.builder()
                .success(true)
                .message("Participantes procesados exitosamente para creacion de lotes")
                .processedAt(LocalDateTime.now())
                .totalParticipantsProcessed(totalParticipants)
                .batchesCreated(batchesCreated)
                .holidaysFound(holidaysFound)
                .batches(batchSummaries)
                .build();
    }

    private Mono<BatchCreationResultModel> handleError(Throwable error) {
        log.error(Constants.LOG_PROCESSING_FAILED_WITH_ERROR, error.getMessage(), error);

        return Mono.just(BatchCreationResultModel.builder()
                .success(false)
                .message("Processing failed: " + error.getMessage())
                .processedAt(LocalDateTime.now())
                .totalParticipantsProcessed(0)
                .batchesCreated(0)
                .holidaysFound(0)
                .batches(List.of())
                .build());
    }
}
