package com.konate.music_application.apigateway.Exceptions;

public class UserFound extends RuntimeException {
    public UserFound(String message) {
        super(message);
    }
}
