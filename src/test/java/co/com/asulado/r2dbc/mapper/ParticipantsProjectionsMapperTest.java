package co.com.asulado.r2dbc.mapper;

import co.com.asulado.model.deduction.Deduction;
import co.com.asulado.model.deduction.Income;
import co.com.asulado.model.deduction.ParticipantDeduction;
import co.com.asulado.r2dbc.entity.row.ParticipantDeductionRow;
import co.com.asulado.r2dbc.testdata.ParticipantProjectionsMapperTestData;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import io.r2dbc.spi.Statement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ParticipantsProjectionsMapperTest {

    private final ParticipantsProjectionsMapper mapper = new ParticipantsProjectionsMapper();

    @Test
    @DisplayName("mapRowToParticipantDeductionProjection: mapea campos y aplica defaults cuando hay nulls")
    void mapRowToParticipantDeductionProjection() {
        Row row = mock(Row.class);
        RowMetadata meta = mock(RowMetadata.class);

        when(row.get("participant_id", Long.class)).thenReturn(11L);
        when(row.get("participant_status", String.class)).thenReturn("ACTIVE");
        when(row.get("product_description", String.class)).thenReturn("Pensión");

        when(row.get("person_id", Long.class)).thenReturn(77L);
        when(row.get("document_type", String.class)).thenReturn("CC");
        when(row.get("document_number", String.class)).thenReturn("123456");
        when(row.get("birth_date", LocalDate.class)).thenReturn(LocalDate.of(1980, 1, 2));
        when(row.get("is_foreign_resident", Boolean.class)).thenReturn(Boolean.TRUE);
        when(row.get("is_person_active", Boolean.class)).thenReturn(Boolean.TRUE);

        when(row.get("income_id", Long.class)).thenReturn(101L);
        when(row.get("income_type", String.class)).thenReturn("MESADA");
        when(row.get("income_frequency", String.class)).thenReturn("MENSUAL");
        when(row.get("number_of_payments", Integer.class)).thenReturn(null);
        when(row.get("payment_number", Integer.class)).thenReturn(3);
        when(row.get("participation_type", String.class)).thenReturn("PERCENTAGE");
        when(row.get("participation_percentage", Double.class)).thenReturn(25.0);
        when(row.get("gross_participation_value", Long.class)).thenReturn(2000L);
        when(row.get("currency", String.class)).thenReturn("COP");
        when(row.get("exchange_rate", Double.class)).thenReturn(null);
        when(row.get("base_participation_value", Long.class)).thenReturn(null);

        when(row.get("deduction_id", Long.class)).thenReturn(501L);
        when(row.get("deduction_is_additional", Boolean.class)).thenReturn(Boolean.FALSE);
        when(row.get("deduction_status", String.class)).thenReturn("OK");
        when(row.get("deduction_type", String.class)).thenReturn("IMP");
        when(row.get("deduction_participant_type", String.class)).thenReturn("TITULAR");
        when(row.get("deduction_percentage", Double.class)).thenReturn(10.0);
        when(row.get("deduction_value", Long.class)).thenReturn(999L);
        when(row.get("deduction_currency", String.class)).thenReturn("COP");
        when(row.get("deduction_exchange_rate", Double.class)).thenReturn(1.0);
        when(row.get("deduction_number_of_payments", Integer.class)).thenReturn(12);
        when(row.get("deduction_start_date", LocalDate.class)).thenReturn(LocalDate.of(2024, 1, 1));
        when(row.get("deduction_end_date", LocalDate.class)).thenReturn(LocalDate.of(2024, 12, 31));

        ParticipantDeductionRow out = mapper.mapRowToParticipantDeductionProjection(row, meta);

        assertEquals(11L, out.getParticipantId());
        assertEquals("ACTIVE", out.getParticipantStatus());
        assertEquals("Pensión", out.getProductDescription());

        assertEquals(77L, out.getPersonId());
        assertEquals("CC", out.getDocumentType());
        assertEquals("123456", out.getDocumentNumber());
        assertEquals(LocalDate.of(1980, 1, 2), out.getBirthDate());

        assertEquals(101L, out.getIncomeId());
        assertEquals("MESADA", out.getIncomeType());
        assertEquals("MENSUAL", out.getIncomeFrequency());
        assertEquals(3, out.getPaymentNumber());
        assertEquals("PERCENTAGE", out.getParticipationType());
        assertEquals(25.0, out.getParticipationPercentage());
        assertEquals(2000L, out.getGrossParticipationValue());
        assertEquals("COP", out.getCurrency());

        assertEquals(501L, out.getDeductionId());
        assertEquals("IMP", out.getDeductionType());
        assertEquals("TITULAR", out.getDeductionParticipantType());
        assertEquals(10.0, out.getDeductionPercentage());
        assertEquals(999L, out.getDeductionValue());
        assertEquals("COP", out.getDeductionCurrency());
        assertEquals(1.0, out.getDeductionExchangeRate());
        assertEquals(12, out.getDeductionNumberOfPayments());
        assertEquals(LocalDate.of(2024, 1, 1), out.getDeductionStartDate());
        assertEquals(LocalDate.of(2024, 12, 31), out.getDeductionEndDate());
    }

    @Test
    @DisplayName("toDomain: arma agregado y elimina duplicados en ingresos y deducciones")
    void toDomain_buildsAggregate_andDeduplicates() {
        ParticipantDeductionRow h = mock(ParticipantDeductionRow.class);
        when(h.getParticipantId()).thenReturn(111L);
        when(h.getParticipantStatus()).thenReturn("ACTIVE");
        when(h.getProductDescription()).thenReturn("Pensión");
        when(h.getPersonId()).thenReturn(77L);
        when(h.getDocumentType()).thenReturn("CC");
        when(h.getDocumentNumber()).thenReturn("123");
        when(h.getBirthDate()).thenReturn(LocalDate.of(1980, 1, 1));
        when(h.isForeignResident()).thenReturn(false);
        when(h.isPersonActive()).thenReturn(true);

        ParticipantDeductionRow i1 = mock(ParticipantDeductionRow.class);
        when(i1.hasIncome()).thenReturn(true);
        when(i1.getIncomeId()).thenReturn(10L);
        when(i1.getIncomeType()).thenReturn("MESADA");
        when(i1.getIncomeFrequency()).thenReturn("MENSUAL");
        when(i1.getNumberOfPayments()).thenReturn(12);
        when(i1.getPaymentNumber()).thenReturn(1);
        when(i1.getParticipationType()).thenReturn("PERCENTAGE");
        when(i1.getParticipationPercentage()).thenReturn(50.0);
        when(i1.getGrossParticipationValue()).thenReturn(1000L);
        when(i1.getCurrency()).thenReturn("COP");
        when(i1.getExchangeRate()).thenReturn(1.0);
        when(i1.getBaseParticipationValue()).thenReturn(1000L);

        ParticipantDeductionRow i2dup = mock(ParticipantDeductionRow.class);
        when(i2dup.hasIncome()).thenReturn(true);
        when(i2dup.getIncomeId()).thenReturn(11L);
        when(i2dup.getIncomeType()).thenReturn("MESADA");
        when(i2dup.getIncomeFrequency()).thenReturn("MENSUAL");
        when(i2dup.getNumberOfPayments()).thenReturn(12);
        when(i2dup.getPaymentNumber()).thenReturn(1);
        when(i2dup.getParticipationType()).thenReturn("PERCENTAGE");
        when(i2dup.getParticipationPercentage()).thenReturn(50.0);
        when(i2dup.getGrossParticipationValue()).thenReturn(1000L);
        when(i2dup.getCurrency()).thenReturn("COP");
        when(i2dup.getExchangeRate()).thenReturn(1.0);
        when(i2dup.getBaseParticipationValue()).thenReturn(1000L);

        ParticipantDeductionRow d1 = mock(ParticipantDeductionRow.class);
        when(d1.hasDeduction()).thenReturn(true);
        when(d1.getDeductionId()).thenReturn(501L);
        when(d1.getDeductionType()).thenReturn("IMP");
        when(d1.getDeductionParticipantType()).thenReturn("TITULAR");
        when(d1.getDeductionNumberOfPayments()).thenReturn(3);
        when(d1.getDeductionValue()).thenReturn(999L);
        when(d1.getDeductionExchangeRate()).thenReturn(1.0);
        when(d1.getDeductionStartDate()).thenReturn(LocalDate.of(2024, 1, 1));
        when(d1.getDeductionEndDate()).thenReturn(LocalDate.of(2024, 12, 31));

        ParticipantDeductionRow d2dup = mock(ParticipantDeductionRow.class);
        when(d2dup.hasDeduction()).thenReturn(true);
        when(d2dup.getDeductionId()).thenReturn(502L);
        when(d2dup.getDeductionType()).thenReturn("IMP");
        when(d2dup.getDeductionParticipantType()).thenReturn("TITULAR");
        when(d2dup.getDeductionNumberOfPayments()).thenReturn(3);
        when(d2dup.getDeductionValue()).thenReturn(999L);
        when(d2dup.getDeductionExchangeRate()).thenReturn(1.0);
        when(d2dup.getDeductionStartDate()).thenReturn(LocalDate.of(2024, 1, 1));
        when(d2dup.getDeductionEndDate()).thenReturn(LocalDate.of(2024, 12, 31));

        ParticipantDeduction domain = mapper.toDomain(List.of(h, i1, i2dup, d1, d2dup));

        assertEquals(111L, domain.getParticipantId());
        assertEquals("ACTIVE", domain.getParticipantStatus());
        assertEquals("Pensión", domain.getProductDescription());
        assertNotNull(domain.getPerson());

        assertEquals(1, domain.getIncomes().size());
        assertEquals(1, domain.getDeductions().size());

        Income onlyIncome = domain.getIncomes().getFirst();
        assertEquals("MESADA", onlyIncome.getType());
        assertEquals(1, onlyIncome.getPaymentNumber());
        assertEquals("COP", onlyIncome.getCurrency());
        assertEquals(1000L, onlyIncome.getGrossParticipationValue());

        Deduction onlyDed = domain.getDeductions().getFirst();
        assertEquals("IMP", onlyDed.getType());
        assertEquals("TITULAR", onlyDed.getParticipantType());
        assertEquals(3, onlyDed.getNumberOfPayments());
        assertEquals(999L, onlyDed.getValue());
    }

    @Test
    void executeParticipantDeduction_executesAndEmitsRowsUpdated() {
        Income withId = mock(Income.class);
        when(withId.getParticipationPercentage()).thenReturn(12.5);
        when(withId.getGrossParticipationValue()).thenReturn(3000L);
        when(withId.getOriginValue()).thenReturn(2500.0);
        when(withId.getLocalValue()).thenReturn(2400.0);
        when(withId.getIncomeId()).thenReturn(10L);

        Income noId = mock(Income.class);
        when(noId.getParticipationPercentage()).thenReturn(5.0);
        when(noId.getGrossParticipationValue()).thenReturn(1000L);
        when(noId.getOriginValue()).thenReturn(900.0);
        when(noId.getLocalValue()).thenReturn(800.0);

        ParticipantDeduction pd = mock(ParticipantDeduction.class);
        when(pd.getIncomes()).thenReturn(List.of(withId, noId));

        Connection conn = mock(Connection.class);
        Statement st = mock(Statement.class);
        Result result = mock(Result.class);

        when(conn.createStatement("UPDATE X")).thenReturn(st);
        when(st.bind(anyInt(), any())).thenReturn(st);
        when(st.bindNull(anyInt(), any())).thenReturn(st);
        when(st.add()).thenReturn(st);

        when(result.getRowsUpdated()).thenReturn(Flux.just(2L));

        when(st.execute()).thenAnswer(inv -> Flux.just(result));

        Flux<Long> out = new ParticipantsProjectionsMapper()
                .executeParticipantDeduction(List.of(pd), conn, "UPDATE X");

        StepVerifier.create(out)
                .expectNext(2L)
                .verifyComplete();

        verify(st, atLeastOnce()).bind(4, 10L);
        verify(st).execute();
    }

    @Test
    @DisplayName("executeParticipantDeduction: si todos los ingresos tienen id nulo, no ejecuta nada y retorna vacío")
    void executeParticipantDeduction_allNullIds_returnsEmptyAndDoesNotExecute() {
        Income a = mock(Income.class);
        when(a.getParticipationPercentage()).thenReturn(10.0);
        when(a.getGrossParticipationValue()).thenReturn(1000L);
        when(a.getOriginValue()).thenReturn(1000.0);
        when(a.getLocalValue()).thenReturn(1000.0);
        when(a.getIncomeId()).thenReturn(0L);

        ParticipantDeduction pd = mock(ParticipantDeduction.class);
        when(pd.getIncomes()).thenReturn(List.of(a));

        Connection conn = mock(Connection.class);
        Statement st = mock(Statement.class);
        when(conn.createStatement("SQL")).thenReturn(st);

        Flux<Long> out = mapper.executeParticipantDeduction(List.of(pd), conn, "SQL");

        StepVerifier.create(out).verifyComplete();

        verify(st, never()).execute();
    }

    @Test
    @DisplayName("executeParticipantDeduction: usa builders de Income/ParticipantDeduction y bindea valores (Double/Long)")
    void executeParticipantDeduction_withBuilders_bindsValues() {
        Income i1 = ParticipantProjectionsMapperTestData.createIncome(10L);
        Income i2 = ParticipantProjectionsMapperTestData.createIncome(11L);

        ParticipantDeduction pd = ParticipantProjectionsMapperTestData.
                createParticipantDeduction(1L, List.of(i1, i2));

        Connection conn = mock(Connection.class);
        Statement st = mock(Statement.class);
        Result result = mock(Result.class);

        when(conn.createStatement("UPD")).thenReturn(st);
        when(st.bind(anyInt(), any())).thenReturn(st);
        when(st.bindNull(anyInt(), any())).thenReturn(st);
        when(st.add()).thenReturn(st);
        when(result.getRowsUpdated()).thenReturn(Flux.just(2L));

        Publisher<? extends Result> pub = Flux.just(result);
        doReturn(pub).when(st).execute();

        StepVerifier.create(mapper.executeParticipantDeduction(List.of(pd), conn, "UPD"))
                .expectNext(2L)
                .verifyComplete();

        verify(st, atLeastOnce()).bind(2, 123L);
        verify(st, atLeastOnce()).bind(3, 7L);

        verify(st, atLeastOnce()).bind(4, 10L);
        verify(st, atLeastOnce()).add();
        verify(st).execute();
    }
}