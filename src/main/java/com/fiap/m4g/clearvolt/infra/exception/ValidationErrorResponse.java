package br.com.fiap.healy.infra.exception;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationErrorResponse {

    private int status;
    private String message;
    private String endpoint;
    private List<ValidationError> errors = new ArrayList<>();

    public void addValidationError(String field, String message) {
        ValidationError error = new ValidationError( field, message );
        errors.add( error );
    }


    @Data
    // classe interna para representar um erro de validação
    public static class ValidationError {
        private String field;
        private String message;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

    }
}
