package com.konate.music_application.apigateway.domainClientLayer.Artist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konate.music_application.apigateway.Exceptions.HttpErrorInfo;
import com.konate.music_application.apigateway.Exceptions.NotFoundException;
import com.konate.music_application.apigateway.PresentationLayer.Artist.ArtistRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Artist.ArtistResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
@Component
public class ArtistServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String ARTIST_SERVICE_BASE_url;

    public ArtistServiceClient(RestTemplate restTemplate,
                               ObjectMapper mapper,
                               @Value("${app.artist.host}") String artistServiceHost,
                               @Value("${app.artist.port}") String artistServicePort) {
        this.restTemplate = restTemplate;
        this.objectMapper = mapper;
        this.ARTIST_SERVICE_BASE_url = "http://" + artistServiceHost + ":" + artistServicePort + "/api/v1/artists";
    }

    public ArtistResponseModel getArtistById(String artistId) {
        try {
            String url = ARTIST_SERVICE_BASE_url + "/" + artistId;
            log.info("Calling Artist Service at {}", url);
            return restTemplate.getForObject(url, ArtistResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public List<ArtistResponseModel> getAllArtist() {
        try {
            String url = ARTIST_SERVICE_BASE_url;
            log.info("Calling Artist Service at {}", url);

            ArtistResponseModel[] artistArray = restTemplate.getForObject(url, ArtistResponseModel[].class);
            return Arrays.asList(artistArray);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public ArtistResponseModel createArtist(ArtistRequestModel artist) {
        try {
            String url = ARTIST_SERVICE_BASE_url;
            log.info("Calling Artist Service at {}", url);
            return restTemplate.postForObject(url, artist, ArtistResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public ArtistResponseModel updateArtist(String artistId, ArtistRequestModel artist) {
        try {
            String url = ARTIST_SERVICE_BASE_url + "/" + artistId;
            log.info("Calling Artist Service at {}", url);
            restTemplate.put(url, artist, ArtistModel.class);
            return getArtistById(artistId);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public void deleteArtist(String artistId) {
        try {
            String url = ARTIST_SERVICE_BASE_url + "/" + artistId;
            log.info("Calling Artist Service at {}", url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    public ArtistResponseModel getArtistByLastName(String lastName) {
        try {
            String url = ARTIST_SERVICE_BASE_url + "/" + lastName;
            log.info("Calling Artist Service at {}", url);
            return restTemplate.getForObject(url, ArtistResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
            return new NotFoundException(getErrorMessage(ex));
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
