package com.konate.music_application.catalogsubdomain.Business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konate.music_application.catalogsubdomain.Exceptions.HttpErrorInfo;
import com.konate.music_application.catalogsubdomain.Exceptions.NotFoundException;
import com.konate.music_application.catalogsubdomain.domainClientLayer.ArtistModel;
import com.konate.music_application.catalogsubdomain.domainClientLayer.ArtistServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private ArtistServiceClient client;

    @BeforeEach
    void setup() {
        client = new ArtistServiceClient(restTemplate, objectMapper, "localhost", "8080");
    }

    @Test
    void getArtistById_Success() {
        when(restTemplate.getForObject(anyString(), eq(ArtistModel.class)))
                .thenReturn(new ArtistModel());

        ArtistModel result = client.getArtistById("A1");

        assertNotNull(result);
    }

    @Test
    void getArtistById_NotFound() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                NOT_FOUND, "Not Found", null, "{\"message\":\"Not found\"}".getBytes(), null
        );

        when(restTemplate.getForObject(anyString(), eq(ArtistModel.class)))
                .thenThrow(ex);

        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(NOT_FOUND, "path", "Not found"));

        assertThrows(NotFoundException.class, () -> client.getArtistById("A1"));
    }

    @Test
    void getAllArtist_Success() {
        when(restTemplate.getForObject(anyString(), eq(ArtistModel[].class)))
                .thenReturn(new ArtistModel[]{new ArtistModel()});

        List<ArtistModel> result = client.getAllArtist();

        assertEquals(1, result.size());
    }

    @Test
    void deleteArtist_Success() {
        doNothing().when(restTemplate).delete(anyString());

        client.deleteArtist("A1");

        verify(restTemplate).delete(anyString());
    }


    @Test
    void createArtist_Success() {
        ArtistModel input = new ArtistModel();
        ArtistModel returned = new ArtistModel();

        when(restTemplate.postForObject(anyString(), eq(input), eq(ArtistModel.class)))
                .thenReturn(returned);

        ArtistModel result = client.createArtist(input);

        assertNotNull(result);
    }

    @Test
    void createArtist_NotFound_ThrowsException() throws Exception {
        ArtistModel input = new ArtistModel();

        HttpClientErrorException ex = HttpClientErrorException.create(
                NOT_FOUND, "Not Found", null,
                "{\"message\":\"Artist not found\"}".getBytes(), null
        );

        when(restTemplate.postForObject(anyString(), eq(input), eq(ArtistModel.class)))
                .thenThrow(ex);

        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(NOT_FOUND, "path", "Artist not found"));

        assertThrows(NotFoundException.class, () -> client.createArtist(input));
    }

    @Test
    void updateArtist_Success() {
        ArtistModel artist = new ArtistModel();

        doNothing().when(restTemplate).put(anyString(), eq(artist), eq(ArtistModel.class));
        when(restTemplate.getForObject(anyString(), eq(ArtistModel.class)))
                .thenReturn(artist);

        ArtistModel result = client.updateArtist("A1", artist);

        assertNotNull(result);
    }

    @Test
    void updateArtist_NotFound_ThrowsException() throws Exception {
        ArtistModel artist = new ArtistModel();

        HttpClientErrorException ex = HttpClientErrorException.create(
                NOT_FOUND, "Not Found", null,
                "{\"message\":\"Artist not found\"}".getBytes(), null
        );

        doThrow(ex).when(restTemplate).put(anyString(), eq(artist), eq(ArtistModel.class));

        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(NOT_FOUND, "path", "Artist not found"));

        assertThrows(NotFoundException.class,
                () -> client.updateArtist("A1", artist));
    }

    @Test
    void getArtistByLastName_Success() {
        when(restTemplate.getForObject(anyString(), eq(ArtistModel.class)))
                .thenReturn(new ArtistModel());

        ArtistModel result = client.getArtistByLastName("Smith");

        assertNotNull(result);
    }

    @Test
    void getArtistByLastName_NotFound() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                NOT_FOUND, "Not Found", null,
                "{\"message\":\"Artist not found\"}".getBytes(), null
        );

        when(restTemplate.getForObject(anyString(), eq(ArtistModel.class)))
                .thenThrow(ex);

        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(NOT_FOUND, "path", "Artist not found"));

        assertThrows(NotFoundException.class,
                () -> client.getArtistByLastName("Smith"));
    }

    @Test
    void getArtistById_UnexpectedHttpError_ReThrows() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "Server error", null, null, null
        );

        when(restTemplate.getForObject(anyString(), eq(ArtistModel.class)))
                .thenThrow(ex);

        assertThrows(HttpClientErrorException.class,
                () -> client.getArtistById("A1"));
    }

    @Test
    void getArtistById_UnprocessableEntity_ThrowsNotFoundException() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY,
                "Unprocessable Entity",
                null,
                "{\"message\":\"Invalid artist data\"}".getBytes(),
                null
        );

        when(restTemplate.getForObject(anyString(), eq(ArtistModel.class)))
                .thenThrow(ex);

        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(
                        org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY,
                        "path",
                        "Invalid artist data"
                ));

        assertThrows(NotFoundException.class,
                () -> client.getArtistById("A1"));
    }

    @Test
    void getAllArtist_UnprocessableEntity_ThrowsNotFoundException() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY,
                "Unprocessable Entity",
                null,
                "{\"message\":\"Invalid request\"}".getBytes(),
                null
        );

        when(restTemplate.getForObject(anyString(), eq(ArtistModel[].class)))
                .thenThrow(ex);

        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(
                        org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY,
                        "path",
                        "Invalid request"
                ));

        assertThrows(NotFoundException.class,
                () -> client.getAllArtist());
    }

    @Test
    void getAllArtist_NotFound_ThrowsNotFoundException() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                NOT_FOUND,
                "Not Found",
                null,
                "{\"message\":\"Artists not found\"}".getBytes(),
                null
        );

        when(restTemplate.getForObject(anyString(), eq(ArtistModel[].class)))
                .thenThrow(ex);

        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(NOT_FOUND, "path", "Artists not found"));

        assertThrows(NotFoundException.class,
                () -> client.getAllArtist());
    }

    @Test
    void deleteArtist_NotFound_ThrowsNotFoundException() throws Exception {
        HttpClientErrorException ex = HttpClientErrorException.create(
                NOT_FOUND,
                "Not Found",
                null,
                "{\"message\":\"Artist not found\"}".getBytes(),
                null
        );

        doThrow(ex).when(restTemplate).delete(anyString());

        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(new HttpErrorInfo(NOT_FOUND, "path", "Artist not found"));

        assertThrows(NotFoundException.class,
                () -> client.deleteArtist("A1"));
    }
}