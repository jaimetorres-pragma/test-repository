package co.com.asulado.usecase.liquidation.batchcreation.services;

import co.com.asulado.model.paymentservice.calendar.Calendar;
import co.com.asulado.model.paymentservice.calendar.EventType;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;

@UtilityClass
public class BusinessDayCalculatorServiceTestData {

    public static final LocalDate MONDAY_AUG_26 = LocalDate.of(2025, 8, 26);
    public static final LocalDate SATURDAY_AUG_30 = LocalDate.of(2025, 8, 30);
    public static final LocalDate SUNDAY_AUG_31 = LocalDate.of(2025, 8, 31);
    public static final LocalDate FRIDAY_AUG_29 = LocalDate.of(2025, 8, 29);
    public static final LocalDate THURSDAY_AUG_7 = LocalDate.of(2025, 8, 7);
    public static final LocalDate WEDNESDAY_AUG_6 = LocalDate.of(2025, 8, 6);
    public static final LocalDate THURSDAY_AUG_28 = LocalDate.of(2025, 8, 28);

    public static final Long EVENT_ID_1 = 1L;
    public static final Long EVENT_TYPE_ID_1 = 1L;
    public static final String EVENT_TYPE_NAME = "Festivo";
    public static final String EVENT_TYPE_DESCRIPTION = "Día festivo";
    public static final String BATALLA_BOYACA_DESCRIPTION = "Batalla de Boyacá";
    public static final String SPECIAL_HOLIDAY_DESCRIPTION = "Special Holiday";

    public static Calendar createCalendarEvent(Long eventId, LocalDate date, String description) {
        EventType eventType = EventType.builder()
            .eventTypeId(EVENT_TYPE_ID_1)
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
}
