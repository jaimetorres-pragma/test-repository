package co.com.asulado.r2dbc.testdata;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class ParticipantProjectionRepositoryAdapterTestData {
    public static final String SQL = "SOME_SQL";

    public static final String PARTICIPANT_ID_1 = "550e8400-e29b-41d4-a716-446655440001";
    public static final String PARTICIPANT_ID_2 = "550e8400-e29b-41d4-a716-446655440002";
    public static final String PARTICIPANT_ID_3 = "550e8400-e29b-41d4-a716-446655449999";

    public static final LocalDate PAYMENT_DATE_1 = LocalDate.of(2025, 8, 20);
    public static final LocalDate PAYMENT_DATE_2 = LocalDate.of(2025, 8, 21);

    public static final String FREQ_20 = "20CADAMES";
    public static final String FREQ_15 = "15CADAMES";
    public static final String FREQ_21 = "21CADAMES";

    public static final String PAYMENT_REQUEST_ID = "550e8400-e29b-41d4-a716-446655440000";
    public static final Long PERSON_ID = 12345L;
    public static final String PRODUCT_PENSION = "PENSION";
}
