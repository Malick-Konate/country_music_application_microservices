package com.konate.music_application.apigateway.Exceptions;

public class InconsistentPodcastException extends RuntimeException {
    public InconsistentPodcastException(String message) {
        super(message);
    }
}
