package br.com.fiap.healy.infra.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setStatus( HttpStatus.BAD_REQUEST.value() );
        response.setMessage( "Erro de validação" );

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            response.addValidationError( fieldError.getField(), fieldError.getDefaultMessage() );
        }

        response.setEndpoint( getEndpointFromRequest( request ) );

        return new ResponseEntity<>( response, HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setStatus( HttpStatus.BAD_REQUEST.value() );
        response.setMessage( "Erro de validação" );

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            response.addValidationError( violation.getPropertyPath().toString(), violation.getMessage() );
        }

        response.setEndpoint( getEndpointFromRequest( request ) );

        return new ResponseEntity<>( response, HttpStatus.BAD_REQUEST );
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setStatus( HttpStatus.INTERNAL_SERVER_ERROR.value() );
        response.setTitle( "Erro interno do servidor" );
        response.setMessage( ex.getMessage() );
        response.setLocalizedMessage( ex.getLocalizedMessage() );
        response.setEndpoint( getEndpointFromRequest( request ) );

        // Aqui você pode adicionar informações adicionais ao objeto ErrorResponse, se necessário.

        return new ResponseEntity<>( response, HttpStatus.INTERNAL_SERVER_ERROR );
    }


    private String getEndpointFromRequest(HttpServletRequest request) {
        return request.getRequestURI();
    }


    @Data
    // Defina a classe ErrorResponse conforme necessário
    public static class ErrorResponse {
        private int status;
        private String message;
        private String localizedMessage;
        private String title;
        private String endpoint;

        // Getters e Setters
    }
}
