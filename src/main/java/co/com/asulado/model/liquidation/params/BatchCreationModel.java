package co.com.asulado.model.liquidation.params;

import co.com.asulado.model.common.HeadersDto;
import co.com.asulado.model.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BatchCreationModel {
    private HeadersDto headers;
    private Integer sla;
    private Integer month;
    private Integer year;
    private String calendarType;
    private LocalDate currentDate;

    public static BatchCreationModel defaultRequest() {
        return BatchCreationModel.builder()
            .sla(3)
            .month(java.time.LocalDate.now().getMonthValue())
            .year(java.time.LocalDate.now().getYear())
            .calendarType(Constants.EVENT_TYPE_HOLIDAYS_AND_WEEKENDS)
            .currentDate(LocalDate.now())
            .build();
    }
}
