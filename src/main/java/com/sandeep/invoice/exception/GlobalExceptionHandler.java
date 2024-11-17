package com.sandeep.invoice.exception;

import com.sandeep.invoice.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        logException(ex);

        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String fieldErrorMessage = error.getDefaultMessage();
                    return String.format("%s: %s", fieldName, fieldErrorMessage);
                })
                .collect(Collectors.joining(", "));

        return new ErrorResponse("Validation failed: " + errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        logException(ex);

        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> String.format(
                        "%s: %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining(", "));

        return new ErrorResponse("Validation failed: " + errorMessage);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, InvoiceDataException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestExceptions(Exception ex) {
        return logAndReturnException(ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(Exception ex) {
        return logAndReturnException(ex);
    }

    private ErrorResponse logAndReturnException(Exception ex) {
        logException(ex);
        return new ErrorResponse(ex.getMessage());
    }

    private void logException(Exception ex) {
        log.error("Exception caught: {}", ex.getMessage(), ex);
    }
}
