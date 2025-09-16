package co.com.asulado.usecase.liquidation.deductions.util;

import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.constants.DeductionsConstants;
import co.com.asulado.model.deduction.Deduction;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@UtilityClass
@Slf4j
public class DeductionCalculatorUtils {

    public static Mono<Deduction> calculateDeduction(
            String traceId,
            double valueInLocalCurrency,
            Deduction deduction,
            RoundingStrategy roundingStrategy,
            Integer roundValue
    ) {
        double totalInLocal;
        double percentage;

        int dayToCalculate = PayrollDateUtils.calculateDaysToPayInMonth(deduction.getStartDate(), deduction.getEndDate());

        boolean invalid = isInvalidDeduction(traceId, deduction, valueInLocalCurrency, dayToCalculate);
        if (invalid) {
            return Mono.just(deduction);
        }

        if (deduction.getParticipantType().equals(DeductionsConstants.TYPE_VALUE)) {
            totalInLocal = deduction.getValue() * deduction.getExchangeRate();
            percentage = totalInLocal / valueInLocalCurrency;
        } else {
            percentage = deduction.getPercentage();
            totalInLocal = valueInLocalCurrency * percentage;
        }

        totalInLocal = totalInLocal * dayToCalculate / DeductionsConstants.DAYS_IN_MONTH;

        long roundedTotalInLocal = roundingStrategy.apply(totalInLocal, roundValue);

        double valueLocalPerPayment = (double) roundedTotalInLocal / deduction.getNumberOfPayments();
        double valueOriginTotal = roundedTotalInLocal / deduction.getExchangeRate();
        double valueOriginPerPayment = valueOriginTotal / deduction.getNumberOfPayments();

        return Mono.just(
                deduction.toBuilder()
                    .percentage(percentage)
                    .value(RoundingStrategy.HALF_UP.apply(valueOriginTotal , 0))
                    .originValue((RoundingStrategy.HALF_UP.apply(valueOriginPerPayment , 0)))
                    .localValue((RoundingStrategy.HALF_UP.apply(valueLocalPerPayment , 0)))
                    .build()
        );
    }

    private static boolean isInvalidDeduction(String traceId, Deduction deduction, double valueInLocalCurrency, int dayToCalculate) {
        if (dayToCalculate == 0) {
            log.warn(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_DEDUCTION_DAYS_TO_CALCULATE_ZERO, traceId, deduction.getDeductionId());
            return true;
        }
        if (deduction.getNumberOfPayments() == null || deduction.getNumberOfPayments() <= 0) {
            log.warn(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_DEDUCTION_INVALID_PAYMENT_COUNT, traceId, deduction.getDeductionId(), deduction.getNumberOfPayments());
            return true;
        }
        if (deduction.getExchangeRate() == null || deduction.getExchangeRate() <= 0) {
            log.warn(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_DEDUCTION_INVALID_TRM, traceId, deduction.getDeductionId(), deduction.getExchangeRate());
            return true;
        }
        if (valueInLocalCurrency <= 0) {
            log.warn(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_DEDUCTION_INVALID_LOCAL_VALUE, traceId, deduction.getDeductionId(), valueInLocalCurrency);
            return true;
        }
        if (deduction.getParticipantType().equals(DeductionsConstants.TYPE_VALUE)) {
            if (deduction.getValue() == null || deduction.getValue() <= 0) {
                log.warn(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_DEDUCTION_INVALID_VALUE, traceId, deduction.getDeductionId(), deduction.getValue());
                return true;
            }
        } else if (deduction.getParticipantType().equals(DeductionsConstants.TYPE_PERCENTAGE) &&
                (deduction.getPercentage() == null || deduction.getPercentage() <= 0 || deduction.getPercentage() >= 1)) {
                log.warn(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_DEDUCTION_INVALID_PERCENTAGE, traceId, deduction.getDeductionId(), deduction.getPercentage());
                return true;
        }

        return false;
    }
}
