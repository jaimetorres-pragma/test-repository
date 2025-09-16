package co.com.asulado.r2dbc.testdata;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class LiquidationBatchRepositoryAdapterTestData {
    public static final String STATUS_NEW = "NUEVO";

    public static final LocalDate DATE_2025_08_26 = LocalDate.of(2025, 8, 26);
    public static final LocalDate DATE_2025_08_27 = LocalDate.of(2025, 8, 27);
    public static final LocalDate DATE_2025_08_29 = LocalDate.of(2025, 8, 29);

    public static final long BATCH_ID_1 = 1L;
    public static final long BATCH_ID_10 = 10L;
    public static final long BATCH_ID_20 = 20L;
    public static final long BATCH_ID_30 = 30L;
    public static final long BATCH_ID_40 = 40L;
    public static final long BATCH_ID_99 = 99L;

    public static final long AMOUNT_1 = 1L;
    public static final long AMOUNT_2 = 2L;
    public static final long AMOUNT_1000 = 1000L;
    public static final long AMOUNT_2000 = 2000L;
}
