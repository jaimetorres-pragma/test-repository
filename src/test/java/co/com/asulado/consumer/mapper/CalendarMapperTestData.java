package co.com.asulado.consumer.mapper;

import co.com.asulado.consumer.dto.CalendarDto;
import co.com.asulado.consumer.dto.EventTypeDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;

@UtilityClass
public class CalendarMapperTestData {

    public static final Long EVENT_TYPE_ID_1 = 1L;
    public static final String EVENT_TYPE_NAME_HOLIDAYS = "Festivos";
    public static final String EVENT_TYPE_DESCRIPTION = "desc";
    public static final boolean EVENT_TYPE_ACTIVE = true;

    public static final Long EVENT_ID_10 = 10L;
    public static final int YEAR_2025 = 2025;
    public static final int MONTH_AUGUST = 8;
    public static final int DAY_30 = 30;
    public static final String DESCRIPTION_SATURDAY = "Sábado";
    public static final boolean CALENDAR_ACTIVE = true;

    public static EventTypeDto createEventTypeDto() {
        return new EventTypeDto(
            EVENT_TYPE_ID_1,
            EVENT_TYPE_NAME_HOLIDAYS,
            EVENT_TYPE_DESCRIPTION,
            EVENT_TYPE_ACTIVE,
            LocalDateTime.now()
        );
    }

    public static CalendarDto createCalendarDto() {
        return new CalendarDto(
            EVENT_ID_10,
            LocalDate.now(),
            YEAR_2025,
            MONTH_AUGUST,
            DAY_30,
            DESCRIPTION_SATURDAY,
            CALENDAR_ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now(),
            createEventTypeDto()
        );
    }
}
