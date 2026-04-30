package com.konate.music_application.apigateway.domainClientLayer.Ad;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konate.music_application.apigateway.Exceptions.*;
import com.konate.music_application.apigateway.PresentationLayer.Ad.AdRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Ad.AdResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Component
public class AdServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String AD_SERVICE_URL;

    public AdServiceClient(RestTemplate restTemplate,
                           ObjectMapper objectMapper,
                           @Value("${app.ad.host}") String adServiceHost,
                           @Value("${app.ad.port}") String adServicePort) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.AD_SERVICE_URL = "http://" + adServiceHost + ":" + adServicePort + "/api/v1/ads";
    }


    public AdResponseModel getAdById(String adId) {
        try {
            String url = AD_SERVICE_URL + "/" + adId;
            log.info("Calling Ad Service at {}", url);
            return restTemplate.getForObject(url, AdResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AdResponseModel createAd(AdRequestModel ad) {
        try {
            String url = AD_SERVICE_URL + "/create";
            log.info("Calling Ad Service at {}", url);
            return restTemplate.postForObject(url, ad, AdResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void deleteAd(String adId) {
        try {
            String url = AD_SERVICE_URL + "/" + adId;
            log.info("Calling Ad Service at {}", url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AdResponseModel updateAd(String adId, AdRequestModel ad) {
        try {
            String url = AD_SERVICE_URL + "/" + adId;
            log.info("Calling Ad Service at {}", url);
            restTemplate.put(url, ad);
            return getAdById(adId); // Return the updated ad
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<AdResponseModel> getAllAds() {
        try {
            String url = AD_SERVICE_URL;
            log.info("Calling Ad Service at {}", url);
            AdResponseModel[] adArray = restTemplate.getForObject(url, AdResponseModel[].class);
            return List.of(adArray);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void activateAd(String adID){
        try{
            String url = AD_SERVICE_URL + "/" + adID + "/activate";
            log.info("Calling Ad Service at {}", url);
            restTemplate.patchForObject(url, null, Void.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public void pauseAd(String adId) {
        try {
            String url = AD_SERVICE_URL + "/" + adId + "/pause";
            log.info("Calling Ad Service at {}", url);
            restTemplate.patchForObject(url, null, Void.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void resumeAd(String adId) {
        try {
            String url = AD_SERVICE_URL + "/" + adId + "/resume";
            log.info("Calling Ad Service at {}", url);
            restTemplate.patchForObject(url, null, Void.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }



    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
            return new InvalidAdTargetException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == CONFLICT) {
            return new InvalidCampaignStateException(getErrorMessage(ex));
        }

        log.warn("Got an unexpected HTTP error from Artist Service: {}, will rethrow it", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }


    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            // Assuming the error response body can be mapped to HttpErrorInfo
            return objectMapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            // Fallback if the error body cannot be parsed
            return ioex.getMessage();
        }
    }

}
