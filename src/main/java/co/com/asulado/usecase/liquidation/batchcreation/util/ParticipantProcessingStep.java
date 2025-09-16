package co.com.asulado.usecase.liquidation.batchcreation.util;

import reactor.core.publisher.Mono;

public interface ParticipantProcessingStep {
    Mono<ProcessingContext> process(ProcessingContext context);
}
