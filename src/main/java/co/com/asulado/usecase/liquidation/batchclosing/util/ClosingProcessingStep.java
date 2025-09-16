package co.com.asulado.usecase.liquidation.batchclosing.util;

import reactor.core.publisher.Mono;

public interface ClosingProcessingStep {
    Mono<ClosingProcessingContext> process(ClosingProcessingContext context);
}

