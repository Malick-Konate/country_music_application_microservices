package com.konate.music_application.apigateway.Exceptions;

public class InvalidCampaignStateException extends RuntimeException {
    public InvalidCampaignStateException(String message) {
        super(message);
    }
}
