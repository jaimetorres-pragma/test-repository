package co.com.asulado.consumer.parameters.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompensationRulesResponse(
        @JsonProperty("rangos")
        List<Float> ranges,
        @JsonProperty("redondeo")
        float rounding
) { }
