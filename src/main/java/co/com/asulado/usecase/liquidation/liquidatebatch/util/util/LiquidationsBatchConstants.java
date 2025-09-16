package co.com.asulado.usecase.liquidation.liquidatebatch.util.util;
import lombok.experimental.UtilityClass;


@UtilityClass
public class LiquidationsBatchConstants {
    public final String ERR_FORMULA_NULL_OR_BLANK = "formula must not be null or blank";
    public final String ERR_INVALID_CHAR_AT_POS = "Invalid character at pos %d: '%c'";
    public final String ERR_MISMATCHED_PARENTHESES = "Mismatched parentheses";
    public final String ERR_MISSING_VALUE_FOR_VAR = "Missing value for variable: %s";
    public final String ERR_RIGHT_OPERAND_FOR = "right operand for %s";
    public final String ERR_LEFT_OPERAND_FOR = "left operand for %s";
    public final String ERR_MALFORMED_EXPRESSION = "Malformed expression";
    public final String ERR_MISSING_PREFIX = "Missing %s";
    public final String ERR_DIVISION_BY_ZERO = "Division by zero";
    public final String ERR_UNKNOWN_OP = "Unknown op: %s";
}