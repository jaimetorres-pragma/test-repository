package co.com.asulado.consumer.parameters.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompensationFundResponse(
        @JsonProperty("reglas")
        CompensationRulesResponse rules,
        @JsonProperty("SMMLV_maximo")
        float smmlvMax,
        @JsonProperty("active")
        boolean active
) {}