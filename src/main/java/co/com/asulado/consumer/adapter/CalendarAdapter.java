package co.com.asulado.consumer.adapter;

import co.com.asulado.consumer.dto.CalendarDto;
import co.com.asulado.consumer.dto.request.CalendarSearchDto;
import co.com.asulado.consumer.mapper.CalendarMapper;
import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.error.BusinessException;
import co.com.asulado.model.error.ErrorCode;
import co.com.asulado.model.paymentservice.calendar.Calendar;
import co.com.asulado.model.paymentservice.calendar.gateway.CalendarGateway;
import co.com.asulado.validator.dto.request.ApiGenericRequest;
import co.com.asulado.validator.dto.response.ApiGenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class CalendarAdapter implements CalendarGateway {

    private final WebClient webClient;
    private final CalendarMapper mapper;

    public CalendarAdapter(@Qualifier("paymentsWebClient") WebClient webClient, CalendarMapper mapper) {
        this.webClient = webClient;
        this.mapper = mapper;
    }


    @Override
    public Flux<Calendar> findByMonthYearAndType(int month, int year, String type) {
        log.info(Constants.LOG_FETCHING_CALENDAR_EVENTS, month, year, type);

        String traceId = UUID.randomUUID().toString();

        ApiGenericRequest<CalendarSearchDto> request = ApiGenericRequest.<CalendarSearchDto>builder()
                .data(CalendarSearchDto.builder()
                        .month(month)
                        .year(year)
                        .type(type)
                        .build())
                .build();

        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiGenericResponse<List<CalendarDto>>>() {})
                .flatMapMany(response -> {
                    if (!response.isSuccess()) {
                        return Flux.error(new BusinessException(ErrorCode.ERROR_EXTERNAL_SERVICE, response.getMessage(), traceId));
                    }
                    return Flux.fromIterable(response.getData())
                            .map(mapper::toDomain);
                })
                .onErrorMap(WebClientResponseException.class, ex -> mapWebClientException(ex, traceId));
    }
    private BusinessException mapWebClientException(WebClientResponseException ex, String traceId) {
        log.error(Constants.LOG_WEBCLIENT_ERROR, ex.getStatusCode(), ex.getResponseBodyAsString());

        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new BusinessException(ErrorCode.ERROR_NOT_FOUND, traceId);
        } else if (ex.getStatusCode().is4xxClientError()) {
            return new BusinessException(ErrorCode.VALIDATION_ERROR, traceId);
        } else if (ex.getStatusCode().is5xxServerError()) {
            return new BusinessException(ErrorCode.ERROR_INTERNAL, traceId);
        }

        return new BusinessException(ErrorCode.ERROR_INTERNAL, traceId);
    }
}
