package co.com.asulado.usecase.liquidation.batchcreation.services;

import lombok.experimental.UtilityClass;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

@UtilityClass
public class ExtractDayFromFrequencyTestData {

    public static final String VALID_FREQUENCY_20_EACH_MONTH = "20CADAMES";
    public static final String VALID_FREQUENCY_1_EACH_3_MONTHS = "1CADA3MESES";
    public static final String VALID_FREQUENCY_30_CURRENT_MONTH = "30MESACTUAL";
    public static final String VALID_FREQUENCY_5_EACH_MONTH = "5CADAMES";
    public static final String VALID_FREQUENCY_15_ONE_PAYMENT = "15UNICOPAGO";
    public static final String VALID_FREQUENCY_100_TEST = "100TEST";
    public static final String VALID_FREQUENCY_999_VALIDATION = "999VALIDATION";

    public static final String INVALID_FREQUENCY_NO_DIGITS = "CADAMES";
    public static final String INVALID_FREQUENCY_EMPTY = "";
    public static final String INVALID_FREQUENCY_ONLY_LETTERS = "ABCDEF";
    public static final String INVALID_FREQUENCY_SPECIAL_CHARS = "@#$%";
    public static final String INVALID_FREQUENCY_STARTS_LETTER = "A20CADAMES";

    public static final String MIXED_FREQUENCY_DIGITS_MIDDLE = "CA20DAMES";
    public static final String SINGLE_DIGIT_FREQUENCY = "5";
    public static final String MULTIPLE_DIGITS_ONLY = "12345";
    public static final String ZERO_PREFIX_FREQUENCY = "01CADAMES";
    public static final String LARGE_NUMBER_FREQUENCY = "999999HUGE";

    public static final int EXPECTED_DAY_20 = 20;
    public static final int EXPECTED_DAY_1 = 1;
    public static final int EXPECTED_DAY_30 = 30;
    public static final int EXPECTED_DAY_5 = 5;
    public static final int EXPECTED_DAY_15 = 15;
    public static final int EXPECTED_DAY_100 = 100;
    public static final int EXPECTED_DAY_999 = 999;
    public static final int EXPECTED_DAY_12345 = 12345;
    public static final int EXPECTED_DAY_999999 = 999999;

    public static final String ERROR_MESSAGE_INVALID_FORMAT = "Invalid frequency format";
    public static final String ERROR_MESSAGE_NULL_EMPTY = "Frequency cannot be null or empty";

    public static Stream<Arguments> validFrequencyTestCases() {
        return Stream.of(
            Arguments.of(VALID_FREQUENCY_20_EACH_MONTH, EXPECTED_DAY_20, "20CADAMES"),
            Arguments.of(VALID_FREQUENCY_1_EACH_3_MONTHS, EXPECTED_DAY_1, "1CADA3MESES"),
            Arguments.of(VALID_FREQUENCY_30_CURRENT_MONTH, EXPECTED_DAY_30, "30MESACTUAL"),
            Arguments.of(VALID_FREQUENCY_15_ONE_PAYMENT, EXPECTED_DAY_15, "15UNICOPAGO"),
            Arguments.of(VALID_FREQUENCY_5_EACH_MONTH, EXPECTED_DAY_5, "5CADAMES"),
            Arguments.of(SINGLE_DIGIT_FREQUENCY, EXPECTED_DAY_5, "single digit"),
            Arguments.of(VALID_FREQUENCY_100_TEST, EXPECTED_DAY_100, "100TEST"),
            Arguments.of(VALID_FREQUENCY_999_VALIDATION, EXPECTED_DAY_999, "999VALIDATION"),
            Arguments.of(LARGE_NUMBER_FREQUENCY, EXPECTED_DAY_999999, "large number"),
            Arguments.of(MULTIPLE_DIGITS_ONLY, EXPECTED_DAY_12345, "digits only"),
            Arguments.of(ZERO_PREFIX_FREQUENCY, EXPECTED_DAY_1, "leading zeros"),
            Arguments.of("20CADAMES   ", EXPECTED_DAY_20, "trailing spaces"),
            Arguments.of("15@#$%", EXPECTED_DAY_15, "special characters"),
            Arguments.of("30_SPECIAL_CHARS", EXPECTED_DAY_30, "underscore and chars")
        );
    }

    public static Stream<Arguments> invalidFrequencyTestCases() {
        return Stream.of(
            Arguments.of(null, ERROR_MESSAGE_NULL_EMPTY, "null frequency"),
            Arguments.of(INVALID_FREQUENCY_EMPTY, ERROR_MESSAGE_NULL_EMPTY, "empty frequency"),
            Arguments.of(INVALID_FREQUENCY_NO_DIGITS, ERROR_MESSAGE_INVALID_FORMAT, "no digits"),
            Arguments.of(INVALID_FREQUENCY_ONLY_LETTERS, ERROR_MESSAGE_INVALID_FORMAT, "only letters"),
            Arguments.of(INVALID_FREQUENCY_SPECIAL_CHARS, ERROR_MESSAGE_INVALID_FORMAT, "special chars"),
            Arguments.of(INVALID_FREQUENCY_STARTS_LETTER, ERROR_MESSAGE_INVALID_FORMAT, "starts with letter"),
            Arguments.of(MIXED_FREQUENCY_DIGITS_MIDDLE, ERROR_MESSAGE_INVALID_FORMAT, "digits in middle"),
            Arguments.of("   ", ERROR_MESSAGE_INVALID_FORMAT, "whitespace only")
        );
    }

    public static Stream<Arguments> errorMessageValidationTestCases() {
        return Stream.of(
            Arguments.of(INVALID_FREQUENCY_NO_DIGITS, new String[]{"CADAMES", "20CADAMES", "1CADA3MESES"}, "should include original frequency and examples")
        );
    }
}
