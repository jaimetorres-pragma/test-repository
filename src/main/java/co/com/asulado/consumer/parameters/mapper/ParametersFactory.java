package co.com.asulado.consumer.parameters.mapper;

import co.com.asulado.consumer.parameters.dto.response.GeneralParameterResponse;
import co.com.asulado.consumer.parameters.dto.response.LiquidationParameterResponse;
import co.com.asulado.consumer.parameters.dto.response.ParameterResponse;
import co.com.asulado.consumer.util.WebConsumerConstants;
import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.liquidation.parameter.GeneralParameter;
import co.com.asulado.model.liquidation.parameter.ParameterBase;
import co.com.asulado.model.liquidation.parameter.LiquidationParameter;
import co.com.asulado.validator.dto.response.ApiGenericResponse;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

import java.util.Arrays;
import java.util.function.BiFunction;

@Getter
public enum ParametersFactory {

    LIQUIDATION(
            Constants.LIQUIDATION_PARAMETER_KEY,
            LiquidationParameterResponse.class,
            LiquidationParameter.class,
            adapt(LiquidationParameterResponse.class, ParameterMapper::toLiquidationDomain)),
    GENERAL(
            Constants.GENERAL_PARAMETER_KEY,
            GeneralParameterResponse.class,
            GeneralParameter.class,
            adapt(GeneralParameterResponse.class, ParameterMapper::toGeneralDomain));

    private final String type;
    private final Class<? extends ParameterResponse> payloadClass;
    private final Class<? extends ParameterBase>     domainClass;
    private final ParameterizedTypeReference<ApiGenericResponse<? extends ParameterResponse>> responseType;
    private final BiFunction<ParameterMapper, ParameterResponse, ParameterBase> toDomainFn;

    ParametersFactory(
            String type,
            Class<? extends ParameterResponse> payloadClass,
            Class<? extends ParameterBase> domainClass,
            BiFunction<ParameterMapper, ParameterResponse, ParameterBase> toDomainFn) {
        this.type = type;
        this.payloadClass = payloadClass;
        this.domainClass = domainClass;
        this.responseType = ptrOf(payloadClass);
        this.toDomainFn = toDomainFn;
    }

    public static ParametersFactory findResponseType(String type) {
        ParametersFactory pf = Arrays.stream(ParametersFactory.values())
                .filter(parameter -> parameter.getType().equals(type)).findFirst().orElse(null);

        if (pf == null) {
            throw new IllegalArgumentException(String.format(WebConsumerConstants.NO_SUPPORTED_TYPE, type));
        }
        return pf;
    }

    public ParameterBase mapToDomain(ParameterMapper mapper, ParameterResponse src) {
        if (!payloadClass.isInstance(src)) {
            throw new IllegalArgumentException(String.format(WebConsumerConstants.INCORRECT_CLASS,
                    payloadClass.getSimpleName(), src.getClass().getSimpleName()));
        }
        return toDomainFn.apply(mapper, src);
    }


    private static ParameterizedTypeReference<ApiGenericResponse<? extends ParameterResponse>> ptrOf(
            Class<? extends ParameterResponse> payloadClass) {
        return ParameterizedTypeReference.forType(
                ResolvableType.forClassWithGenerics(ApiGenericResponse.class, payloadClass).getType()
        );
    }

    private static <S extends ParameterResponse, D extends ParameterBase>
    BiFunction<ParameterMapper, ParameterResponse, ParameterBase> adapt(
            Class<S> cls, BiFunction<ParameterMapper, S, D> fn) {
        return (m, r) -> fn.apply(m, cls.cast(r));
    }
}
