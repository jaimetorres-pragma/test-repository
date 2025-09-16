package co.com.asulado.usecase.liquidation.testdata;

import co.com.asulado.model.constants.LiquidationConstants;
import co.com.asulado.model.payments.ParticipantProjection;
import co.com.asulado.model.paymentservice.calendar.Calendar;
import co.com.asulado.model.paymentservice.calendar.EventType;
import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class TestData {

    public static final Integer DEFAULT_MONTH = 8;
    public static final Integer DEFAULT_YEAR = 2025;
    public static final String DEFAULT_CALENDAR_TYPE = LiquidationConstants.CALENDAR_TYPE_HOLIDAYS_WEEKENDS;
    public static final LocalDate DEFAULT_CURRENT_DATE = LocalDate.of(2025, 8, 1);
    public static final LocalDate GROUPED_DATA_CURRENT_DATE = LocalDate.of(2025, 8, 1);

    public static final LocalDate PAYMENT_DATE_1 = LocalDate.of(2025, 8, 20);
    public static final LocalDate PAYMENT_DATE_2 = LocalDate.of(2025, 8, 25);
    public static final LocalDate PAYMENT_DATE_3 = LocalDate.of(2025, 8, 15);
    public static final LocalDate PAYMENT_DATE_4 = LocalDate.of(2025, 8, 30);
    public static final LocalDate LIQUIDATION_DATE_1 = LocalDate.of(2025, 8, 17);
    public static final LocalDate LIQUIDATION_DATE_2 = LocalDate.of(2025, 8, 22);

    public static final LocalDate HOLIDAY_BATALLA_BOYACA = LocalDate.of(2025, 8, 7);
    public static final LocalDate HOLIDAY_SATURDAY_30 = LocalDate.of(2025, 8, 30);
    public static final LocalDate HOLIDAY_SUNDAY_31 = LocalDate.of(2025, 8, 31);
    public static final LocalDate HOLIDAY_SATURDAY_16 = LocalDate.of(2025, 8, 16);
    public static final LocalDate HOLIDAY_SUNDAY_17 = LocalDate.of(2025, 8, 17);
    public static final LocalDate HOLIDAY_SUNDAY_10 = LocalDate.of(2025, 8, 10);
    public static final LocalDate HOLIDAY_SATURDAY_9 = LocalDate.of(2025, 8, 9);
    public static final LocalDate HOLIDAY_SATURDAY_24 = LocalDate.of(2025, 8, 24);

    public static final Long HOLIDAY_ID_BATALLA_BOYACA = 1L;
    public static final Long HOLIDAY_ID_SATURDAY_30 = 2L;
    public static final Long HOLIDAY_ID_SUNDAY_31 = 3L;
    public static final Long HOLIDAY_ID_SATURDAY_16 = 4L;
    public static final Long HOLIDAY_ID_SUNDAY_17 = 5L;
    public static final Long HOLIDAY_ID_SUNDAY_10 = 6L;
    public static final Long HOLIDAY_ID_SATURDAY_9 = 7L;
    public static final Long HOLIDAY_ID_SATURDAY_24 = 13L;

    public static final String HOLIDAY_DESC_BATALLA_BOYACA = "Batalla de Boyacá";
    public static final String HOLIDAY_DESC_SATURDAY = "Sábado";
    public static final String HOLIDAY_DESC_SUNDAY = "Domingo";

    public static final Long EVENT_TYPE_ID_DEFAULT = 1L;
    public static final String EVENT_TYPE_NAME = LiquidationConstants.CALENDAR_TYPE_HOLIDAYS_WEEKENDS;
    public static final String EVENT_TYPE_DESCRIPTION = "Días festivos y fines de semana";

    public static final String PARTICIPANT_ID_1 = "participant-1";
    public static final String PARTICIPANT_ID_2 = "participant-2";
    public static final String PARTICIPANT_ID_3 = "participant-3";
    public static final String PARTICIPANT_ID_4 = "participant-4";

    public static final String PARTICIPANT_UUID_1 = "550e8400-e29b-41d4-a716-446655440001";
    public static final String PARTICIPANT_UUID_2 = "550e8400-e29b-41d4-a716-446655440002";
    public static final String PARTICIPANT_UUID_3 = "550e8400-e29b-41d4-a716-446655440003";
    public static final String PAYMENT_REQUEST_UUID_1 = "550e8400-e29b-41d4-a716-446655440030";
    public static final String PAYMENT_REQUEST_UUID_2 = "550e8400-e29b-41d4-a716-446655440045";
    public static final String PAYMENT_REQUEST_UUID_3 = "550e8400-e29b-41d4-a716-446655440054";

    public static final Long PERSON_ID_DEFAULT = 1L;
    public static final Long PERSON_ID_1 = 1L;
    public static final Long PERSON_ID_2 = 2L;
    public static final Long PERSON_ID_3 = 3L;

    public static final String PRODUCT_NAME_TEST = "TEST_PRODUCT";
    public static final String PRODUCT_NAME_PENSION = "PENSION_PRODUCT";

    public static final String FREQUENCY_20_EACH_MONTH = "20CADAMES";
    public static final String FREQUENCY_15_EACH_MONTH = "15CADAMES";
    public static final String FREQUENCY_25_EACH_MONTH = "25CADAMES";
    public static final String FREQUENCY_30_CURRENT_MONTH = "30MESACTUAL";

    public static ProcessingContext createDefaultProcessingContext() {
        return ProcessingContext.builder()
                .sla(LiquidationConstants.DEFAULT_SLA_DAYS)
                .month(DEFAULT_MONTH)
                .year(DEFAULT_YEAR)
                .calendarType(DEFAULT_CALENDAR_TYPE)
                .currentDate(DEFAULT_CURRENT_DATE)
                .build();
    }

    public static ProcessingContext createProcessingContextWithParticipants() {
        return createDefaultProcessingContext().toBuilder()
                .participants(createParticipantProjectionsList())
                .build();
    }

    public static ProcessingContext createProcessingContextWithGroupedData() {
        return createProcessingContextWithParticipants().toBuilder()
                .currentDate(GROUPED_DATA_CURRENT_DATE)
                .holidays(createHolidaysList())
                .groupedByPaymentDate(createGroupedParticipants())
                .liquidationDates(createLiquidationDatesMap())
                .build();
    }

    public static List<Calendar> createHolidaysList() {
        return List.of(
                createCalendarEvent(HOLIDAY_ID_BATALLA_BOYACA, HOLIDAY_BATALLA_BOYACA, HOLIDAY_DESC_BATALLA_BOYACA),
                createCalendarEvent(HOLIDAY_ID_SATURDAY_30, HOLIDAY_SATURDAY_30, HOLIDAY_DESC_SATURDAY),
                createCalendarEvent(HOLIDAY_ID_SUNDAY_31, HOLIDAY_SUNDAY_31, HOLIDAY_DESC_SUNDAY),
                createCalendarEvent(HOLIDAY_ID_SATURDAY_16, HOLIDAY_SATURDAY_16, HOLIDAY_DESC_SATURDAY),
                createCalendarEvent(HOLIDAY_ID_SUNDAY_17, HOLIDAY_SUNDAY_17, HOLIDAY_DESC_SUNDAY),
                createCalendarEvent(HOLIDAY_ID_SUNDAY_10, HOLIDAY_SUNDAY_10, HOLIDAY_DESC_SUNDAY),
                createCalendarEvent(HOLIDAY_ID_SATURDAY_9, HOLIDAY_SATURDAY_9, HOLIDAY_DESC_SATURDAY),
                createCalendarEvent(HOLIDAY_ID_SATURDAY_24, HOLIDAY_SATURDAY_24, HOLIDAY_DESC_SATURDAY)
        );
    }

    public static Calendar createCalendarEvent(Long eventId, LocalDate date, String description) {
        EventType eventType = EventType.builder()
                .eventTypeId(EVENT_TYPE_ID_DEFAULT)
                .name(EVENT_TYPE_NAME)
                .description(EVENT_TYPE_DESCRIPTION)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        return Calendar.builder()
                .eventId(eventId)
                .eventDate(date)
                .year(date.getYear())
                .month(date.getMonthValue())
                .day(date.getDayOfMonth())
                .description(description)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .eventType(eventType)
                .build();
    }

    public static List<ParticipantProjection> createParticipantProjectionsList() {
        return List.of(
                createParticipantProjection(PARTICIPANT_ID_1, PAYMENT_DATE_1, FREQUENCY_20_EACH_MONTH),
                createParticipantProjection(PARTICIPANT_ID_2, null, FREQUENCY_15_EACH_MONTH),
                createParticipantProjection(PARTICIPANT_ID_3, PAYMENT_DATE_2, FREQUENCY_25_EACH_MONTH),
                createParticipantProjection(PARTICIPANT_ID_4, null, FREQUENCY_30_CURRENT_MONTH)
        );
    }

    public static List<ParticipantProjection> createParticipantProjectionsWithPaymentDatesList() {
        return List.of(
                createParticipantProjection(PARTICIPANT_ID_1, PAYMENT_DATE_1, FREQUENCY_20_EACH_MONTH),
                createParticipantProjection(PARTICIPANT_ID_2, PAYMENT_DATE_3, FREQUENCY_15_EACH_MONTH),
                createParticipantProjection(PARTICIPANT_ID_3, PAYMENT_DATE_2, FREQUENCY_25_EACH_MONTH),
                createParticipantProjection(PARTICIPANT_ID_4, PAYMENT_DATE_4, FREQUENCY_30_CURRENT_MONTH)
        );
    }

    public static ParticipantProjection createParticipantProjection(String participantId, LocalDate paymentDate, String frequency) {
        return ParticipantProjection.builder()
                .participantId(participantId)
                .personId(PERSON_ID_DEFAULT)
                .productName(PRODUCT_NAME_TEST)
                .paymentRequestId(UUID.randomUUID().toString())
                .paymentDate(paymentDate)
                .productFrequency(frequency)
                .build();
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

    public static Map<LocalDate, List<ParticipantProjection>> createGroupedParticipants() {
        return Map.of(
                PAYMENT_DATE_1, List.of(
                        createParticipantProjection(PARTICIPANT_UUID_1, PERSON_ID_1, PRODUCT_NAME_PENSION, PAYMENT_REQUEST_UUID_1, PAYMENT_DATE_1, FREQUENCY_20_EACH_MONTH),
                        createParticipantProjection(PARTICIPANT_UUID_2, PERSON_ID_2, PRODUCT_NAME_PENSION, PAYMENT_REQUEST_UUID_2, PAYMENT_DATE_1, FREQUENCY_20_EACH_MONTH)
                ),
                PAYMENT_DATE_2, List.of(
                        createParticipantProjection(PARTICIPANT_UUID_3, PERSON_ID_3, PRODUCT_NAME_PENSION, PAYMENT_REQUEST_UUID_3, PAYMENT_DATE_2, FREQUENCY_25_EACH_MONTH)
                )
        );
    }

    public static Map<LocalDate, LocalDate> createLiquidationDatesMap() {
        return Map.of(
                PAYMENT_DATE_1, LIQUIDATION_DATE_1,
                PAYMENT_DATE_2, LIQUIDATION_DATE_2
        );
    }
}
