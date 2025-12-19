package com.carrilloabogados.legalcase.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.carrilloabogados.legalcase.exception.payload.ExceptionMsg;
import com.carrilloabogados.legalcase.exception.wrapper.CaseActivityNotFoundException;
import com.carrilloabogados.legalcase.exception.wrapper.CaseDocumentNotFoundException;
import com.carrilloabogados.legalcase.exception.wrapper.CaseTypeNotFoundException;
import com.carrilloabogados.legalcase.exception.wrapper.LegalCaseNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApiExceptionHandler {

    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
    })
    public <T extends BindException> ResponseEntity<ExceptionMsg> handleValidationException(final T e) {

        log.info("**ApiExceptionHandler controller, handle validation exception*\n");
        final var badRequest = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                ExceptionMsg.builder()
                        .msg("*" + e.getBindingResult().getFieldError().getDefaultMessage() + "!**")
                        .httpStatus(badRequest)
                        .timestamp(ZonedDateTime
                                .now(ZoneId.systemDefault()))
                        .build(),
                badRequest);
    }

    @ExceptionHandler(value = {
            IllegalStateException.class,
            IllegalArgumentException.class,
    })
    public <T extends RuntimeException> ResponseEntity<ExceptionMsg> handleApiRequestException(final T e) {

        log.info("**ApiExceptionHandler controller, handle API request*\n");
        final var badRequest = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                ExceptionMsg.builder()
                        .msg("#### " + e.getMessage() + "! ####")
                        .httpStatus(badRequest)
                        .timestamp(ZonedDateTime
                                .now(ZoneId.systemDefault()))
                        .build(),
                badRequest);
    }

    @ExceptionHandler(value = {
            LegalCaseNotFoundException.class,
            CaseTypeNotFoundException.class,
            CaseActivityNotFoundException.class,
            CaseDocumentNotFoundException.class,
    })
    public ResponseEntity<ExceptionMsg> handleNotFoundException(final RuntimeException e) {

        log.info("**ApiExceptionHandler controller, handle not found exception*\n");
        final var notFound = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(
                ExceptionMsg.builder()
                        .msg(e.getMessage())
                        .httpStatus(notFound)
                        .timestamp(ZonedDateTime
                                .now(ZoneId.systemDefault()))
                        .build(),
                notFound);
    }

}
