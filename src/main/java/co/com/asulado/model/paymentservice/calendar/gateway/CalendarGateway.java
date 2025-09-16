package co.com.asulado.model.paymentservice.calendar.gateway;

import co.com.asulado.model.paymentservice.calendar.Calendar;
import reactor.core.publisher.Flux;

public interface CalendarGateway {
    Flux<Calendar> findByMonthYearAndType(int month, int year, String type);
}

