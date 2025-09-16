package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;
import co.com.asulado.usecase.liquidation.testdata.TestData;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class GroupByPaymentDateStepTestData {

    public static final LocalDate PAYMENT_DATE_AUG_15 = LocalDate.of(2025, 8, 15);
    public static final LocalDate PAYMENT_DATE_AUG_20 = LocalDate.of(2025, 8, 20);
    public static final LocalDate PAYMENT_DATE_AUG_25 = LocalDate.of(2025, 8, 25);

    public static final String PARTICIPANT_ID_1 = "participant-1";
    public static final String PARTICIPANT_ID_2 = "participant-2";
    public static final String PARTICIPANT_ID_3 = "participant-3";
    public static final String PARTICIPANT_ID_4 = "participant-4";

    public static final String FREQUENCY_15_EACH_MONTH = "15CADAMES";
    public static final String FREQUENCY_20_EACH_MONTH = "20CADAMES";
    public static final String FREQUENCY_25_EACH_MONTH = "25CADAMES";

    public static final int GROUPS_COUNT_3 = 3;
    public static final int GROUPS_COUNT_0 = 0;
    public static final int GROUP_SIZE_1 = 1;
    public static final int GROUP_SIZE_2 = 2;

    public static ProcessingContext createContextWithEmptyParticipants() {
        return TestData.createDefaultProcessingContext().toBuilder()
                .participants(List.of())
                .build();
    }

    public static ProcessingContext createContextWithMultiplePaymentDates() {
        return ProcessingContext.builder()
                .participants(List.of(
                    TestData.createParticipantProjection(PARTICIPANT_ID_1, PAYMENT_DATE_AUG_15, FREQUENCY_15_EACH_MONTH),
                    TestData.createParticipantProjection(PARTICIPANT_ID_2, PAYMENT_DATE_AUG_20, FREQUENCY_20_EACH_MONTH),
                    TestData.createParticipantProjection(PARTICIPANT_ID_3, PAYMENT_DATE_AUG_25, FREQUENCY_25_EACH_MONTH),
                    TestData.createParticipantProjection(PARTICIPANT_ID_4, PAYMENT_DATE_AUG_20, FREQUENCY_20_EACH_MONTH)
                ))
                .build();
    }
}
