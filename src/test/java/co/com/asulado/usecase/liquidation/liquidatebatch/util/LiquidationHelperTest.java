package co.com.asulado.usecase.liquidation.liquidatebatch.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LiquidationHelperTest {

    private static final double EPS = 1e-9;
    private final LiquidationHelperImpl helper = new LiquidationHelperImpl();
    private static final Map<String, Number> NO_VARS = Collections.emptyMap();

    private static Map<String, Number> vals(Object... kv) {
        Map<String, Number> m = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], (Number) kv[i + 1]);
        }
        return m;
    }

    private static Stream<Arguments> exceptionCases() {
        return Stream.of(
                Arguments.of("(1 + 2", IllegalArgumentException.class),
                Arguments.of("1 + $", IllegalArgumentException.class),
                Arguments.of("1 / (2 - 2)", ArithmeticException.class)
        );
    }

    @Test
    void simpleAddition() {
        double r = helper.evaluateFormula("1 + 2", Map.of());
        assertEquals(3.0, r, EPS);
    }

    @Test
    void precedenceMultiplicationBeforeAdd() {
        double r = helper.evaluateFormula("2 + 3 * 4", Map.of());
        assertEquals(14.0, r, EPS);
    }

    @Test
    void parenthesesOverridePrecedence() {
        double r = helper.evaluateFormula("(2 + 3) * 4", Map.of());
        assertEquals(20.0, r, EPS);
    }

    @Test
    void exponentRightAssociativity() {
        double r = helper.evaluateFormula("2 ^ 3 ^ 2", Map.of());
        assertEquals(512.0, r, EPS);
    }

    @Test
    void exponentWithParentheses() {
        double r = helper.evaluateFormula("(2 ^ 3) ^ 2", Map.of());
        assertEquals(64.0, r, EPS);
    }

    @Test
    void decimalsSupported() {
        double r = helper.evaluateFormula("3.5 * 2", Map.of());
        assertEquals(7.0, r, EPS);
    }

    @Test
    void whitespaceIsIgnored() {
        double r = helper.evaluateFormula("   12  +\t3  ", Map.of());
        assertEquals(15.0, r, EPS);
    }

    @Test
    void unaryMinusAtStart() {
        double r = helper.evaluateFormula("-5 + 2", Map.of());
        assertEquals(-3.0, r, EPS);
    }

    @Test
    void unaryPlusAtStart() {
        double r = helper.evaluateFormula("+5 + 2", Map.of());
        assertEquals(7.0, r, EPS);
    }

    @Test
    void unaryInsideParentheses() {
        double r = helper.evaluateFormula("(-2) * 3", Map.of());
        assertEquals(-6.0, r, EPS);
    }

    @Test
    void variablesBasicExpression() {
        double r = helper.evaluateFormula("a + b * c", vals(
                "a", 2, "b", 3, "c", 4
        ));
        assertEquals(14.0, r, EPS);
    }

    @Test
    void variablesWithUnderscore() {
        double r = helper.evaluateFormula("gross_value - law_deductions", vals(
                "gross_value", 1000, "law_deductions", 123.45
        ));
        assertEquals(876.55, r, EPS);
    }

    @Test
    void moreComplexCombo() {
        double r = helper.evaluateFormula("gross_value - law_deductions + (extra / count) ^ 2", vals(
                "gross_value", 1000,
                "law_deductions", 250,
                "extra", 20,
                "count", 5
        ));
        assertEquals(766.0, r, EPS);
    }

    @ParameterizedTest(name = "{index} ⇒ formula=''{0}'' throws {1}")
    @MethodSource("exceptionCases")
    void evaluateFormula_throws(String formula, Class<? extends Throwable> expected) {
        Executable call = () -> helper.evaluateFormula(formula, NO_VARS); // single invocation
        assertThrows(expected, call);
    }

    @Test
    void errorMissingVariable() {
        String formula = "a + 1";
        Executable call = () -> helper.evaluateFormula(formula, NO_VARS);
        assertThrows(IllegalArgumentException.class, call);
    }

    @Test
    void errorMalformedTrailingOperator() {
        String formula = "1 +";
        Executable call = () -> helper.evaluateFormula(formula, NO_VARS);
        assertThrows(IllegalStateException.class, call);
    }
}
