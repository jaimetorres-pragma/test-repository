package co.com.asulado.usecase.liquidation.testdata;

import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.model.liquidation.Participant;
import co.com.asulado.model.liquidation.ThirdParty;
import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingContext;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class ClosingUseCaseTestData {

    public static final LocalDate CURRENT_DATE = LocalDate.of(2025, 8, 27);

    public static final long BATCH_ID_1 = 1L;
    public static final long BATCH_ID_2 = 2L;

    public static final int P_COUNT_B1 = 3;
    public static final int P_COUNT_B2 = 2;

    public static final long P_GROSS_B1 = 1_000L;
    public static final long P_DED_B1 = 200L;
    public static final long P_NET_B1 = 800L;

    public static final long P_GROSS_B2 = 500L;
    public static final long P_DED_B2 = 50L;
    public static final long P_NET_B2 = 450L;

    public static final int T_COUNT_B1 = 1;
    public static final int T_COUNT_B2 = 4;

    public static final long T_GROSS_B1 = 700L;
    public static final long T_DED_B1 = 100L;
    public static final long T_NET_B1 = 600L;

    public static final long T_GROSS_B2 = 900L;
    public static final long T_DED_B2 = 300L;
    public static final long T_NET_B2 = 600L;

    public static List<LiquidationBatch> createEligibleBatches() {
        return List.of(
                LiquidationBatch.builder().batchId(BATCH_ID_1).liquidationDate(CURRENT_DATE).build(),
                LiquidationBatch.builder().batchId(BATCH_ID_2).liquidationDate(CURRENT_DATE.plusDays(1)).build()
        );
    }

    public static List<Participant> createParticipantsForBatch1() {
        return List.of(
                Participant.builder().grossAmountCop(400L).deductionAmountCop(100L).netAmountCop(300L).build(),
                Participant.builder().grossAmountCop(300L).deductionAmountCop(50L).netAmountCop(250L).build(),
                Participant.builder().grossAmountCop(300L).deductionAmountCop(50L).netAmountCop(250L).build()
        );
    }

    public static List<Participant> createParticipantsForBatch2() {
        return List.of(
                Participant.builder().grossAmountCop(200L).deductionAmountCop(20L).netAmountCop(180L).build(),
                Participant.builder().grossAmountCop(300L).deductionAmountCop(30L).netAmountCop(270L).build()
        );
    }

    public static List<ThirdParty> createThirdPartiesForBatch1() {
        return List.of(
                ThirdParty.builder().grossAmountCop(700L).deductionAmountCop(100L).netAmountCop(600L).build()
        );
    }

    public static List<ThirdParty> createThirdPartiesForBatch2() {
        return List.of(
                ThirdParty.builder().grossAmountCop(300L).deductionAmountCop(100L).netAmountCop(200L).build(),
                ThirdParty.builder().grossAmountCop(600L).deductionAmountCop(200L).netAmountCop(400L).build(),
                ThirdParty.builder().grossAmountCop(0L).deductionAmountCop(0L).netAmountCop(0L).build(),
                ThirdParty.builder().grossAmountCop(0L).deductionAmountCop(0L).netAmountCop(0L).build()
        );
    }

    public static ClosingProcessingContext createContextWithEligibleBatches() {
        return ClosingProcessingContext.builder()
                .currentDate(CURRENT_DATE)
                .eligibleBatches(createEligibleBatches())
                .build();
    }

    public static ClosingProcessingContext createContextWithSummaries() {
        return ClosingProcessingContext.builder()
                .currentDate(CURRENT_DATE)
                .eligibleBatches(createEligibleBatches())
                .summaries(List.of(
                        ClosingProcessingContext.BatchSummary.builder()
                                .batchId(BATCH_ID_1)
                                .participantGrossTotal(P_GROSS_B1)
                                .participantDeductionsTotal(P_DED_B1)
                                .participantNetTotal(P_NET_B1)
                                .thirdPartyGrossTotal(T_GROSS_B1)
                                .thirdPartyDeductionsTotal(T_DED_B1)
                                .thirdPartyNetTotal(T_NET_B1)
                                .participantCount(P_COUNT_B1)
                                .thirdPartyCount(T_COUNT_B1)
                                .build(),
                        ClosingProcessingContext.BatchSummary.builder()
                                .batchId(BATCH_ID_2)
                                .participantGrossTotal(P_GROSS_B2)
                                .participantDeductionsTotal(P_DED_B2)
                                .participantNetTotal(P_NET_B2)
                                .thirdPartyGrossTotal(T_GROSS_B2)
                                .thirdPartyDeductionsTotal(T_DED_B2)
                                .thirdPartyNetTotal(T_NET_B2)
                                .participantCount(P_COUNT_B2)
                                .thirdPartyCount(T_COUNT_B2)
                                .build()
                ))
                .totalParticipantsProcessed(P_COUNT_B1 + P_COUNT_B2)
                .totalThirdPartiesProcessed(T_COUNT_B1 + T_COUNT_B2)
                .build();
    }
}

