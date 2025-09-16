package co.com.asulado.usecase.liquidation.liquidatebatch;

import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.constants.DeductionsConstants;
import co.com.asulado.model.deduction.Income;
import co.com.asulado.model.deduction.ParticipantDeduction;
import co.com.asulado.model.eventmessages.LiquidateBatchMessage;
import co.com.asulado.model.liquidation.gateways.ParameterRepository;
import co.com.asulado.model.liquidation.parameter.GeneralParameter;
import co.com.asulado.model.liquidation.parameter.LiquidationParameter;
import co.com.asulado.model.payments.gateways.ParticipantProjectionRepository;
import co.com.asulado.usecase.liquidation.deductions.DeductionUseCase;
import co.com.asulado.usecase.liquidation.liquidatebatch.util.gateway.LiquidationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class LiquidateBatchUseCase {

    private final DeductionUseCase deductionUseCase;
    private final ParameterRepository parameterRepository;
    private final ParticipantProjectionRepository participantProjectionRepository;
    private final LiquidationHelper liquidationHelper;

    public Mono<Void> liquidateBatch(LiquidateBatchMessage liquidateBatchMessage, String traceId) {
        return Mono.zip(parameterRepository.getParameters(Constants.LIQUIDATION_PARAMETER_KEY, traceId),
                        parameterRepository.getParameters(Constants.GENERAL_PARAMETER_KEY, traceId))
                .flatMap(parameters ->
                                processDeductions(traceId,liquidateBatchMessage.getBatchId().toString(),
                                        (LiquidationParameter) Objects.requireNonNull(parameters.get(0)),
                                        (GeneralParameter) Objects.requireNonNull(parameters.get(1)))
                        )
                .then();
    }


    private Mono<Void> processDeductions(String traceId,String batchId, LiquidationParameter liquidationParameter,
                                         GeneralParameter generalParameter) {
        return participantProjectionRepository.findAllParticipantsDeductions(batchId)
                        .map(participantDeduction -> {
                            updateIncomeValues(participantDeduction, liquidationParameter.getTaxableBase().getFormula());
                            Income income = participantDeduction.getIncomes()
                                    .stream()
                                    .filter(part -> part
                                            .getType().equalsIgnoreCase(DeductionsConstants.MESADA_PENSIONAL))
                                    .findFirst()
                                    .orElse(null);

                            double taxableBase = (income == null) ? 0 : income.getLocalValue();

                            deductionUseCase.getTotalDeductions(
                                    participantDeduction, liquidationParameter, taxableBase,
                                    generalParameter.getMinimumSalary(),traceId);
                            return participantDeduction;
                        })
                .collectList()
                .flatMap(participantProjectionRepository::updateAllParticipantsDeductions)
                .then();
    }

    private void updateIncomeValues(ParticipantDeduction participantDeductions,
                                    String formula) {
        for (Income income : participantDeductions.getIncomes()) {
            if (income.getParticipationType().equals(DeductionsConstants.TYPE_VALUE)) {
                income.setOriginValue(income.getGrossParticipationValue());
                income.setParticipationPercentage( (double) income.getGrossParticipationValue() /
                                income.getBaseParticipationValue());
            } else if (income.getParticipationType().equals(DeductionsConstants.TYPE_PERCENTAGE)) {
                income.setOriginValue(income.getBaseParticipationValue() * (
                        income.getParticipationPercentage() > 1.0 ?
                                income.getParticipationPercentage() / 100.0 :
                                income.getParticipationPercentage()));
                income.setGrossParticipationValue((long) income.getOriginValue());
            } else {
                income.setOriginValue(0);
            }
            double baseLocalValue = liquidationHelper.evaluateFormula(formula, mapParameters(income));
            income.setLocalValue(baseLocalValue);
            participantDeductions.setGrossValueLocalTotal(
                    participantDeductions.getGrossValueLocalTotal() + (long) baseLocalValue);
        }
    }

    private Map<String, Number> mapParameters(Income income) {
        return Map.of(
                "originValue", income.getOriginValue(),
                "exchangeRate", income.getExchangeRate(),
                "numberOfPayments", income.getNumberOfPayments()
        );
    }
}
