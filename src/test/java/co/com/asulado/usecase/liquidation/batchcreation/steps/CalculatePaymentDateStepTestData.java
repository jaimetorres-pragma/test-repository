package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.model.payments.ParticipantProjection;
import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class CalculatePaymentDateStepTestData {

    public static final LocalDate CURRENT_DATE_AUG_27 = LocalDate.of(2025, 8, 27);
    public static final LocalDate CALCULATED_DATE_AUG_20 = LocalDate.of(2025, 8, 20);
    public static final LocalDate EXISTING_DATE_AUG_15 = LocalDate.of(2025, 8, 15);
    public static final LocalDate EXISTING_DATE_AUG_25 = LocalDate.of(2025, 8, 25);

    public static final String PARTICIPANT_ID_1 = "participant-1";
    public static final String PARTICIPANT_ID_2 = "participant-2";
    public static final String FREQUENCY_20_EACH_MONTH = "20CADAMES";
    public static final String FREQUENCY_15_EACH_MONTH = "15CADAMES";

    public static final int PARTICIPANTS_COUNT_2 = 2;
    public static final int PARTICIPANTS_COUNT_0 = 0;

    public static ParticipantProjection createParticipantWithNullDate() {
        return ParticipantProjection.builder()
            .participantId(PARTICIPANT_ID_1)
            .paymentDate(null)
            .productFrequency(FREQUENCY_20_EACH_MONTH)
            .build();
    }

    public static ParticipantProjection createParticipantWithExistingDate() {
        return ParticipantProjection.builder()
            .participantId(PARTICIPANT_ID_2)
            .paymentDate(EXISTING_DATE_AUG_15)
            .productFrequency(FREQUENCY_15_EACH_MONTH)
            .build();
    }

    public static ParticipantProjection createParticipantWithSpecificDate(LocalDate paymentDate) {
        return ParticipantProjection.builder()
            .participantId(PARTICIPANT_ID_1)
            .paymentDate(paymentDate)
            .productFrequency(FREQUENCY_20_EACH_MONTH)
            .build();
    }

    public static ProcessingContext createContextWithParticipants(List<ParticipantProjection> participants) {
        return ProcessingContext.builder()
            .currentDate(CURRENT_DATE_AUG_27)
            .participants(participants)
            .build();
    }

    public static ProcessingContext createEmptyContext() {
        return ProcessingContext.builder()
            .currentDate(CURRENT_DATE_AUG_27)
            .participants(List.of())
            .build();
    }
}
