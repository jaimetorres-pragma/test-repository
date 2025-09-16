package co.com.asulado.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record EventTypeDto(
    @JsonProperty("idTipoEvento")
    Long eventTypeId,
    @JsonProperty("nombre")
    String name,
    @JsonProperty("descripcion")
    String description,
    @JsonProperty("esActivo")
    Boolean active,
    @JsonProperty("fechaCreacion")
    LocalDateTime createdAt
) {}

