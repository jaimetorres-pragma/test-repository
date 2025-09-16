package co.com.asulado.model.liquidation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ThirdParty {
    private Long thirdPartyId;
    private Long personId;
    private Long liquidationBatchId;
    private String paymentType;
    private String thirdPartyStatus;
    private String liquidationStatus;
    private Long grossAmountCop;
    private Long deductionAmountCop;
    private Long netAmountCop;
}

