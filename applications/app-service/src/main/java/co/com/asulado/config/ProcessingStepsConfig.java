package co.com.asulado.config;

import co.com.asulado.usecase.liquidation.batchcreation.steps.CalculateAndValidateLiquidationDatesStep;
import co.com.asulado.usecase.liquidation.batchcreation.steps.CalculatePaymentDateStep;
import co.com.asulado.usecase.liquidation.batchcreation.steps.CreateLiquidationBatchesStep;
import co.com.asulado.usecase.liquidation.batchcreation.steps.FetchCalendarStep;
import co.com.asulado.usecase.liquidation.batchcreation.steps.FetchParticipantsStep;
import co.com.asulado.usecase.liquidation.batchcreation.steps.FilterGroupsByLiquidationDateStep;
import co.com.asulado.usecase.liquidation.batchcreation.steps.GroupByPaymentDateStep;
import co.com.asulado.usecase.liquidation.batchcreation.steps.UpdateParticipantCountsStep;
import co.com.asulado.usecase.liquidation.batchcreation.steps.ValidatePaymentDatesAgainstHolidaysStep;
import co.com.asulado.usecase.liquidation.batchcreation.util.ParticipantProcessingStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ProcessingStepsConfig {

    @Bean("orderedProcessingSteps")
    public List<ParticipantProcessingStep> processingSteps(
            FetchCalendarStep fetchCalendarStep,
            FetchParticipantsStep fetchParticipantsStep,
            CalculatePaymentDateStep calculatePaymentDateStep,
            ValidatePaymentDatesAgainstHolidaysStep validatePaymentDatesStep,
            GroupByPaymentDateStep groupByPaymentDateStep,
            CalculateAndValidateLiquidationDatesStep calculateLiquidationDatesStep,
            FilterGroupsByLiquidationDateStep filterGroupsByLiquidationDateStep,
            CreateLiquidationBatchesStep createLiquidationBatchesStep,
            UpdateParticipantCountsStep updateParticipantCountsStep) {

        return List.of(
            fetchCalendarStep,
            fetchParticipantsStep,
            calculatePaymentDateStep,
            validatePaymentDatesStep,
            groupByPaymentDateStep,
            calculateLiquidationDatesStep,
            filterGroupsByLiquidationDateStep,
            createLiquidationBatchesStep,
            updateParticipantCountsStep
        );
    }
}
