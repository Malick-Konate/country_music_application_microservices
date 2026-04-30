package com.konate.music_application.adsubdomain.domainClientLayer.Catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konate.music_application.adsubdomain.Exceptions.HttpErrorInfo;
import com.konate.music_application.adsubdomain.Exceptions.InconsistentAlbumException;
import com.konate.music_application.adsubdomain.Exceptions.NotFoundException;
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
public class CatalogServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String CATALOG_SERVICE_URL;

    public CatalogServiceClient(RestTemplate restTemplate,
                                ObjectMapper mapper,
                                @Value("${app.catalog.host}") String albumServiceHost,
                                @Value("${app.catalog.port}") String albumServicePort) {
        this.restTemplate = restTemplate;
        this.objectMapper = mapper;
        this.CATALOG_SERVICE_URL = "http://" + albumServiceHost + ":" + albumServicePort + "/api/v1/album";
    }

    public List<AlbumModel> getAllAlbums() {
        try {
            String url = CATALOG_SERVICE_URL;
            log.info("Calling Catalog Service at {}", url);

            return Arrays.asList(restTemplate.getForObject(url, AlbumModel[].class));
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AlbumModel getAlbum(String albumId) {
        try {
            String url = CATALOG_SERVICE_URL + "/" + albumId;
            log.info("Calling Catalog Service at {}", url);
            return restTemplate.getForObject(url, AlbumModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void  deleteAlbum(String albumId) {
        try {
            String url = CATALOG_SERVICE_URL + "/delete/" + albumId;
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AlbumModel createAlbum(AlbumModel albumRequestModel) {
        try {
            String url = CATALOG_SERVICE_URL + "/create";
            return restTemplate.postForObject(url, albumRequestModel, AlbumModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AlbumModel updateAlbum(String albumId, AlbumModel albumRequestModel) {
        try {
            String url = CATALOG_SERVICE_URL + "/update/" + albumId;
            restTemplate.put(url, albumRequestModel, AlbumModel.class);
            return getAlbum(albumId);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AlbumModel getAlbumByTitle(String title){
        try{
            String url = CATALOG_SERVICE_URL + "/title/" + title;
            return restTemplate.getForObject(url, AlbumModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
            return new InconsistentAlbumException(getErrorMessage(ex));
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
