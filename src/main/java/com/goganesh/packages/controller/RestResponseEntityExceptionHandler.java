package com.goganesh.packages.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goganesh.packages.exception.ApiException;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @SneakyThrows
    @ExceptionHandler(value = {ApiException.class})
    protected ResponseEntity<Object> handleConflict(ApiException ex, WebRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("result", false);
        result.put("error", ex.getMessage());

        return handleExceptionInternal(ex, new ObjectMapper().writeValueAsString(result),
                new HttpHeaders(), ex.getHttpStatus(), request);
    }


}
