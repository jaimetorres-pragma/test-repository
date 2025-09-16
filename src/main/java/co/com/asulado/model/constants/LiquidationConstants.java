package co.com.asulado.model.constants;

import lombok.experimental.UtilityClass;


@UtilityClass
public class LiquidationConstants {

    public static final String STATUS_NEW = "NUEVO";
    public static final String STATUS_ACTIVE = "ACTIVO";
    public static final String STATUS_LIQUIDATED = "LIQUIDADO";

    public static final String BATCH_REASON_AUTOMATIC_CREATION = "CREACIÓN AUTOMÁTICA POR SISTEMA";

    public static final Integer DEFAULT_THIRD_PARTY_COUNT = 0;
    public static final Long DEFAULT_AMOUNT_ZERO = 0L;
    public static final Integer DEFAULT_SLA_DAYS = 3;

    public static final String CALENDAR_TYPE_HOLIDAYS_WEEKENDS = "Festivos y fines de semana";
    public static final String CALENDAR_TYPE_HOLIDAYS_AND_WEEKEND = "FESTIVOS_Y_FINES_DE_SEMANA";

    public static final String LOG_CREATING_SAVING_LIQUIDATION_BATCHES = "Creating and saving {} liquidation batches";
    public static final String LOG_CREATING_BATCH_FOR_PAYMENT_DATE = "Creating batch for payment date {} with {} participants";
    public static final String LOG_CREATED_BATCH_WITH_PARTICIPANTS = "Created batch with ID {} containing {} participants for payment date {} and liquidation date {}";
    public static final String LOG_FETCHING_PARTICIPANTS_WITHOUT_BATCH = "Fetching participants without liquidation batch";
    public static final String LOG_FOUND_PARTICIPANTS_TO_PROCESS = "Found {} participants to process";
    public static final String LOG_FAILED_TO_CREATE_BATCH = "Failed to create batch for payment date {} due to: {}";

    public static final String LOG_FOUND_EXISTING_BATCH_FOR_LIQ_DATE = "Found {} existing batch(es) for liquidation date {}. Will reuse batch with ID {}";
    public static final String LOG_ASSIGNING_PARTICIPANTS_TO_EXISTING_BATCH = "Assigning {} participants to existing batch {} for liquidation date {}";
    public static final String LOG_NO_EXISTING_BATCH_FOR_LIQ_DATE = "No existing batch found for liquidation date {}. A new batch will be created";
    public static final String LOG_SKIP_ASSIGNMENT_DUE_TO_PAST_LIQ_DATE = "Skipping group for payment date {} because liquidation date {} is before current date {}";

    public static final String LOG_UPDATING_PARTICIPANT_COUNTS = "Updating participant counts for {} liquidation date(s)";
    public static final String LOG_NO_BATCHES_FOUND_FOR_LIQ_DATE = "No batches found for liquidation date {}";
    public static final String LOG_UPDATING_PARTICIPANT_COUNT_FOR_BATCH = "Updating participant count for batch {} to {}";
    public static final String LOG_UPDATED_PARTICIPANT_COUNT_FOR_BATCH = "Updated participant count for batch {} to {}";
}
