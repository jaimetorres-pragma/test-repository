package co.com.asulado.config;

import co.com.asulado.usecase.liquidation.batchclosing.steps.ComputeBatchTotalsStep;
import co.com.asulado.usecase.liquidation.batchclosing.steps.ComputeParticipantTotalsStep;
import co.com.asulado.usecase.liquidation.batchclosing.steps.ComputeThirdPartyTotalsStep;
import co.com.asulado.usecase.liquidation.batchclosing.steps.FindEligibleBatchesStep;
import co.com.asulado.usecase.liquidation.batchclosing.steps.SendClosingEventStep;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ClosingStepsConfig {

    @Bean("orderedClosingSteps")
    public List<ClosingProcessingStep> closingSteps(
        FindEligibleBatchesStep findEligibleBatchesStep,
        ComputeParticipantTotalsStep computeParticipantTotalsStep,
        ComputeThirdPartyTotalsStep computeThirdPartyTotalsStep,
        ComputeBatchTotalsStep computeBatchTotalsStep,
        SendClosingEventStep sendClosingEventStep
    ) {
        return List.of(
            findEligibleBatchesStep,
            computeParticipantTotalsStep,
            computeThirdPartyTotalsStep,
            computeBatchTotalsStep,
            sendClosingEventStep
        );
    }
}
