package com.serviceorder.presentation.handler;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import com.serviceorder.domain.exception.BusinessException;
import com.serviceorder.domain.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(final BusinessException ex,
            final WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Problem problem = createProblem(status.value(), ex.getMessage(), OffsetDateTime.now(), null);

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(final BusinessException ex,
            final WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        Problem problem = createProblem(status.value(), ex.getMessage(), OffsetDateTime.now(), null);

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

        ArrayList<Field> fields = new ArrayList<Field>();

        ex.getBindingResult().getAllErrors().forEach(
            error -> fields.add(Field.builder()
                                .name(((FieldError) error).getField())
                                .message(messageSource.getMessage(error, LocaleContextHolder.getLocale()))
                                .build())
        );

        String message = new StringBuilder()
                .append("One or more invalid fields. Please, try again with valid information.")
                .toString();

        Problem problem = createProblem(status.value(), message, OffsetDateTime.now(), fields);

        return super.handleExceptionInternal(ex, problem, headers, status, request);
    }

    private Problem createProblem(final Integer status, final String message, final OffsetDateTime dateTime,
            final ArrayList<Field> fields) {

        return Problem.builder()
                    .status(status)
                    .message(message)
                    .dateTime(dateTime)
                    .fields(fields)
                    .build();
    }
    
}