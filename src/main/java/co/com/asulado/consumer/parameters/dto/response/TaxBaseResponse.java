package co.com.asulado.consumer.parameters.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaxBaseResponse(
        @JsonProperty("formula")
        String formula,
        @JsonProperty("description")
        String description
) {}