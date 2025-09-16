package co.com.asulado.usecase.liquidation.batchcreation.services;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class FrequencyCalculatorServiceTestData {

    public static final String FREQUENCY_29_EACH_MONTH = "29CADAMES";
    public static final String FREQUENCY_31_EACH_MONTH = "31CADAMES";
    public static final String FREQUENCY_INVALID = "INVALID";
    public static final String FREQUENCY_EMPTY = "";

    public static final LocalDate CURRENT_DATE_AUG_27_2025 = LocalDate.of(2025, 8, 27);
    public static final LocalDate CURRENT_DATE_FEB_15_2024 = LocalDate.of(2024, 2, 15);
    public static final LocalDate CURRENT_DATE_SEP_15_2025 = LocalDate.of(2025, 9, 15);

    public static final LocalDate EXPECTED_DATE_FEB_29_2024 = LocalDate.of(2024, 2, 29);
    public static final LocalDate EXPECTED_DATE_SEP_30_2025 = LocalDate.of(2025, 9, 30);

    public static final String ERROR_MESSAGE_INVALID_FREQUENCY_FORMAT = "Invalid frequency format";
}
