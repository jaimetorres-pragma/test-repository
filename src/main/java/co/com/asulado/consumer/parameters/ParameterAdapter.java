package co.com.asulado.consumer.parameters;

import co.com.asulado.consumer.parameters.dto.request.GetParameterRequest;
import co.com.asulado.consumer.parameters.mapper.ParameterMapper;
import co.com.asulado.consumer.parameters.mapper.ParametersFactory;
import co.com.asulado.consumer.util.RestUtil;
import co.com.asulado.consumer.util.WebConsumerConstants;
import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.error.BusinessException;
import co.com.asulado.model.error.ErrorCode;
import co.com.asulado.model.liquidation.gateways.ParameterRepository;
import co.com.asulado.model.liquidation.parameter.ParameterBase;
import co.com.asulado.validator.dto.request.ApiGenericRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


@Slf4j
@RequiredArgsConstructor
public class ParameterAdapter implements ParameterRepository {
    private final String uri;
    private final WebClient webClient;
    private final ParameterMapper mapper;


    @Override
    public Mono<ParameterBase> getParameters(String parameterName, String traceId) {
        ParametersFactory parameterFactory =  ParametersFactory.findResponseType(parameterName);
        return  webClient
                .post()
                .uri(uri)
                .header(Constants.TRACE_ID_HEADER, traceId)
                .bodyValue(createGenericRequest(parameterName))
                .retrieve()
                .bodyToMono(parameterFactory.getResponseType())
                .doOnSuccess(response -> log.info(
                        WebConsumerConstants.PARAMETER_SERVICE_RESPONSE_OK, traceId))
                .flatMap(response -> {
                    if (!response.isSuccess()) {
                        return Mono.error(new BusinessException(ErrorCode.ERROR_EXTERNAL_SERVICE, response.getMessage(), traceId));
                    }
                    return Mono.just(response.getData());
                })
                .map(data -> parameterFactory.mapToDomain(mapper, data))
                .onErrorMap(WebClientResponseException.class, ex -> RestUtil.mapWebClientException(ex, traceId));
    }

    private ApiGenericRequest<GetParameterRequest> createGenericRequest(String parameterName) {
        return ApiGenericRequest.<GetParameterRequest>builder()
                .data(GetParameterRequest.builder()
                        .parameter(parameterName)
                        .action(Constants.GET_ACTION)
                        .build())
                .build();
    }
}
