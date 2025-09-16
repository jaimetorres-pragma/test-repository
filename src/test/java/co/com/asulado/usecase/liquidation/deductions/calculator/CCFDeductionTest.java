package co.com.asulado.usecase.liquidation.deductions.calculator;


import co.com.asulado.model.deduction.Deduction;
import co.com.asulado.model.liquidation.parameter.LiquidationParameter;
import co.com.asulado.usecase.liquidation.deductions.util.DeductionContext;
import co.com.asulado.usecase.liquidation.testdata.CCFDeductionTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CCFDeductionTest {

    @ParameterizedTest
    @MethodSource("ccfTestCasesPercentages")
    @DisplayName("Calculo Caja de compensación - casos dinámicos porcentaje")
    void shouldCalculateCCFDynamically(double percentage,
                                       double salary,
                                       int payments,
                                       long expectedValue,
                                       long expectedValueOrigin,
                                       long expectedValueLocal,
                                       LocalDate startDate,
                                       LocalDate endDate

    ) {

        CCFDeduction ccfDeduction = new CCFDeduction();

        DeductionContext context = CCFDeductionTestData.createDeductionContext(salary);

        Deduction deduction = CCFDeductionTestData.createDeductionPercentage(percentage,payments, startDate, endDate);

        LiquidationParameter parameter = CCFDeductionTestData.createLiquidationParameter();

        Mono<Deduction> resultMono = ccfDeduction.calculate(CCFDeductionTestData.TRACE_ID,context, deduction, parameter, CCFDeductionTestData.MINIMUM_SALARY);

        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertEquals(expectedValue, result.getValue());
                    assertEquals(expectedValueOrigin, result.getOriginValue());
                    assertEquals(expectedValueLocal, result.getLocalValue());
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("ccfTestCasesValues")
    @DisplayName("Calculo Caja de compensación - casos dinámicos valor")
    void shouldCalculateCCFDynamicallyByValue(long value,
                                       double salary,
                                       int payments,
                                       long expectedValueOrigin,
                                       long expectedValueLocal,
                                       LocalDate startDate,
                                       LocalDate endDate) {

        CCFDeduction ccfDeduction = new CCFDeduction();

        DeductionContext context = CCFDeductionTestData.createDeductionContext(salary);

        Deduction deduction = CCFDeductionTestData.createDeductionValue(value ,payments, startDate, endDate);

        LiquidationParameter parameter = CCFDeductionTestData.createLiquidationParameter();

        Mono<Deduction> resultMono = ccfDeduction.calculate(CCFDeductionTestData.TRACE_ID,context, deduction, parameter, CCFDeductionTestData.MINIMUM_SALARY);

        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertEquals(expectedValueOrigin, result.getOriginValue());
                    assertEquals(expectedValueLocal, result.getLocalValue());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("No calcular Caja de compensación si el salario es 0 o negativo")
    void shouldNotCalculateCCFForZeroOrNegativeSalary() {
        CCFDeduction ccfDeduction = new CCFDeduction();

        DeductionContext context = CCFDeductionTestData.createDeductionContext(0.0);

        Deduction deduction = CCFDeductionTestData.createDeduction();

        LiquidationParameter parameter = CCFDeductionTestData.createLiquidationParameter();

        Mono<Deduction> resultMono = ccfDeduction.calculate(CCFDeductionTestData.TRACE_ID,context, deduction, parameter, CCFDeductionTestData.MINIMUM_SALARY);

        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertEquals(0L, result.getValue());
                    assertEquals(0L, result.getOriginValue());
                    assertEquals(0L, result.getLocalValue());
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("invalidDeductionsProvider")
    @DisplayName("No calcular Caja de compensación para deducciones inválidas")
    void shouldNotCalculateCCFForInvalidDeductions(Deduction deduction) {
        CCFDeduction ccfDeduction = new CCFDeduction();
        DeductionContext context = CCFDeductionTestData.createDeductionContext(1_423_500.0);
        LiquidationParameter parameter = CCFDeductionTestData.createLiquidationParameter();

        Mono<Deduction> resultMono = ccfDeduction.calculate(
                CCFDeductionTestData.TRACE_ID,
                context,
                deduction,
                parameter,
                CCFDeductionTestData.MINIMUM_SALARY
        );

        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertEquals(0L, result.getValue());
                    assertEquals(0L, result.getOriginValue());
                    assertEquals(0L, result.getLocalValue());
                })
                .verifyComplete();
    }

    private static Stream<Deduction> invalidDeductionsProvider() {
        Deduction baseDeduction = CCFDeductionTestData.createDeduction();

        Deduction zeroPayments = baseDeduction.toBuilder()
                .numberOfPayments(0)
                .build();

        Deduction zeroTRM = baseDeduction.toBuilder()
                .exchangeRate(0.0)
                .build();

        Deduction zeroValueType = baseDeduction.toBuilder()
                .participantType("VALOR")
                .value(0L)
                .build();

        Deduction invalidPercentageType1 = baseDeduction.toBuilder()
                .participantType("PORCENTAJE")
                .percentage(0.0)
                .build();

        Deduction invalidPercentageType2 = baseDeduction.toBuilder()
                .participantType("PORCENTAJE")
                .percentage(-0.5)
                .build();

        Deduction invalidPercentageType3 = baseDeduction.toBuilder()
                .participantType("PORCENTAJE")
                .percentage(1.0)
                .build();

        Deduction isAdditional = baseDeduction.toBuilder()
                .isAdditional(true)
                .build();

        return Stream.of(
                zeroPayments,
                zeroTRM,
                zeroValueType,
                invalidPercentageType1,
                invalidPercentageType2,
                invalidPercentageType3,
                isAdditional
        );
    }

    private static Stream<Arguments> ccfTestCasesValues() {
        YearMonth evaluationMonth = YearMonth.from(LocalDate.now());

        return Stream.of(
                Arguments.of(28_500, 1_423_500.0,2,14_250L,14_250L,evaluationMonth.atDay(1),evaluationMonth.atEndOfMonth()),
                Arguments.of(28_500, 1_423_500.0,2,0,0,evaluationMonth.plusMonths(1).atDay(1),evaluationMonth.plusMonths(2).atDay(1))
        );
    }

    private static Stream<Arguments> ccfTestCasesPercentages() {
        YearMonth evaluationMonth = YearMonth.from(LocalDate.now());

        return Stream.of(
                Arguments.of(0.02, 1_423_500.0,1, 28_500L,28_500L,28_500L,evaluationMonth.atDay(1),evaluationMonth.atEndOfMonth()),
                Arguments.of(0.006, 1_423_500.0, 2, 8_600L,4_300L,4_300L,evaluationMonth.atDay(1),evaluationMonth.atEndOfMonth()),
                Arguments.of(0.02, 1_423_500.0,1, 28_500L,28_500L,28_500L,
                        evaluationMonth.minusMonths(1).atDay(1), evaluationMonth.plusMonths(1).atDay(30)),
                Arguments.of(0.02, 29_800_000.0,1, 596000L,596000L,596000L,evaluationMonth.atDay(1),evaluationMonth.atEndOfMonth())
        );
    }
}