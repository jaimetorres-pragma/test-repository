package co.com.asulado.usecase.liquidation.batchcreation.services;

import co.com.asulado.model.paymentservice.calendar.Calendar;
import co.com.asulado.usecase.liquidation.batchcreation.util.BusinessDayCalculatorUtility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Pruebas de BusinessDayCalculatorService")
class BusinessDayCalculatorUtilityTest {

    @Test
    @DisplayName("Debe retornar la misma fecha cuando es día hábil")
    void shouldReturnSameDateWhenBusinessDay() {
        // Given
        List<Calendar> holidays = List.of();

        // When
        var result = BusinessDayCalculatorUtility.findPreviousBusinessDay(
            BusinessDayCalculatorServiceTestData.MONDAY_AUG_26, holidays);

        // Then
        assertEquals(BusinessDayCalculatorServiceTestData.MONDAY_AUG_26, result);
    }

    @Test
    @DisplayName("Debe encontrar el día hábil anterior cuando la fecha es sábado")
    void shouldFindPreviousBusinessDayWhenSaturday() {
        // Given
        List<Calendar> holidays = List.of();

        // When
        var result = BusinessDayCalculatorUtility.findPreviousBusinessDay(
            BusinessDayCalculatorServiceTestData.SATURDAY_AUG_30, holidays);

        // Then
        assertEquals(BusinessDayCalculatorServiceTestData.FRIDAY_AUG_29, result);
    }

    @Test
    @DisplayName("Debe encontrar el día hábil anterior cuando la fecha es domingo")
    void shouldFindPreviousBusinessDayWhenSunday() {
        // Given
        List<Calendar> holidays = List.of();

        // When
        var result = BusinessDayCalculatorUtility.findPreviousBusinessDay(
            BusinessDayCalculatorServiceTestData.SUNDAY_AUG_31, holidays);

        // Then
        assertEquals(BusinessDayCalculatorServiceTestData.FRIDAY_AUG_29, result);
    }

    @Test
    @DisplayName("Debe encontrar el día hábil anterior cuando la fecha es festivo")
    void shouldFindPreviousBusinessDayWhenHoliday() {
        // Given
        List<Calendar> holidays = List.of(
            BusinessDayCalculatorServiceTestData.createCalendarEvent(
                BusinessDayCalculatorServiceTestData.EVENT_ID_1,
                BusinessDayCalculatorServiceTestData.THURSDAY_AUG_7,
                BusinessDayCalculatorServiceTestData.BATALLA_BOYACA_DESCRIPTION)
        );

        // When
        var result = BusinessDayCalculatorUtility.findPreviousBusinessDay(
            BusinessDayCalculatorServiceTestData.THURSDAY_AUG_7, holidays);

        // Then
        assertEquals(BusinessDayCalculatorServiceTestData.WEDNESDAY_AUG_6, result);
    }

    @Test
    @DisplayName("Debe manejar múltiples días no hábiles consecutivos")
    void shouldHandleMultipleConsecutiveNonBusinessDays() {
        // Given
        List<Calendar> holidays = List.of(
            BusinessDayCalculatorServiceTestData.createCalendarEvent(
                BusinessDayCalculatorServiceTestData.EVENT_ID_1,
                BusinessDayCalculatorServiceTestData.FRIDAY_AUG_29,
                BusinessDayCalculatorServiceTestData.SPECIAL_HOLIDAY_DESCRIPTION)
        );

        // When
        var result = BusinessDayCalculatorUtility.findPreviousBusinessDay(
            BusinessDayCalculatorServiceTestData.SUNDAY_AUG_31, holidays);

        // Then
        assertEquals(BusinessDayCalculatorServiceTestData.THURSDAY_AUG_28, result);
    }

    @Test
    @DisplayName("Debe funcionar con lista de festivos vacía")
    void shouldWorkWithEmptyHolidaysList() {
        // Given
        List<Calendar> holidays = List.of();

        // When
        var result = BusinessDayCalculatorUtility.findPreviousBusinessDay(
            BusinessDayCalculatorServiceTestData.FRIDAY_AUG_29, holidays);

        // Then
        assertEquals(BusinessDayCalculatorServiceTestData.FRIDAY_AUG_29, result);
    }
}
