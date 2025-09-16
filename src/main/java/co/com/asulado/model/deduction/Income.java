package co.com.asulado.model.deduction;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Income {
    private long incomeId;
    private String type;
    private String frequency;
    private int numberOfPayments;
    private int paymentNumber;
    private String participationType;
    private double participationPercentage;
    private long grossParticipationValue;
    private String currency;
    private double exchangeRate;
    private long baseParticipationValue;
    private double originValue;
    private double localValue;
}