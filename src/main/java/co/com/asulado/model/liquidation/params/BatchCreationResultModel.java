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
@Builder
public class BatchCreationResultModel {
    private boolean success;
    private String message;
    private LocalDateTime processedAt;
    private int totalParticipantsProcessed;
    private int batchesCreated;
    private int holidaysFound;
    private List<BatchSummary> batches;

    @Data
    @Builder
    public static class BatchSummary {
        private Long batchId;
        private String liquidationDate;
        private String dispersionDate;
        private int participantCount;
    }
}
