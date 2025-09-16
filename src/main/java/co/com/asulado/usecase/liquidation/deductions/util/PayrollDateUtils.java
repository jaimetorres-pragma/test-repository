package co.com.asulado.usecase.liquidation.deductions.util;

import co.com.asulado.model.constants.DeductionsConstants;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.YearMonth;

@UtilityClass
public class PayrollDateUtils {

    public int calculateDaysToPayInMonth(LocalDate startDate, LocalDate endDate) {
        YearMonth evaluationMonth = YearMonth.from(LocalDate.now());

        if (endDate == null) {
            endDate = evaluationMonth.atEndOfMonth();
        }

        if (YearMonth.from(startDate).isAfter(evaluationMonth) || YearMonth.from(endDate).isBefore(evaluationMonth)) {
            return 0;
        }

        LocalDate firstDayOfCurrentMonth = evaluationMonth.atDay(1);
        LocalDate lastDayOfCurrentMonth = evaluationMonth.atEndOfMonth();

        if (startDate.isBefore(firstDayOfCurrentMonth)) {
            startDate = firstDayOfCurrentMonth;
        }

        if (endDate.isAfter(lastDayOfCurrentMonth)) {
            endDate = lastDayOfCurrentMonth;
        }

        int firstDay = startDate.getDayOfMonth();
        int lastDay = endDate.equals(lastDayOfCurrentMonth) ? DeductionsConstants.DAYS_IN_MONTH : endDate.getDayOfMonth();
        return lastDay - firstDay + 1;
    }
}
