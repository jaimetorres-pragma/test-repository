package co.com.asulado.model.liquidation.params;

import co.com.asulado.model.common.HeadersDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BatchClosingModel {
    private HeadersDto headers;
    private LocalDate currentDate;

    public static BatchClosingModel defaultRequest() {
        return BatchClosingModel.builder()
            .currentDate(LocalDate.now())
            .build();
    }
}
