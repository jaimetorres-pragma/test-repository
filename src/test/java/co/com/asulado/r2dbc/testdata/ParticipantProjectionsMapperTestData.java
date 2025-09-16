package co.com.asulado.r2dbc.testdata;

import co.com.asulado.model.deduction.Income;
import co.com.asulado.model.deduction.ParticipantDeduction;

import java.time.LocalDate;
import java.util.List;

public class ParticipantProjectionsMapperTestData {

    public static Income createIncome(long incomeId) {
        return Income.builder()
                .incomeId(incomeId)
                .participationPercentage(10.0)
                .grossParticipationValue(1000L)
                .originValue(123.9)
                .localValue(7.1)
                .type("A").frequency("M")
                .paymentNumber(1).numberOfPayments(1)
                .currency("COP").exchangeRate(1.0).baseParticipationValue(1000L)
                .participationType("P")
                .build();
    }

    public static ParticipantDeduction createParticipantDeduction(long participantId, List<Income> incomes) {
        return ParticipantDeduction.builder()
                .participantId(participantId)
                .participantStatus("ACTIVE")
                .productDescription("Product A")
                .person(ParticipantDeduction.Person.builder()
                        .personId(1L).documentType("CC").documentNumber("X")
                        .birthDate(LocalDate.now()).isActive(true).isForeignResident(false).build())
                .incomes(incomes)
                .deductions(List.of())
                .build();
    }
}
