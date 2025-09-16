package co.com.asulado.model.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String TRACE_ID_PLACEHOLDER = "[traceId={}] ";
    public static final String CODE_SUCCESS = "LIQ_SUCC_0001";
    public static final String EVENT_TYPE_HOLIDAYS_AND_WEEKENDS = "FESTIVOS_Y_FINES_DE_SEMANA";
    public static final String TRACE_ID_HEADER = "X-B3-TraceId";

    // BatchCreationForLiquidationUseCase
    public static final String LOG_STARTING_PARTICIPANT_PROCESSING = "Starting participant processing for liquidation with SLA: {}, month: {}, year: {}";
    public static final String LOG_ALL_PROCESSING_STEPS_COMPLETED = "All processing steps completed successfully";
    public static final String LOG_PROCESSING_COMPLETED_SUCCESSFULLY = "Processing completed successfully: {} batches created, {} participants processed";
    public static final String LOG_ERROR_DURING_PROCESSING = "Error during processing";
    public static final String LOG_EXECUTING_STEP = "Executing step {} of {}: {}";
    public static final String LOG_STEP_COMPLETED_SUCCESSFULLY = "Step {} completed successfully: {}";
    public static final String LOG_ERROR_IN_STEP = "Error in step {} ({}): {}";
    public static final String LOG_PROCESSING_FAILED_WITH_ERROR = "Processing failed with error: {}";

    // LiquidationBatchHandler
    public static final String LOG_PROCESSING_PARTICIPANTS_REQUEST_RECEIVED = "Processing participants for liquidation request received with traceId: {}";
    public static final String LOG_PARTICIPANTS_PROCESSED_SUCCESSFULLY = "Participants processed successfully: {} batches created, {} participants processed";
    public static final String LOG_ERROR_PROCESSING_PARTICIPANTS = "Error processing participants: {}";
    public static final String LOG_UNEXPECTED_ERROR_PROCESSING_PARTICIPANTS = "Unexpected error processing participants";

    // ErrorResponseBuilder
    public static final String LOG_ERROR_OCCURRED_FOR_TRANSACTION = "An error occurred for transaction {} with message: {}";

    // CalendarAdapter
    public static final String LOG_FETCHING_CALENDAR_EVENTS = "Fetching calendar events for month: {}, year: {}, type: {}";
    public static final String LOG_WEBCLIENT_ERROR = "WebClient error: {} - {}";

    // FetchCalendarStep
    public static final String LOG_FETCHING_CALENDAR = "Fetching calendar for month: {}, year: {}, type: {}";
    public static final String LOG_FOUND_CALENDAR_EVENTS = "Found {} calendar events";

    // CalculatePaymentDateStep
    public static final String LOG_CALCULATING_MISSING_PAYMENT_DATES = "Calculating missing payment dates for {} participants";
    public static final String LOG_CALCULATED_PAYMENT_DATE = "Calculated payment date {} for participant {} with frequency {}";
    public static final String LOG_UPDATED_PAYMENT_DATES = "Updated payment dates for participants";

    // GroupByPaymentDateStep
    public static final String LOG_GROUPING_PARTICIPANTS_BY_PAYMENT_DATE = "Grouping {} participants by payment date";
    public static final String LOG_CREATED_GROUPS_BY_PAYMENT_DATE = "Created {} groups by payment date";
    public static final String LOG_GROUP_PARTICIPANTS_COUNT = "Group {}: {} participants";

    // ValidatePaymentDatesAgainstHolidaysStep
    public static final String LOG_VALIDATING_PAYMENT_DATES_AGAINST_HOLIDAYS = "Validating payment dates against holidays for {} participants";
    public static final String LOG_ADJUSTED_PAYMENT_DATE_HOLIDAY = "Adjusted payment date for participant {} from {} to {} (was holiday/weekend)";
    public static final String LOG_VALIDATED_PAYMENT_DATES_AGAINST_HOLIDAYS = "Validated payment dates against holidays";

    // CalculateAndValidateLiquidationDatesStep
    public static final String LOG_CALCULATING_VALIDATING_LIQUIDATION_DATES = "Calculating and validating liquidation dates for {} groups with SLA of {} days";
    public static final String LOG_ADJUSTED_LIQUIDATION_DATE_HOLIDAY = "Adjusted liquidation date for payment date {} from {} to {} (was holiday/weekend)";
    public static final String LOG_GROUP_WITH_PAYMENT_DATE = "Group with payment date {}: {} participants, liquidation date: {}";
    public static final String LOG_CALCULATED_VALIDATED_LIQUIDATION_DATES = "Calculated and validated {} liquidation dates";

    // FilterGroupsByLiquidationDateStep
    public static final String LOG_FILTERING_GROUPS_BY_LIQ_DATE = "Filtering {} groups by liquidation date against current date {}";
    public static final String LOG_GROUP_ELIGIBLE_FOR_BATCH = "Payment date {} with liquidation date {} is eligible for batch creation";
    public static final String LOG_GROUP_NOT_ELIGIBLE_FOR_BATCH = "Payment date {} with liquidation date {} is not eligible for batch creation";
    public static final String LOG_FILTERED_GROUPS_RESULT = "Filtered groups result: {} eligible, {} filtered out";

    // Batch closing
    public static final String LOG_SENDING_CLOSING_EVENT = "Enviando evento de cierre al motor de reglas de liquidación";
    public static final String MSG_OPERATION_EXECUTED_SUCCESSFULLY = "La operación fue ejecutada exitosamente";
    public static final String LOG_FINDING_ELIGIBLE_BATCHES = "Buscando lotes elegibles desde fecha {}";
    public static final String LOG_FOUND_ELIGIBLE_BATCHES = "Encontrados {} lotes elegibles";
    public static final String LOG_COMPUTING_PARTICIPANT_TOTALS = "Calculando totales de participantes para {} lotes";
    public static final String LOG_UPDATED_PARTICIPANT_TOTALS_FOR_BATCH = "Actualizados totales de participantes para lote {}: count={}, bruto={}, deducciones={}, neto={}";
    public static final String LOG_COMPUTING_THIRD_PARTY_TOTALS = "Calculando totales de terceros para {} lotes";
    public static final String LOG_UPDATED_THIRD_PARTY_TOTALS_FOR_BATCH = "Actualizados totales de terceros para lote {}: count={}, bruto={}, deducciones={}, neto={}";

    // FrequencyCalculatorService messages
    public static final String ERR_FREQUENCY_NULL_OR_EMPTY = "Frequency cannot be null or empty";
    public static final String ERR_INVALID_FREQUENCY_FORMAT = "Invalid frequency format: %s. Expected format: must initiate with a number (e.g., 20CADAMES, 1CADA3MESES, 30MESACTUAL, 20UNICOPAGO)";
    public static final String ERR_INVALID_NUMERIC_VALUE_IN_FREQUENCY = "Invalid numeric value in frequency: %s";

    // Parameter
    public static final String GET_ACTION = "OBTENER";
    public static final String LIQUIDATION_PARAMETER_KEY = "liquidacion";
    public static final String GENERAL_PARAMETER_KEY = "general";

    //Logs for Deduction
    public static final String LOG_DEDUCTION_DAYS_TO_CALCULATE_ZERO = "Deducción {}: días a calcular es 0. Se omite cálculo.";
    public static final String LOG_DEDUCTION_INVALID_PAYMENT_COUNT = "Deducción {}: cantidad de pagos inválida ({}). Se omite cálculo.";
    public static final String LOG_DEDUCTION_INVALID_TRM = "Deducción {}: TRM inválida ({}). Se omite cálculo.";
    public static final String LOG_DEDUCTION_INVALID_LOCAL_VALUE = "Deducción {}: valor en moneda local es 0 o negativo ({}). Se omite cálculo.";
    public static final String LOG_DEDUCTION_INVALID_VALUE = "Deducción {}: valor es 0 o negativo ({}). Se omite cálculo.";
    public static final String LOG_DEDUCTION_INVALID_PERCENTAGE = "Deducción {}: porcentaje inválido ({}). Se omite cálculo.";
    public static final String LOG_DEDUCTION_MARKED_AS_ADDITIONAL = "Deducción {} Caja de compensación marcada como adicional. Se omite cálculo.";
    public static final String LOG_DEDUCTION_INVALID_GROSS_SALARY = "Deducción {}: salario bruto es 0 o negativo ({}). Se omite cálculo.";
    public static final String LOG_DEDUCTION_UNEXPECTED_ERROR = "Error inesperado calculando deducción {}: {}";

}
