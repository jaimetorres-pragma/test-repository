package co.com.asulado.consumer.parameters.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LimitResponse(
        @JsonProperty("SMMLV_minimo")
        float smmlvMin,
        @JsonProperty("SMMLV_maximo")
        float smmlvMax,
        @JsonProperty("percentaje_descuento")
        float discountPercentage,
        @JsonProperty("FSP")
        float fsp,
        @JsonProperty("FSPS")
        float fsps
) { }
