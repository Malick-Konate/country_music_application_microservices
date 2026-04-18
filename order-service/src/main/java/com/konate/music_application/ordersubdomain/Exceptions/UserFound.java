package com.konate.music_application.ordersubdomain.Exceptions;

public class UserFound extends RuntimeException {
    public UserFound(String message) {
        super(message);
    }
}
