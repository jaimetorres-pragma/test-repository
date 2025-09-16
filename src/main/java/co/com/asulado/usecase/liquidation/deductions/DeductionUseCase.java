package co.com.asulado.usecase.liquidation.deductions;

import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.constants.DeductionsConstants;
import co.com.asulado.model.deduction.Deduction;
import co.com.asulado.model.deduction.ParticipantDeduction;
import co.com.asulado.model.deduction.gateways.DeductionRepository;
import co.com.asulado.model.liquidation.parameter.LiquidationParameter;
import co.com.asulado.usecase.liquidation.deductions.calculator.DeductionRule;
import co.com.asulado.usecase.liquidation.deductions.util.DeductionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class DeductionUseCase {

    private final Map<String, DeductionRule> deductionProcessors;
    private final DeductionRepository deductionRepository;

    public DeductionUseCase(
            @Qualifier(DeductionsConstants.BUSINESS_DEDUCTION_PROCESSORS) Map<String, DeductionRule> deductionProcessors, DeductionRepository deductionRepository
    ) {
        this.deductionProcessors = deductionProcessors;
        this.deductionRepository = deductionRepository;
    }

    public Mono<Long> getTotalDeductions(ParticipantDeduction participantDeduction,
                        LiquidationParameter liquidationParameter,
                        double taxableBase,
                        double minimumSalary,
                        String traceId
    ) {

        DeductionContext deductionContext = DeductionContext.builder()
                .grossSalaryInLocalCurrency(taxableBase)
                .build();

    return Flux.fromIterable(participantDeduction.getDeductions())
        .flatMap(deduction -> {
            DeductionRule rule = deductionProcessors.get(deduction.getType());
            Mono<Deduction> calculated = rule != null
                ? rule.calculate(traceId, deductionContext, deduction, liquidationParameter, minimumSalary)
                : Mono.just(deduction);

            return calculated.onErrorResume(e -> {
                log.error(Constants.TRACE_ID_PLACEHOLDER + Constants.LOG_DEDUCTION_UNEXPECTED_ERROR, traceId, deduction.getDeductionId(), e.toString());
                return Mono.just(deduction);
            });
        })
        .collectList()
        .flatMapMany(deductionRepository::saveAll)
        .map(Deduction::getLocalValue)
        .reduce(0L, Long::sum);
    }
}
