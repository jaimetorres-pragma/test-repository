package co.com.asulado.consumer.parameters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ParameterResponse {
    @JsonProperty("parametro")
    protected String parameter;
}
