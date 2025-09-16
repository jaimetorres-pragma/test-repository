package co.com.asulado.consumer.mapper;

import co.com.asulado.model.paymentservice.calendar.Calendar;
import co.com.asulado.model.paymentservice.calendar.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Pruebas de CalendarMapper")
class CalendarMapperTest {
    private final CalendarMapper mapper = Mappers.getMapper(CalendarMapper.class);

    @Test
    @DisplayName("Debe mapear EventTypeDto a dominio")
    void shouldMapEventTypeDtoToDomain() {
        // Given
        var dto = CalendarMapperTestData.createEventTypeDto();

        // When
        EventType domain = mapper.toDomain(dto);

        // Then
        assertNotNull(domain);
        assertEquals(dto.eventTypeId(), domain.eventTypeId());
        assertEquals(dto.name(), domain.name());
        assertEquals(dto.description(), domain.description());
        assertEquals(dto.active(), domain.active());
        assertEquals(dto.createdAt(), domain.createdAt());
    }

    @Test
    @DisplayName("Debe mapear CalendarDto a dominio")
    void shouldMapCalendarDtoToDomain() {
        // Given
        var dto = CalendarMapperTestData.createCalendarDto();

        // When
        Calendar domain = mapper.toDomain(dto);

        // Then
        assertNotNull(domain);
        assertEquals(dto.eventId(), domain.eventId());
        assertEquals(dto.eventDate(), domain.eventDate());
        assertEquals(dto.year(), domain.year());
        assertEquals(dto.month(), domain.month());
        assertEquals(dto.day(), domain.day());
        assertEquals(dto.description(), domain.description());
        assertEquals(dto.active(), domain.active());
        assertEquals(dto.createdAt(), domain.createdAt());
        assertEquals(dto.updatedAt(), domain.updatedAt());
        assertNotNull(domain.eventType().eventTypeId());
        assertEquals(dto.eventType().eventTypeId(), domain.eventType().eventTypeId());
    }
}
