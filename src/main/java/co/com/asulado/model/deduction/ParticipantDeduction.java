package co.com.asulado.model.deduction;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ParticipantDeduction {
    long participantId;
    String participantStatus;
    String productDescription;
    long grossValueLocalTotal;
    long netValueLocalTotal;
    long deductionValueLocalTotal;

    Person person;
    List<Income> incomes;
    List<Deduction> deductions;

    @Data
    @Builder
    public static class Person {
        long personId;
        String documentType;
        String documentNumber;
        LocalDate birthDate;
        boolean isForeignResident;
        boolean isActive;
    }


}