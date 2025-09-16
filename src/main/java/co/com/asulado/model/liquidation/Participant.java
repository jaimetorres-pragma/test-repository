package co.com.asulado.model.liquidation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Participant {
    private Long participantId;
    private UUID participantPaymentId;
    private UUID paymentRequestId;
    private String product;
    private Long personId;
    private Long liquidationBatchId;
    private String participantPaymentStatus;
    private String liquidationStatus;
    private Long grossAmountCop;
    private Long deductionAmountCop;
    private Long netAmountCop;
}
