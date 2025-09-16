package co.com.asulado.consumer.parameters.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SecurityRulesResponse(
        @JsonProperty("limites")
        List<LimitResponse> limits,
        @JsonProperty("redondeo")
        float rounding
) { }
