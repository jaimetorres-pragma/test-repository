package co.com.asulado.consumer.parameters.mapper;

import co.com.asulado.consumer.parameters.dto.response.GeneralParameterResponse;
import co.com.asulado.consumer.parameters.dto.response.LiquidationParameterResponse;
import co.com.asulado.model.liquidation.parameter.GeneralParameter;
import co.com.asulado.model.liquidation.parameter.LiquidationParameter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParameterMapper {

    LiquidationParameter toLiquidationDomain(LiquidationParameterResponse entity);
    GeneralParameter toGeneralDomain(GeneralParameterResponse response);
}