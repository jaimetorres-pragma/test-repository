package co.com.asulado.model.paymentservice.calendar;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder()
public record EventType(
    Long eventTypeId,
    String name,
    String description,
    Boolean active,
    LocalDateTime createdAt
) {}

