package co.com.asulado.usecase.liquidation.deductions.calculator;

import co.com.asulado.model.deduction.Deduction;
import co.com.asulado.model.liquidation.parameter.LiquidationParameter;
import co.com.asulado.usecase.liquidation.deductions.util.DeductionContext;
import reactor.core.publisher.Mono;

public interface DeductionRule {
    Mono<Deduction> calculate(
            String traceId,
            DeductionContext deductionContext,
            Deduction deduction,
            LiquidationParameter parameter,
            double minimumSalary
    );
}