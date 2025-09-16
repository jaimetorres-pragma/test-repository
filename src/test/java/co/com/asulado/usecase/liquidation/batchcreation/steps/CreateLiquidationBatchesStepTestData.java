package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;
import co.com.asulado.usecase.liquidation.testdata.UseCaseTestData;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.Map;

@UtilityClass
public class CreateLiquidationBatchesStepTestData {

    public static final LocalDate LIQUIDATION_DATE_AUG_17 = LocalDate.of(2025, 8, 17);
    public static final LocalDate LIQUIDATION_DATE_AUG_22 = LocalDate.of(2025, 8, 22);
    public static final LocalDate PAYMENT_DATE_AUG_20 = LocalDate.of(2025, 8, 20);
    public static final LocalDate PAYMENT_DATE_AUG_25 = LocalDate.of(2025, 8, 25);

    public static final Long BATCH_ID_100 = 100L;
    public static final Long BATCH_ID_101 = 101L;

    public static final int BATCHES_COUNT_2 = 2;
    public static final int BATCHES_COUNT_0 = 0;
    public static final int PARTICIPANTS_COUNT_2 = 2;
    public static final int PARTICIPANTS_COUNT_1 = 1;

    public static final String STATUS_NEW = "NUEVO";
    public static final String STATUS_ACTIVE = "ACTIVO";
    public static final String PRODUCT_NAME_PENSION = "PENSION_PRODUCT";

    public static final Long AMOUNT_ZERO = 0L;
    public static final Long PERSON_ID_1 = 1L;
    public static final Long PERSON_ID_2 = 2L;

    public static ProcessingContext createProcessingContextWithGroupedData() {
        return UseCaseTestData.createInitialProcessingContext().toBuilder()
                .currentDate(LIQUIDATION_DATE_AUG_17)
                .groupedByPaymentDate(UseCaseTestData.createGroupedParticipantsByPaymentDate())
                .liquidationDates(UseCaseTestData.createLiquidationDatesMap())
                .build();
    }

    public static ProcessingContext createEmptyGroupedContext() {
        return UseCaseTestData.createInitialProcessingContext().toBuilder()
                .groupedByPaymentDate(Map.of())
                .liquidationDates(Map.of())
                .build();
    }

    public static LiquidationBatch createExpectedBatch1() {
        return UseCaseTestData.createLiquidationBatch(BATCH_ID_100, LIQUIDATION_DATE_AUG_17, PAYMENT_DATE_AUG_20, PARTICIPANTS_COUNT_2);
    }

    public static LiquidationBatch createExpectedBatch2() {
        return UseCaseTestData.createLiquidationBatch(BATCH_ID_101, LIQUIDATION_DATE_AUG_22, PAYMENT_DATE_AUG_25, PARTICIPANTS_COUNT_1);
    }

    public static RuntimeException createDatabaseException() {
        return UseCaseTestData.createDatabaseException();
    }
}
