package co.com.asulado.model.error;

import co.com.asulado.model.validations.ValidationError;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private final List<ValidationError> errors;

    public ValidationException(List<ValidationError> errors) {
        super("Errores de validación");
        this.errors = errors;
    }

}