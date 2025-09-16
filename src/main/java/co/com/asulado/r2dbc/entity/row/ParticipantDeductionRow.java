package co.com.asulado.r2dbc.entity.row;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ParticipantDeductionRow {
    long participantId;
    String participantStatus;
    String productDescription;

    long personId;
    String documentType;
    String documentNumber;
    LocalDate birthDate;
    boolean isForeignResident;
    boolean isPersonActive;

    long incomeId;
    String incomeType;
    String incomeFrequency;
    int numberOfPayments;
    int paymentNumber;
    String participationType;
    double participationPercentage;
    long grossParticipationValue;
    String currency;
    double exchangeRate;
    long baseParticipationValue;

    long deductionId;
    boolean deductionIsAdditional;
    String deductionStatus;
    String deductionType;
    String deductionParticipantType;
    Double deductionPercentage;
    long deductionValue;
    String deductionCurrency;
    Double deductionExchangeRate;
    int deductionNumberOfPayments;
    LocalDate deductionStartDate;
    LocalDate deductionEndDate;

    public boolean hasIncome() {
        return incomeType != null;
    }

    public boolean hasDeduction() {
        return deductionType != null;
    }
}
