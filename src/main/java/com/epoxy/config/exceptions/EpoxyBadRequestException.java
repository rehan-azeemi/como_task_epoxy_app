package com.epoxy.config.exceptions;

public class EpoxyBadRequestException extends RuntimeException{
    private String message;
    public EpoxyBadRequestException(String msg) {
        super(msg);
        this.message = msg;
    }
}
