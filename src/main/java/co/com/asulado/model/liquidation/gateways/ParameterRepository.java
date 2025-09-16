package co.com.asulado.model.liquidation.gateways;

import co.com.asulado.model.liquidation.parameter.ParameterBase;
import reactor.core.publisher.Mono;

public interface ParameterRepository {
    Mono<ParameterBase> getParameters(String parameterName, String traceId);
}
