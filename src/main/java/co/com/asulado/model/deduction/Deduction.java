package co.com.asulado.model.deduction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Deduction {
    private Long deductionId;
    private Long participantId;
    private String type;
    private Boolean isAdditional;
    private String participantType;
    private Double percentage;
    private Long value;
    private Long originValue;
    private Double exchangeRate;
    private Long localValue;
    private Integer numberOfPayments;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}