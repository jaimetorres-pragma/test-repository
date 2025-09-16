package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;
import co.com.asulado.usecase.liquidation.testdata.TestData;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class ValidatePaymentDatesAgainstHolidaysStepTestData {

    public static final LocalDate HOLIDAY_DATE_AUG_15 = LocalDate.of(2025, 8, 15);
    public static final LocalDate ADJUSTED_DATE_AUG_15 = LocalDate.of(2025, 8, 15);
    public static final String PARTICIPANT_ID_1 = "participant-1";
    public static final String FREQUENCY_15_EACH_MONTH = "15CADAMES";

    public static final int PARTICIPANTS_COUNT_0 = 0;
    public static final int PARTICIPANTS_COUNT_1 = 1;

    public static ProcessingContext createContextWithHolidayParticipant() {
        return ProcessingContext.builder()
                .participants(List.of(
                    TestData.createParticipantProjection(PARTICIPANT_ID_1, HOLIDAY_DATE_AUG_15, FREQUENCY_15_EACH_MONTH)
                ))
                .holidays(TestData.createHolidaysList())
                .build();
    }

    public static ProcessingContext createContextWithEmptyParticipants() {
        return TestData.createDefaultProcessingContext().toBuilder()
                .participants(List.of())
                .holidays(TestData.createHolidaysList())
                .build();
    }

    public static ProcessingContext createContextWithParticipantsWithPaymentDates() {
        return TestData.createProcessingContextWithParticipants().toBuilder()
                .holidays(TestData.createHolidaysList())
                .participants(TestData.createParticipantProjectionsWithPaymentDatesList())
                .build();
    }
}
