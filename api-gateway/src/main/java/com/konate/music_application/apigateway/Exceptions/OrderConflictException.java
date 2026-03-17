package com.konate.music_application.apigateway.Exceptions;

public class OrderConflictException extends RuntimeException {
    public OrderConflictException(String message) {
        super(message);
    }
}
