package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;
import co.com.asulado.usecase.liquidation.testdata.TestData;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.Map;

@UtilityClass
public class CalculateAndValidateLiquidationDatesStepTestData {

    public static final LocalDate PAYMENT_DATE_AUG_20 = LocalDate.of(2025, 8, 20);
    public static final LocalDate PAYMENT_DATE_AUG_25 = LocalDate.of(2025, 8, 25);
    public static final LocalDate ADJUSTED_LIQUIDATION_DATE_AUG_15 = LocalDate.of(2025, 8, 15);

    public static final int LIQUIDATION_DATES_COUNT_2 = 2;
    public static final int LIQUIDATION_DATES_COUNT_0 = 0;

    public static ProcessingContext createContextWithEmptyGroupedParticipants() {
        return TestData.createDefaultProcessingContext().toBuilder()
                .groupedByPaymentDate(Map.of())
                .holidays(TestData.createHolidaysList())
                .build();
    }

    public static LocalDate calculateExpectedLiquidationDate(LocalDate paymentDate, Integer sla) {
        return paymentDate.minusDays(sla);
    }
}
