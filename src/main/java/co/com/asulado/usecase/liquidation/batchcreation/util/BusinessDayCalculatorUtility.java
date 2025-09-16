package co.com.asulado.usecase.liquidation.batchcreation.util;

import co.com.asulado.model.paymentservice.calendar.Calendar;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class BusinessDayCalculatorUtility {

    public LocalDate findPreviousBusinessDay(LocalDate date, List<Calendar> holidays) {
        LocalDate currentDate = date;

        while (isHolidayOrWeekend(currentDate, holidays)) {
            currentDate = currentDate.minusDays(1);
        }

        return currentDate;
    }

    private boolean isHolidayOrWeekend(LocalDate date, List<Calendar> holidays) {
        // Verify if it's a weekend
        if (date.getDayOfWeek().getValue() >= 6) {
            return true;
        }

        // Verify if it's a holiday
        return holidays.stream()
                .anyMatch(holiday -> holiday.eventDate().equals(date));
    }
}
