package com.epoxy.config;

import com.epoxy.model.EpoxyErrorResponse;
import com.epoxy.config.exceptions.ApiFailedException;
import com.epoxy.config.exceptions.EpoxyBadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionalHandler {

    @ExceptionHandler(value = EpoxyBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody EpoxyErrorResponse handleException(EpoxyBadRequestException ex) {
        return new EpoxyErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(value = ApiFailedException.class)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody EpoxyErrorResponse handleException(ApiFailedException ex) {
        return new EpoxyErrorResponse(HttpStatus.OK.value(), ex.getMessage());
    }
}