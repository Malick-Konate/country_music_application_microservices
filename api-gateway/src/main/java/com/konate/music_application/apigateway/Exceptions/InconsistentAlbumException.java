package com.konate.music_application.apigateway.Exceptions;

public class InconsistentAlbumException extends RuntimeException {
    public InconsistentAlbumException(String message) {
        super(message);
    }
}
