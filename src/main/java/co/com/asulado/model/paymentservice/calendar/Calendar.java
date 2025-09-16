package co.com.asulado.model.paymentservice.calendar;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record Calendar(
    Long eventId,
    LocalDate eventDate,
    Integer year,
    Integer month,
    Integer day,
    String description,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    EventType eventType
) {}

