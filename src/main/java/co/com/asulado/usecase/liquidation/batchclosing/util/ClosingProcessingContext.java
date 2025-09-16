package co.com.asulado.usecase.liquidation.batchclosing.util;

import co.com.asulado.model.common.HeadersDto;
import co.com.asulado.model.liquidation.LiquidationBatch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ClosingProcessingContext {
    private HeadersDto headers;
    private LocalDate currentDate;
    private List<LiquidationBatch> eligibleBatches;

    private int totalParticipantsProcessed;
    private int totalThirdPartiesProcessed;

    @Builder.Default
    private List<BatchSummary> summaries = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class BatchSummary {
        private Long batchId;
        private Long participantGrossTotal;
        private Long participantDeductionsTotal;
        private Long participantNetTotal;
        private Long thirdPartyGrossTotal;
        private Long thirdPartyDeductionsTotal;
        private Long thirdPartyNetTotal;
        private Long batchGrossTotal;
        private Long batchDeductionsTotal;
        private Long batchNetTotal;
        private Integer participantCount;
        private Integer thirdPartyCount;
    }
}
