package com.konate.music_application.adsubdomain.Exceptions;

public class OrderConflictException extends RuntimeException {
    public OrderConflictException(String message) {
        super(message);
    }
}
