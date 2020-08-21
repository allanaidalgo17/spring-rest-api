package com.serviceorder.presentation.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.serviceorder.domain.exception.BusinessException;

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
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Problem problem = createProblem(status, ex.getMessage(), LocalDateTime.now(), null);

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        ArrayList<Field> fields = new ArrayList<Field>();

        ex.getBindingResult().getAllErrors().forEach(
            error -> fields.add(
                new Field(((FieldError) error).getField(),
                    messageSource.getMessage(error, LocaleContextHolder.getLocale())))
        );

        String message = new String("One or more invalid fields. Please, try again with valid information.");

        Problem problem = createProblem(status, message, LocalDateTime.now(), fields);

        return super.handleExceptionInternal(ex, problem, headers, status, request);
    }

    private Problem createProblem(HttpStatus status, String message,
                 LocalDateTime dateTime, ArrayList<Field> fields) {

        Problem problem = new Problem();
        problem.setStatus(status.value());
        problem.setMessage(message);
        problem.setDateTime(dateTime);
        problem.setFields(fields);

        return problem;
    }
    
}