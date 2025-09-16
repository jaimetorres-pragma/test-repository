package co.com.asulado.usecase.liquidation.testdata;

import co.com.asulado.model.deduction.Deduction;
import co.com.asulado.model.liquidation.parameter.CompensationFund;
import co.com.asulado.model.liquidation.parameter.CompensationRules;
import co.com.asulado.model.liquidation.parameter.LiquidationParameter;
import co.com.asulado.usecase.liquidation.deductions.util.DeductionContext;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.YearMonth;

@UtilityClass
public class CCFDeductionTestData {
    public static final double MINIMUM_SALARY = 1423500.0;
    public static final String TRACE_ID = "123";

    public static DeductionContext createDeductionContext(Double grossSalary) {
        return DeductionContext.builder()
                .grossSalaryInLocalCurrency(grossSalary)
                .build();
    }

    public static Deduction createDeductionPercentage(
        double percentage,
        int numberOfPayments,
        LocalDate startDate,
        LocalDate endDate
        ) {

        return Deduction.builder()
                .deductionId(1L)
                .type("CCF")
                .participantType("PORCENTAJE")
                .percentage(percentage)
                .value(0L)
                .originValue(0L)
                .localValue(0L)
                .exchangeRate(1.0)
                .numberOfPayments(numberOfPayments)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public static Deduction createDeduction() {
        YearMonth evaluationMonth = YearMonth.from(LocalDate.now());

        return Deduction.builder()
                .deductionId(1L)
                .type("CCF")
                .participantType("PORCENTAJE")
                .percentage(0.02)
                .value(0L)
                .originValue(0L)
                .localValue(0L)
                .exchangeRate(1.0)
                .numberOfPayments(1)
                .startDate(evaluationMonth.atDay(1))
                .endDate(evaluationMonth.atEndOfMonth())
                .build();
    }

    public static Deduction createDeductionValue(
            long value,
            int numberOfPayments,
            LocalDate startDate,
            LocalDate endDate
    ) {

        return Deduction.builder()
                .deductionId(1L)
                .type("CCF")
                .participantType("VALOR")
                .value(value)
                .originValue(0L)
                .localValue(0L)
                .exchangeRate(1.0)
                .numberOfPayments(numberOfPayments)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public static LiquidationParameter createLiquidationParameter() {
        return LiquidationParameter.builder()
                .compensationFund(
                        CompensationFund.builder()
                        .smmlvMax(25)
                        .rules(CompensationRules.builder().rounding(100).build())
                        .build())
                .build();
    }
}
