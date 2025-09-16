package co.com.asulado.model.liquidation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LiquidationBatch {
    private Long batchId;
    private LocalDate liquidationDate;
    private LocalDate dispersionDate;
    private String liquidationPeriod;
    private String status;
    private Integer participantCount;
    private Integer thirdPartyCount;
    private Integer paymentsCount;
    private Long totalGrossAmountCop;
    private Long totalDeductionAmountCop;
    private Long totalNetAmountCop;
    private Long totalThirdPartyGrossAmountCop;
    private Long totalThirdPartyDeductionAmountCop;
    private Long totalThirdPartyNetAmountCop;
    private String batchReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
