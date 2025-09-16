package co.com.asulado.consumer.parameters.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;


import java.util.List;
import java.util.Map;

@Value
@Builder
@Jacksonized
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiquidationParameterResponse extends ParameterResponse {
        @JsonProperty("porcentaje_endeudamiento")
        float debtPercentage;
        @JsonProperty("sla")
        Map<String, Integer> sla;
        @JsonProperty("seguridad_social")
        Map<String, SocialSecurityResponse> socialSecurity;
        @JsonProperty("caja_compensacion")
        CompensationFundResponse compensationFund;
        @JsonProperty("base_gravable")
        TaxBaseResponse taxableBase;
        @JsonProperty("tipos_ingreso")
        List<String> incomeTypes;
        @JsonProperty("tipo_deduccion")
        List<String> deductionTypes;
}