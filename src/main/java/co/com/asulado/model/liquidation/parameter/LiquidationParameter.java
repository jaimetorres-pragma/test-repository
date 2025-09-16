package co.com.asulado.model.liquidation.parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder(toBuilder = true)
public class LiquidationParameter extends ParameterBase {
    private float debtPercentage;
    private Map<String, Integer> sla;
    private Map<String, SocialSecurity> socialSecurity;
    private CompensationFund compensationFund;
    private TaxBase taxableBase;
    private List<String> incomeTypes;
    private List<String> deductionTypes;
}
