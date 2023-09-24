package com.epoxy.config.exceptions;

public class ApiFailedException extends RuntimeException{
    private String message;
    public ApiFailedException(String msg) {
        super(msg);
        this.message = msg;
    }
}
