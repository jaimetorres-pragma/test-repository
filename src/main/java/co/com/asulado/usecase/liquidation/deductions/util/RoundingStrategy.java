package co.com.asulado.usecase.liquidation.deductions.util;

import java.util.function.BiFunction;

public enum RoundingStrategy implements BiFunction<Double, Integer, Long> {
    UP_TO_NEAREST_MULTIPLE {
        @Override
        public Long apply(Double value, Integer multiple) {
            return (long) (Math.ceil(value / multiple) * multiple);
        }
    },
    HALF_UP {
        @Override
        public Long apply(Double value, Integer multiple) {
            return Math.round(value);
        }
    }
}