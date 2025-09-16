package co.com.asulado.r2dbc.mapper;

import co.com.asulado.model.deduction.Deduction;
import co.com.asulado.model.deduction.Income;
import co.com.asulado.model.deduction.ParticipantDeduction;
import co.com.asulado.r2dbc.entity.row.ParticipantDeductionRow;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import io.r2dbc.spi.Statement;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Component
public class ParticipantsProjectionsMapper {

    public ParticipantDeductionRow mapRowToParticipantDeductionProjection(Row row, RowMetadata meta) {
        UnaryOperator<String> str = c -> row.get(c, String.class);
        ToIntFunction<String> i32 = c -> java.util.Optional.ofNullable(c)
                .map(k -> row.get(k, Integer.class))
                .orElse(0);
        ToLongFunction<String> i64 = c -> java.util.Optional.ofNullable(c)
                .map(k -> row.get(k, Long.class))
                .orElse(-1L);
        ToDoubleFunction<String> dbl = c -> java.util.Optional.ofNullable(c)
                .map(k -> row.get(k, Double.class))
                .orElse(Double.NaN);
        Predicate<String> bool = c -> c != null && Boolean.TRUE.equals(row.get(c, Boolean.class));
        Function<String, LocalDate> date = c -> row.get(c, LocalDate.class);

        return ParticipantDeductionRow.builder()
                .participantId(i64.applyAsLong("participant_id"))
                .participantStatus(str.apply("participant_status"))
                .productDescription(str.apply("product_description"))

                .personId(i64.applyAsLong("person_id"))
                .documentType(str.apply("document_type"))
                .documentNumber(str.apply("document_number"))

                .birthDate(date.apply("birth_date"))
                .isForeignResident(bool.test("is_foreign_resident"))
                .isPersonActive(bool.test("is_person_active"))

                .incomeId(i64.applyAsLong("income_id"))
                .incomeType(str.apply("income_type"))
                .incomeFrequency(str.apply("income_frequency"))
                .numberOfPayments(i32.applyAsInt("number_of_payments"))
                .paymentNumber(i32.applyAsInt("payment_number"))
                .participationType(str.apply("participation_type"))
                .participationPercentage(dbl.applyAsDouble("participation_percentage"))
                .grossParticipationValue(i64.applyAsLong("gross_participation_value"))
                .currency(str.apply("currency"))
                .exchangeRate(dbl.applyAsDouble("exchange_rate"))
                .baseParticipationValue(i64.applyAsLong("base_participation_value"))

                .deductionId(i64.applyAsLong("deduction_id"))
                .deductionIsAdditional(bool.test("deduction_is_additional"))
                .deductionStatus(str.apply("deduction_status"))
                .deductionType(str.apply("deduction_type"))
                .deductionParticipantType(str.apply("deduction_participant_type"))
                .deductionPercentage(dbl.applyAsDouble("deduction_percentage"))
                .deductionValue(i64.applyAsLong("deduction_value"))
                .deductionCurrency(str.apply("deduction_currency"))
                .deductionExchangeRate(dbl.applyAsDouble("deduction_exchange_rate"))
                .deductionNumberOfPayments(i32.applyAsInt("deduction_number_of_payments"))
                .deductionStartDate(date.apply("deduction_start_date"))
                .deductionEndDate(date.apply("deduction_end_date"))
                .build();
    }

    public ParticipantDeduction toDomain(List<ParticipantDeductionRow> rows) {
        ParticipantDeductionRow h = rows.getFirst();
        var person = ParticipantDeduction.Person.builder()
                .personId(h.getPersonId())
                .documentType(h.getDocumentType())
                .documentNumber(h.getDocumentNumber())
                .birthDate(h.getBirthDate())
                .isForeignResident(h.isForeignResident())
                .isActive(h.isPersonActive())
                .build();

        List<Income> incomes = rows.stream()
                .filter(ParticipantDeductionRow::hasIncome)
                .map(this::toIncome)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(this::incomeKey, x -> x, (a, b) -> a, LinkedHashMap::new),
                        m -> new ArrayList<>(m.values())
                ));

        List<Deduction> deductions = rows.stream()
                .filter(ParticipantDeductionRow::hasDeduction)
                .map(this::toDeduction)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(this::deductionKey, x -> x, (a, b) -> a, LinkedHashMap::new),
                        m -> new ArrayList<>(m.values())
                ));

        return ParticipantDeduction.builder()
                .participantId(h.getParticipantId())
                .participantStatus(h.getParticipantStatus())
                .productDescription(h.getProductDescription())
                .person(person)
                .incomes(incomes)
                .deductions(deductions)
                .build();
    }

    private Income toIncome(ParticipantDeductionRow r) {
        return Income.builder()
                .incomeId(r.getIncomeId())
                .type(r.getIncomeType())
                .frequency(r.getIncomeFrequency())
                .numberOfPayments(r.getNumberOfPayments())
                .paymentNumber(r.getPaymentNumber())
                .participationType(r.getParticipationType())
                .participationPercentage(r.getParticipationPercentage())
                .grossParticipationValue(r.getGrossParticipationValue())
                .currency(r.getCurrency())
                .exchangeRate(r.getExchangeRate())
                .baseParticipationValue(r.getBaseParticipationValue())
                .build();
    }

    private Deduction toDeduction(ParticipantDeductionRow r) {
        return Deduction.builder()
                .deductionId(r.getDeductionId())
                .isAdditional(r.isDeductionIsAdditional())
                .status(r.getDeductionStatus())
                .type(r.getDeductionType())
                .participantType(r.getDeductionParticipantType())
                .percentage(r.getDeductionPercentage())
                .value(r.getDeductionValue())
                .exchangeRate(r.getDeductionExchangeRate())
                .numberOfPayments(r.getDeductionNumberOfPayments())
                .startDate(r.getDeductionStartDate())
                .endDate(r.getDeductionEndDate())
                .build();
    }

    private String incomeKey(Income x) {
        return String.join("|",
                nullSafe(x.getType()),
                String.valueOf(x.getPaymentNumber()),
                nullSafe(x.getCurrency()),
                String.valueOf(x.getGrossParticipationValue()),
                String.valueOf(x.getBaseParticipationValue())
        );
    }

    private String deductionKey(Deduction x) {
        return String.join("|",
                nullSafe(x.getType()),
                nullSafe(x.getParticipantType()),
                String.valueOf(x.getNumberOfPayments()),
                String.valueOf(x.getValue())
        );
    }

    private static String nullSafe(Object o) {
        return o == null ? "" : o.toString();
    }


    public Flux<Long> executeParticipantDeduction (List<ParticipantDeduction> batch,
                                                          Connection conn, String sql) {
        var st = conn.createStatement(sql);
        boolean hasAny = false;
        boolean first   = true;
        for (ParticipantDeduction participantDeduction : batch) {
            for (Income e : participantDeduction.getIncomes()) {
                if (!first) {
                    st.add();
                }
                bindNullable(st, 0, BigDecimal.valueOf(e.getParticipationPercentage()), BigDecimal.class);
                bindNullable(st, 1, toLong(e.getGrossParticipationValue()), Long.class);
                bindNullable(st, 2, toLong(e.getOriginValue()), Long.class);
                bindNullable(st, 3, toLong(e.getLocalValue()), Long.class);
                Long id = toLong(e.getIncomeId());
                if (id == null || id <= 0) {
                    continue;
                }
                st.bind(4, id);

                first  = false;
                hasAny = true;
            }
        }
        if (!hasAny) {
            return Flux.empty();
        }
        return Flux.from(st.execute())
                .flatMap(r -> Flux.from(r.getRowsUpdated()));
    }

    private <T> void bindNullable(Statement st, int idx, T value, Class<T> type) {
        if (value == null) st.bindNull(idx, type);
        else               st.bind(idx, value);
    }

    private Long toLong(Object v) {
        return switch (v) {
            case Long l -> l;
            case Double d -> d.longValue();
            default -> Long.valueOf(v.toString());
        };
    }
}
