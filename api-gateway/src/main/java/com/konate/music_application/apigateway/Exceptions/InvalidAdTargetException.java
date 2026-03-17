package com.konate.music_application.apigateway.Exceptions;

public class InvalidAdTargetException extends RuntimeException {
    public InvalidAdTargetException(String message) {
        super(message);
    }
}
