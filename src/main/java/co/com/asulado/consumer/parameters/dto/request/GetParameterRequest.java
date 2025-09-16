package co.com.asulado.consumer.parameters.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetParameterRequest {
    @JsonProperty("accion")
    private String action;
    @JsonProperty("parametro")
    private String parameter;
}