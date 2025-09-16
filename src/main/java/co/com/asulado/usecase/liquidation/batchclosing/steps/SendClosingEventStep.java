package co.com.asulado.usecase.liquidation.batchclosing.steps;

import co.com.asulado.model.constants.Constants;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingContext;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendClosingEventStep implements ClosingProcessingStep {

    @Override
    public Mono<ClosingProcessingContext> process(ClosingProcessingContext context) {
        String traceId = context.getHeaders() != null ? context.getHeaders().getTraceId() : null;
        log.info(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_SENDING_CLOSING_EVENT, traceId);
        return Mono.just(context);
    }
}
