package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.model.payments.ParticipantProjection;
import co.com.asulado.usecase.liquidation.batchcreation.testdata.TestData;
import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilterGroupsByLiquidationDateStepTest {

    private final FilterGroupsByLiquidationDateStep step = new FilterGroupsByLiquidationDateStep();

    static class Scenario {
        final String name;
        final Map<LocalDate, List<ParticipantProjection>> groups;
        final Map<LocalDate, LocalDate> liquidationDates;
        final LocalDate currentDate;
        final Set<LocalDate> expectedKeys;

        Scenario(String name,
                 Map<LocalDate, List<ParticipantProjection>> groups,
                 Map<LocalDate, LocalDate> liquidationDates,
                 LocalDate currentDate,
                 Set<LocalDate> expectedKeys) {
            this.name = name;
            this.groups = groups;
            this.liquidationDates = liquidationDates;
            this.currentDate = currentDate;
            this.expectedKeys = expectedKeys;
        }

        @Override public String toString() { return name; }
    }

    static Stream<Scenario> scenarios() {
        LocalDate current = LocalDate.of(2025, 8, 28);
        LocalDate pd1 = LocalDate.of(2025, 9, 1);
        LocalDate pd2 = LocalDate.of(2025, 9, 2);

        return Stream.of(
            new Scenario(
                "elegible_igual_que_actual",
                TestData.groups(Map.of(pd1, 2)),
                Map.of(pd1, LocalDate.of(2025, 8, 28)),
                current,
                Set.of(pd1)
            ),
            new Scenario(
                "no_elegible_menor_que_actual",
                TestData.groups(Map.of(pd1, 1)),
                Map.of(pd1, LocalDate.of(2025, 8, 27)),
                current,
                Collections.emptySet()
            ),
            new Scenario(
                "mixto_varios_grupos",
                TestData.groups(Map.of(pd1, 3, pd2, 1)),
                Map.of(
                    pd1, LocalDate.of(2025, 8, 28),
                    pd2, LocalDate.of(2025, 8, 27)
                ),
                current,
                Set.of(pd1)
            ),
            new Scenario(
                "sin_fechas_liquidacion",
                TestData.groups(Map.of(pd1, 1)),
                Collections.emptyMap(),
                current,
                Collections.emptySet()
            )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    @DisplayName("Debe filtrar grupos según la fecha de liquidación y la fecha actual")
    void shouldFilterGroups(Scenario s) {
        ProcessingContext input = TestData.contextWith(s.groups, s.liquidationDates, s.currentDate);

        StepVerifier.create(step.process(input))
            .assertNext(ctx -> {
                Map<LocalDate, List<ParticipantProjection>> resultGroups = ctx.getGroupedByPaymentDate();
                Map<LocalDate, LocalDate> resultLiq = ctx.getLiquidationDates();
                assertEquals(s.expectedKeys, resultGroups.keySet(), "keys grupos");
                assertEquals(s.expectedKeys, resultLiq.keySet(), "keys liqDates");
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar el contexto sin cambios cuando no hay grupos")
    void shouldReturnTheContextWithoutChangesWhenThereAreNoGroups() {
        ProcessingContext input = ProcessingContext.builder()
            .groupedByPaymentDate(null)
            .liquidationDates(Map.of(LocalDate.now(), LocalDate.now()))
            .currentDate(LocalDate.of(2025, 8, 28))
            .build();

        StepVerifier.create(step.process(input))
            .expectNext(input)
            .verifyComplete();
    }
}

