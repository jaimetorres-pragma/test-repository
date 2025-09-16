package co.com.asulado.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CalendarDto(
    @JsonProperty("idEvento")
    Long eventId,
    @JsonProperty("fechaEvento")
    LocalDate eventDate,
    @JsonProperty("anio")
    Integer year,
    @JsonProperty("mes")
    Integer month,
    @JsonProperty("dia")
    Integer day,
    @JsonProperty("descripcion")
    String description,
    @JsonProperty("esActivo")
    Boolean active,
    @JsonProperty("fechaCreacion")
    LocalDateTime createdAt,
    @JsonProperty("fechaActualizacion")
    LocalDateTime updatedAt,
    @JsonProperty("tipoEvento")
    EventTypeDto eventType
) {}

