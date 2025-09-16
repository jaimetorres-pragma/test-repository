package co.com.asulado.usecase.liquidation.deductions.calculator;

import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.constants.DeductionsConstants;
import co.com.asulado.model.deduction.Deduction;
import co.com.asulado.model.liquidation.parameter.CompensationFund;
import co.com.asulado.model.liquidation.parameter.LiquidationParameter;
import co.com.asulado.usecase.liquidation.deductions.util.DeductionCalculatorUtils;
import co.com.asulado.usecase.liquidation.deductions.util.DeductionContext;
import co.com.asulado.usecase.liquidation.deductions.util.RoundingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class CCFDeduction implements DeductionRule{

    @Override
    public Mono<Deduction> calculate(
            String traceId,
            DeductionContext deductionContext,
            Deduction deduction,
            LiquidationParameter parameter,
            double minimumSalary
    ) {

        if (Boolean.TRUE.equals(deduction.getIsAdditional())) {
            log.warn(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_DEDUCTION_MARKED_AS_ADDITIONAL, traceId, deduction.getDeductionId());
            return Mono.just(deduction);
        }

        double grossSalaryInLocal = deductionContext.getGrossSalaryInLocalCurrency();

        if (grossSalaryInLocal <= 0) {
            log.warn(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_DEDUCTION_INVALID_GROSS_SALARY, traceId, deduction.getDeductionId(), grossSalaryInLocal);
            return Mono.just(deduction);
        }

        CompensationFund parameterCCF = parameter.getCompensationFund();
        int maxSalaries = (int) parameterCCF.getSmmlvMax();
        int roundValue = (int) parameterCCF.getRules().getRounding();

        if (deduction.getParticipantType().equals(DeductionsConstants.TYPE_PERCENTAGE)) {
            double maxRange = maxSalaries * minimumSalary;

            if (grossSalaryInLocal > maxRange) {
                grossSalaryInLocal = maxRange;
            }
        }

        return DeductionCalculatorUtils.calculateDeduction(
                traceId,
                grossSalaryInLocal,
                deduction,
                RoundingStrategy.UP_TO_NEAREST_MULTIPLE,
                roundValue
        );
    }
}