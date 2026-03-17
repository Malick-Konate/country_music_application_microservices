package com.konate.music_application.apigateway.Exceptions;

public class ArtistFound extends RuntimeException {
    public ArtistFound(String message) {
        super(message);
    }
}
