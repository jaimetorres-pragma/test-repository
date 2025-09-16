package co.com.asulado.consumer.adapter;

import co.com.asulado.consumer.dto.CalendarDto;
import co.com.asulado.consumer.dto.EventTypeDto;
import co.com.asulado.validator.dto.response.ApiGenericResponse;
import lombok.experimental.UtilityClass;
import okhttp3.mockwebserver.MockResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class CalendarAdapterTestData {

    public static final Long EVENT_TYPE_ID_1 = 1L;
    public static final String EVENT_TYPE_NAME_HOLIDAYS = "Festivos";
    public static final String EVENT_TYPE_DESCRIPTION = "desc";
    public static final boolean EVENT_TYPE_ACTIVE = true;

    public static final Long EVENT_ID_10 = 10L;
    public static final int YEAR_2025 = 2025;
    public static final int MONTH_AUGUST = 8;
    public static final int DAY_30 = 30;
    public static final String DESCRIPTION_SATURDAY = "Sábado";
    public static final boolean CALENDAR_ACTIVE = true;

    public static final String CALENDAR_TYPE_HOLIDAYS = "Festivos";
    public static final String ERROR_MESSAGE_EXTERNAL = "Error externo";
    public static final String ERROR_MESSAGE_EXTERNAL_SERVICE = "Error en el servicio externo";
    public static final String ERROR_MESSAGE_NOT_FOUND = "El recurso no existe";
    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final int HTTP_STATUS_404 = 404;

    public static EventTypeDto createEventTypeDto() {
        return new EventTypeDto(
            EVENT_TYPE_ID_1,
                EVENT_TYPE_NAME_HOLIDAYS,
            EVENT_TYPE_DESCRIPTION,
            EVENT_TYPE_ACTIVE,
            LocalDateTime.now()
        );
    }

    public static CalendarDto createCalendarDto() {
        return new CalendarDto(
            EVENT_ID_10,
            LocalDate.now(),
            YEAR_2025,
            MONTH_AUGUST,
            DAY_30,
            DESCRIPTION_SATURDAY,
            CALENDAR_ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now(),
            createEventTypeDto()
        );
    }

    public static ApiGenericResponse<List<CalendarDto>> createSuccessfulApiResponse() {
        return ApiGenericResponse.<List<CalendarDto>>builder()
                .success(true)
                .data(List.of(createCalendarDto()))
                .build();
    }

    public static ApiGenericResponse<List<CalendarDto>> createUnsuccessfulApiResponse() {
        return ApiGenericResponse.<List<CalendarDto>>builder()
                .success(false)
                .message(ERROR_MESSAGE_EXTERNAL)
                .build();
    }

    public static MockResponse createJsonMockResponse(String jsonBody) {
        return new MockResponse()
                .setBody(jsonBody)
                .setHeader("Content-Type", CONTENT_TYPE_JSON);
    }

    public static MockResponse createNotFoundMockResponse() {
        return new MockResponse().setResponseCode(HTTP_STATUS_404);
    }
}
