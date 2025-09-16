package co.com.asulado.model.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Validation errors
    VALIDATION_ERROR("PAG_VAL_0001", "Error de validación en los datos de entrada", 400),
    ERROR_JSON_DESERIALIZATION("PAG_VAL_0002", "Error al deserializar el JSON", 400),
    // Resource not found errors
    ERROR_NOT_FOUND("PAG_NOTF_0001", "El recurso no existe", 404),
    // External service errors
    ERROR_EXTERNAL_SERVICE("PAG_EXT_0001", "Error en el servicio externo", 502),
    // Internal errors
    ERROR_INTERNAL("PAG_INT_0001", "Error interno del servidor", 500);
    private final String code;
    private final String message;
    private final int traditionalStatusCode;
}
