package co.com.asulado.r2dbc.testdata;

import co.com.asulado.model.constants.LiquidationConstants;
import co.com.asulado.model.liquidation.Participant;
import co.com.asulado.r2dbc.entity.liquidation.ParticipantEntity;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ParticipantEntityMapperTestData {

    public static final Long PARTICIPANT_ID_1 = 1L;
    public static final Long PARTICIPANT_ID_2 = 2L;
    public static final Long PARTICIPANT_ID_100 = 100L;
    public static final Long LIQUIDATION_BATCH_ID_100 = 100L;
    public static final Long LIQUIDATION_BATCH_ID_200 = 200L;
    public static final Long LIQUIDATION_BATCH_ID_300 = 300L;

    public static final Long GROSS_AMOUNT_5000 = 5000L;
    public static final Long DEDUCTION_AMOUNT_500 = 500L;
    public static final Long NET_AMOUNT_4500 = 4500L;

    public static final Long GROSS_AMOUNT_8000 = 8000L;
    public static final Long DEDUCTION_AMOUNT_800 = 800L;
    public static final Long NET_AMOUNT_7200 = 7200L;

    public static final Long GROSS_AMOUNT_15000 = 15000L;
    public static final Long DEDUCTION_AMOUNT_1500 = 1500L;
    public static final Long NET_AMOUNT_13500 = 13500L;

    public static final Long AMOUNT_ZERO = 0L;

    public static final String STATUS_ACTIVE = LiquidationConstants.STATUS_ACTIVE;
    public static final String STATUS_LIQUIDATED = LiquidationConstants.STATUS_LIQUIDATED;
    public static final String STATUS_PROCESSED = "PROCESSED";

    public static Participant createDomainParticipant() {
        return Participant.builder()
                .participantId(PARTICIPANT_ID_1)
                .participantPaymentId(UUID.randomUUID())
                .liquidationBatchId(LIQUIDATION_BATCH_ID_100)
                .participantPaymentStatus(STATUS_ACTIVE)
                .grossAmountCop(GROSS_AMOUNT_5000)
                .deductionAmountCop(DEDUCTION_AMOUNT_500)
                .netAmountCop(NET_AMOUNT_4500)
                .build();
    }

    public static ParticipantEntity createEntityParticipant() {
        return ParticipantEntity.builder()
                .participantId(PARTICIPANT_ID_2)
                .participantPaymentId(UUID.randomUUID())
                .liquidationBatchId(LIQUIDATION_BATCH_ID_200)
                .participantPaymentStatus(STATUS_LIQUIDATED)
                .grossAmountCop(GROSS_AMOUNT_8000)
                .deductionAmountCop(DEDUCTION_AMOUNT_800)
                .netAmountCop(NET_AMOUNT_7200)
                .build();
    }

    public static Participant createOriginalDomain() {
        return Participant.builder()
                .participantId(PARTICIPANT_ID_100)
                .participantPaymentId(UUID.randomUUID())
                .liquidationBatchId(LIQUIDATION_BATCH_ID_300)
                .participantPaymentStatus(STATUS_PROCESSED)
                .grossAmountCop(GROSS_AMOUNT_15000)
                .deductionAmountCop(DEDUCTION_AMOUNT_1500)
                .netAmountCop(NET_AMOUNT_13500)
                .build();
    }

    public static Participant createDomainWithNulls() {
        return Participant.builder()
                .participantId(null)
                .participantPaymentId(null)
                .liquidationBatchId(null)
                .participantPaymentStatus(null)
                .grossAmountCop(null)
                .deductionAmountCop(null)
                .netAmountCop(null)
                .build();
    }

    public static Participant createDomainWithZeros() {
        return Participant.builder()
                .participantId(PARTICIPANT_ID_1)
                .participantPaymentId(UUID.randomUUID())
                .liquidationBatchId(LIQUIDATION_BATCH_ID_100)
                .participantPaymentStatus(STATUS_ACTIVE)
                .grossAmountCop(AMOUNT_ZERO)
                .deductionAmountCop(AMOUNT_ZERO)
                .netAmountCop(AMOUNT_ZERO)
                .build();
    }
}
