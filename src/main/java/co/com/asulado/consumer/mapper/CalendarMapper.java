package co.com.asulado.consumer.mapper;

import co.com.asulado.consumer.dto.CalendarDto;
import co.com.asulado.consumer.dto.EventTypeDto;
import co.com.asulado.model.paymentservice.calendar.Calendar;
import co.com.asulado.model.paymentservice.calendar.EventType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CalendarMapper {
    Calendar toDomain(CalendarDto dto);
    EventType toDomain(EventTypeDto dto);
}

