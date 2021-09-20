package com.animeshagrawal.timerservice.api;

import exceptions.ApiRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
        final HttpStatus badRequest = e.getStatus();
        ApiException apiException = new ApiException(e.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
        return new ResponseEntity<>(apiException, badRequest);
    }
}
