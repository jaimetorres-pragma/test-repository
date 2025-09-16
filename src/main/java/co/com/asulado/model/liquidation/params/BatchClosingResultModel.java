package co.com.asulado.model.liquidation.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BatchClosingResultModel {
    private boolean processingResult;
    private String processingMessage;
    private LocalDateTime processedAt;
    private int totalParticipantsProcessed;
    private int totalThirdPartiesProcessed;
    private int batchesProcessed;
    private List<BatchSummary> batchSummaries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class BatchSummary {
        private Long batchId;
        private Long participantTotalGrossAmountCop;
        private Long participantTotalDeductionAmountCop;
        private Long participantTotalNetAmountCop;
        private Long thirdPartyTotalGrossAmountCop;
        private Long thirdPartyTotalDeductionAmountCop;
        private Long thirdPartyTotalNetAmountCop;
        private Long batchTotalGrossAmountCop;
        private Long batchTotalDeductionAmountCop;
        private Long batchTotalNetAmountCop;
        private Integer paymentsCount;
    }
}
