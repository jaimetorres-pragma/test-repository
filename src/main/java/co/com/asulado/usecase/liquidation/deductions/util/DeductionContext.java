package co.com.asulado.usecase.liquidation.deductions.util;

import co.com.asulado.model.deduction.Deduction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DeductionContext {
    private Double grossSalaryInLocalCurrency;
    private Double netSalaryInLocalCurrency;
    private Double additionalIncomeInLocalCurrency;
    private List<Deduction> deductions;
}
