package co.com.asulado.config;

import co.com.asulado.model.constants.DeductionsConstants;
import co.com.asulado.usecase.liquidation.deductions.calculator.CCFDeduction;
import co.com.asulado.usecase.liquidation.deductions.calculator.DeductionRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DeductionConfig {

    @Bean(DeductionsConstants.BUSINESS_DEDUCTION_PROCESSORS)
    public Map<String, DeductionRule> deductionProcessors(
    ) {
        Map<String,DeductionRule> deductionRuleMap = new HashMap<>();
        deductionRuleMap.put(DeductionsConstants.CCF, new CCFDeduction());
        return deductionRuleMap;
    }
}