package co.com.asulado.r2dbc.testdata;

import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.r2dbc.entity.liquidation.LiquidationBatchEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class LiquidationBatchEntityMapperTestData {

    public static final Long BATCH_ID_1 = 1L;
    public static final Long BATCH_ID_2 = 2L;
    public static final Long BATCH_ID_100 = 100L;

    public static final LocalDate LIQUIDATION_DATE_AUG_15 = LocalDate.of(2025, 8, 15);
    public static final LocalDate DISPERSION_DATE_AUG_20 = LocalDate.of(2025, 8, 20);
    public static final LocalDate LIQUIDATION_DATE_AUG_22 = LocalDate.of(2025, 8, 22);
    public static final LocalDate DISPERSION_DATE_AUG_25 = LocalDate.of(2025, 8, 25);
    public static final LocalDate LIQUIDATION_DATE_DEC_15 = LocalDate.of(2025, 12, 15);
    public static final LocalDate DISPERSION_DATE_DEC_20 = LocalDate.of(2025, 12, 20);

    public static final String LIQUIDATION_PERIOD_AUG_2025 = "2025-08";
    public static final String LIQUIDATION_PERIOD_DEC_2025 = "2025-12";
    public static final String STATUS_NUEVO = "NUEVO";
    public static final String STATUS_PROCESADO = "PROCESADO";
    public static final String STATUS_FINALIZADO = "FINALIZADO";

    public static final Integer PARTICIPANT_COUNT_5 = 5;
    public static final Integer PARTICIPANT_COUNT_3 = 3;
    public static final Integer PARTICIPANT_COUNT_15 = 15;
    public static final Integer THIRD_PARTY_COUNT_2 = 2;
    public static final Integer THIRD_PARTY_COUNT_1 = 1;
    public static final Integer THIRD_PARTY_COUNT_5 = 5;

    public static final Long GROSS_AMOUNT_10000 = 10000L;
    public static final Long DEDUCTION_AMOUNT_1000 = 1000L;
    public static final Long NET_AMOUNT_9000 = 9000L;
    public static final Long GROSS_AMOUNT_5000 = 5000L;
    public static final Long DEDUCTION_AMOUNT_500 = 500L;
    public static final Long NET_AMOUNT_4500 = 4500L;
    public static final Long GROSS_AMOUNT_50000 = 50000L;
    public static final Long DEDUCTION_AMOUNT_5000 = 5000L;
    public static final Long NET_AMOUNT_45000 = 45000L;

    public static LiquidationBatch createDomainBatch1() {
        return LiquidationBatch.builder()
                .batchId(BATCH_ID_1)
                .liquidationDate(LIQUIDATION_DATE_AUG_15)
                .dispersionDate(DISPERSION_DATE_AUG_20)
                .liquidationPeriod(LIQUIDATION_PERIOD_AUG_2025)
                .status(STATUS_NUEVO)
                .participantCount(PARTICIPANT_COUNT_5)
                .thirdPartyCount(THIRD_PARTY_COUNT_2)
                .totalGrossAmountCop(GROSS_AMOUNT_10000)
                .totalDeductionAmountCop(DEDUCTION_AMOUNT_1000)
                .totalNetAmountCop(NET_AMOUNT_9000)
                .build();
    }

    public static LiquidationBatchEntity createEntityBatch2() {
        return LiquidationBatchEntity.builder()
                .batchId(BATCH_ID_2)
                .liquidationDate(LIQUIDATION_DATE_AUG_22)
                .dispersionDate(DISPERSION_DATE_AUG_25)
                .liquidationPeriod(LIQUIDATION_PERIOD_AUG_2025)
                .status(STATUS_PROCESADO)
                .participantCount(PARTICIPANT_COUNT_3)
                .thirdPartyCount(THIRD_PARTY_COUNT_1)
                .totalGrossAmountCop(GROSS_AMOUNT_5000)
                .totalDeductionAmountCop(DEDUCTION_AMOUNT_500)
                .totalNetAmountCop(NET_AMOUNT_4500)
                .build();
    }

    public static LiquidationBatch createDomainWithNulls() {
        return LiquidationBatch.builder()
                .batchId(null)
                .liquidationDate(null)
                .dispersionDate(null)
                .liquidationPeriod(null)
                .status(null)
                .participantCount(null)
                .thirdPartyCount(null)
                .totalGrossAmountCop(null)
                .totalDeductionAmountCop(null)
                .totalNetAmountCop(null)
                .build();
    }

    public static LiquidationBatch createOriginalDomainForRoundTrip() {
        return LiquidationBatch.builder()
                .batchId(BATCH_ID_100)
                .liquidationDate(LIQUIDATION_DATE_DEC_15)
                .dispersionDate(DISPERSION_DATE_DEC_20)
                .liquidationPeriod(LIQUIDATION_PERIOD_DEC_2025)
                .status(STATUS_FINALIZADO)
                .participantCount(PARTICIPANT_COUNT_15)
                .thirdPartyCount(THIRD_PARTY_COUNT_5)
                .totalGrossAmountCop(GROSS_AMOUNT_50000)
                .totalDeductionAmountCop(DEDUCTION_AMOUNT_5000)
                .totalNetAmountCop(NET_AMOUNT_45000)
                .build();
    }
}
