package co.com.asulado.consumer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarSearchDto {
    @JsonProperty("mes")
    private Integer month;
    @JsonProperty("anio")
    private Integer year;
    @JsonProperty("tipo")
    private String type;
}

