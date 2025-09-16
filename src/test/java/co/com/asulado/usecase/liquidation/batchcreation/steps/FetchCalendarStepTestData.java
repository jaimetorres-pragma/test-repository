package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FetchCalendarStepTestData {

    public static final int MONTH_AUGUST = 8;
    public static final int MONTH_DECEMBER = 12;
    public static final int YEAR_2025 = 2025;
    public static final int YEAR_2024 = 2024;
    public static final String CALENDAR_TYPE_HOLIDAYS_AND_WEEKEND = "Festivos y fines de semana";
    public static final String CALENDAR_TYPE_HOLIDAYS = "Días festivos";

    public static final int HOLIDAYS_COUNT_8 = 8;
    public static final int HOLIDAYS_COUNT_0 = 0;

    public static final String EXTERNAL_SERVICE_ERROR_MESSAGE = "External service error";

    public static ProcessingContext createCustomContext() {
        return ProcessingContext.builder()
                .month(MONTH_DECEMBER)
                .year(YEAR_2024)
                .calendarType(CALENDAR_TYPE_HOLIDAYS)
                .build();
    }
}
