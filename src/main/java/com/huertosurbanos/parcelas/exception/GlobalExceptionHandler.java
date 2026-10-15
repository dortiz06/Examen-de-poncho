package com.huertosurbanos.parcelas.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.huertosurbanos.parcelas.dto.ErrorResponse;
import com.huertosurbanos.parcelas.dto.FieldErrorDetail;
import com.huertosurbanos.parcelas.model.SoilType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ParcelNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ParcelNotFoundException ex,
            HttpServletRequest request
    ) {
        return build(ex.getStatus(), ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(DuplicateParcelException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateParcelException ex,
            HttpServletRequest request
    ) {
        return build(ex.getStatus(), ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(InvalidParcelDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(
            InvalidParcelDataException ex,
            HttpServletRequest request
    ) {
        return build(ex.getStatus(), ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .toList();
        return build(
                HttpStatus.BAD_REQUEST,
                "Los datos enviados no son válidos",
                request.getRequestURI(),
                fieldErrors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        String message = "El cuerpo de la petición está malformado o contiene datos con formato inválido";
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof InvalidFormatException invalidFormat) {
            if (invalidFormat.getTargetType() == SoilType.class) {
                message = "El tipo de suelo no es válido. Valores permitidos: ARCILLOSO, LIMOSO, ARENOSO";
            } else if (invalidFormat.getTargetType() == UUID.class) {
                message = "El identificador de la parcela no tiene un formato UUID válido";
            } else if (invalidFormat.getOriginalMessage() != null
                    && invalidFormat.getOriginalMessage().contains("tipo de suelo")) {
                message = invalidFormat.getOriginalMessage();
            }
        }
        return build(HttpStatus.BAD_REQUEST, message, request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String message = "El valor del parámetro '" + ex.getName() + "' no es válido";
        if (ex.getRequiredType() == UUID.class) {
            message = "El identificador de la parcela no tiene un formato UUID válido";
        }
        return build(HttpStatus.BAD_REQUEST, message, request.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ha ocurrido un error interno. Intente nuevamente más tarde",
                request.getRequestURI(),
                null
        );
    }

    private FieldErrorDetail toFieldError(FieldError fieldError) {
        return new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage());
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String message,
            String path,
            List<FieldErrorDetail> fieldErrors
    ) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                fieldErrors
        );
        return ResponseEntity.status(status).body(body);
    }
}
