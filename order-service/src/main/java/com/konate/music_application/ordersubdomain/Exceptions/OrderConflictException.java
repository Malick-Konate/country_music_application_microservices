package com.konate.music_application.ordersubdomain.Exceptions;

public class OrderConflictException extends RuntimeException {
    public OrderConflictException(String message) {
        super(message);
    }
}
