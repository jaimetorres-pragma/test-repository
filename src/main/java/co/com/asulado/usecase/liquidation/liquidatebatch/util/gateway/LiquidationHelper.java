package co.com.asulado.usecase.liquidation.liquidatebatch.util.gateway;

import java.util.Map;

public interface LiquidationHelper {
    double evaluateFormula(String formula, Map<String, Number> values);
}
