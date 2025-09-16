package co.com.asulado.usecase.liquidation.testdata;

import co.com.asulado.model.constants.LiquidationConstants;
import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.model.liquidation.params.BatchCreationModel;
import co.com.asulado.model.payments.ParticipantProjection;
import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@UtilityClass
public class UseCaseTestData {

    public static final Integer SLA_THREE_DAYS = 3;
    public static final Integer DEFAULT_MONTH = 8;
    public static final Integer DEFAULT_YEAR = 2025;
    public static final String CALENDAR_TYPE_HOLIDAYS_WEEKENDS = "Festivos y fines de semana";
    public static final LocalDate CURRENT_DATE_AUG_27 = LocalDate.of(2025, 8, 27);

    public static final LocalDate PAYMENT_DATE_AUG_5 = LocalDate.of(2025, 8, 5);
    public static final LocalDate PAYMENT_DATE_AUG_15 = LocalDate.of(2025, 8, 15);
    public static final LocalDate PAYMENT_DATE_AUG_20 = LocalDate.of(2025, 8, 20);
    public static final LocalDate PAYMENT_DATE_AUG_25 = LocalDate.of(2025, 8, 25);
    public static final LocalDate PAYMENT_DATE_AUG_30 = LocalDate.of(2025, 8, 30);
   public static final LocalDate LIQUIDATION_DATE_AUG_17 = LocalDate.of(2025, 8, 17);
    public static final LocalDate LIQUIDATION_DATE_AUG_22 = LocalDate.of(2025, 8, 22);
    public static final LocalDate LIQUIDATION_DATE_AUG_27 = LocalDate.of(2025, 8, 27);

    public static final String SUCCESS_MESSAGE = "Participantes procesados exitosamente para creacion de lotes";
    public static final String FAILURE_MESSAGE_PREFIX = "Processing failed: ";
    public static final String STEP_FAILED_MESSAGE = "Step failed";
    public static final String DATABASE_ERROR_MESSAGE = "Database error";
    public static final String CONNECTION_ERROR_MESSAGE = "Database connection error";

    public static final String FREQUENCY_20_EACH_MONTH = "20CADAMES";
    public static final String FREQUENCY_15_EACH_MONTH = "15CADAMES";
    public static final String FREQUENCY_25_EACH_MONTH = "25CADAMES";
    public static final String FREQUENCY_05_EACH_MONTH = "05CADAMES";

    public static final String PARTICIPANT_UUID_1 = "550e8400-e29b-41d4-a716-446655440001";
    public static final String PARTICIPANT_UUID_2 = "550e8400-e29b-41d4-a716-446655440002";
    public static final String PARTICIPANT_UUID_3 = "550e8400-e29b-41d4-a716-446655440003";
    public static final String PARTICIPANT_UUID_4 = "550e8400-e29b-41d4-a716-446655440004";

    public static final String PAYMENT_REQUEST_UUID_1 = "550e8400-e29b-41d4-a716-446655440034";
    public static final String PAYMENT_REQUEST_UUID_2 = "550e8400-e29b-41d4-a716-446655440065";
    public static final String PAYMENT_REQUEST_UUID_3 = "550e8400-e29b-41d4-a716-446655440023";
    public static final String PAYMENT_REQUEST_UUID_4 = "550e8400-e29b-41d4-a716-446655440062";

    public static final Long PERSON_ID_1 = 1001L;
    public static final Long PERSON_ID_2 = 1002L;
    public static final Long PERSON_ID_3 = 1003L;
    public static final Long PERSON_ID_4 = 1004L;

    public static final String PRODUCT_PENSION = "PENSION";
    public static final String PRODUCT_AUXILIO = "AUXILIO";
    public static final String PRODUCT_PRIMA = "PRIMA";

    public static final Long TOTAL_PARTICIPANTS_10 = 10L;
    public static final Long TOTAL_PARTICIPANTS_0 = 0L;

    public static final Integer TOTAL_BATCHES_2 = 2;
    public static final Integer TOTAL_BATCHES_0 = 0;

    public static final Integer TOTAL_HOLIDAYS_5 = 5;
    public static final Integer TOTAL_HOLIDAYS_0 = 0;


    public static BatchCreationModel createDefaultBatchCreationModel() {
        return BatchCreationModel.builder()
                .sla(SLA_THREE_DAYS)
                .month(DEFAULT_MONTH)
                .year(DEFAULT_YEAR)
                .calendarType(CALENDAR_TYPE_HOLIDAYS_WEEKENDS)
                .currentDate(CURRENT_DATE_AUG_27)
                .build();
    }

    public static ProcessingContext createInitialProcessingContext() {
        return ProcessingContext.builder()
                .sla(SLA_THREE_DAYS)
                .month(DEFAULT_MONTH)
                .year(DEFAULT_YEAR)
                .calendarType(CALENDAR_TYPE_HOLIDAYS_WEEKENDS)
                .currentDate(CURRENT_DATE_AUG_27)
                .build();
    }

    public static ProcessingContext createProcessingContextWithData() {
        return ProcessingContext.builder()
                .sla(SLA_THREE_DAYS)
                .month(DEFAULT_MONTH)
                .year(DEFAULT_YEAR)
                .calendarType(CALENDAR_TYPE_HOLIDAYS_WEEKENDS)
                .currentDate(LocalDate.now())
                .participants(List.of())
                .holidays(List.of())
                .createdBatches(List.of())
                .build();
    }

    public static ProcessingContext createFinalProcessingContext() {
        return createInitialProcessingContext().toBuilder()
                .totalParticipantsFound(TOTAL_PARTICIPANTS_10)
                .totalBatchesCreated(TOTAL_BATCHES_2)
                .totalHolidaysFound(TOTAL_HOLIDAYS_5)
                .build();
    }

    public static List<ParticipantProjection> createParticipantProjectionsList() {
        return List.of(
                createParticipantProjection(PARTICIPANT_UUID_1, PERSON_ID_1, PRODUCT_PENSION, PAYMENT_REQUEST_UUID_1, PAYMENT_DATE_AUG_20, FREQUENCY_20_EACH_MONTH),
                createParticipantProjection(PARTICIPANT_UUID_2, PERSON_ID_2, PRODUCT_PENSION, PAYMENT_REQUEST_UUID_2, PAYMENT_DATE_AUG_25, FREQUENCY_25_EACH_MONTH),
                createParticipantProjection(PARTICIPANT_UUID_3, PERSON_ID_3, PRODUCT_AUXILIO, PAYMENT_REQUEST_UUID_3, PAYMENT_DATE_AUG_15, FREQUENCY_15_EACH_MONTH),
                createParticipantProjection(PARTICIPANT_UUID_4, PERSON_ID_4, PRODUCT_PRIMA, PAYMENT_REQUEST_UUID_4, PAYMENT_DATE_AUG_5, FREQUENCY_05_EACH_MONTH)
        );
    }

    public static ParticipantProjection createParticipantProjection(String participantId, Long personId, String productName, String paymentRequestId, LocalDate paymentDate, String frequency) {
        return ParticipantProjection.builder()
                .participantId(participantId)
                .personId(personId)
                .productName(productName)
                .paymentRequestId(paymentRequestId)
                .paymentDate(paymentDate)
                .productFrequency(frequency)
                .build();
    }

    public static Map<LocalDate, List<ParticipantProjection>> createGroupedParticipantsByPaymentDate() {
        return Map.of(
                PAYMENT_DATE_AUG_20, List.of(
                        createParticipantProjection(PARTICIPANT_UUID_1, PERSON_ID_1, PRODUCT_PENSION, PAYMENT_REQUEST_UUID_1, PAYMENT_DATE_AUG_20, FREQUENCY_20_EACH_MONTH),
                        createParticipantProjection(PARTICIPANT_UUID_2, PERSON_ID_2, PRODUCT_PENSION, PAYMENT_REQUEST_UUID_2, PAYMENT_DATE_AUG_20, FREQUENCY_20_EACH_MONTH)
                ),
                PAYMENT_DATE_AUG_25, List.of(
                        createParticipantProjection(PARTICIPANT_UUID_3, PERSON_ID_3, PRODUCT_AUXILIO, PAYMENT_REQUEST_UUID_3, PAYMENT_DATE_AUG_25, FREQUENCY_25_EACH_MONTH)
                )
        );
    }

    public static Map<LocalDate, LocalDate> createLiquidationDatesMap() {
        return Map.of(
                PAYMENT_DATE_AUG_20, LIQUIDATION_DATE_AUG_17,
                PAYMENT_DATE_AUG_25, LIQUIDATION_DATE_AUG_22,
                PAYMENT_DATE_AUG_30, LIQUIDATION_DATE_AUG_27
        );
    }

    public static LiquidationBatch createLiquidationBatch(Long batchId, LocalDate liquidationDate, LocalDate dispersionDate, Integer participantCount) {
        return LiquidationBatch.builder()
                .batchId(batchId)
                .liquidationDate(liquidationDate)
                .dispersionDate(dispersionDate)
                .liquidationPeriod(YearMonth.from(dispersionDate).toString())
                .status(LiquidationConstants.STATUS_NEW)
                .participantCount(participantCount)
                .thirdPartyCount(LiquidationConstants.DEFAULT_THIRD_PARTY_COUNT)
                .totalGrossAmountCop(LiquidationConstants.DEFAULT_AMOUNT_ZERO)
                .totalDeductionAmountCop(LiquidationConstants.DEFAULT_AMOUNT_ZERO)
                .totalNetAmountCop(LiquidationConstants.DEFAULT_AMOUNT_ZERO)
                .batchReason(LiquidationConstants.BATCH_REASON_AUTOMATIC_CREATION)
                .build();
    }

    public static RuntimeException createStepFailedException() {
        return new RuntimeException(STEP_FAILED_MESSAGE);
    }

    public static RuntimeException createDatabaseException() {
        return new RuntimeException(DATABASE_ERROR_MESSAGE);
    }

    public static RuntimeException createConnectionException() {
        return new RuntimeException(CONNECTION_ERROR_MESSAGE);
    }
}
