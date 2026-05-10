package com.konate.music_application.ordersubdomain.BusinessLayer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.konate.music_application.ordersubdomain.Exceptions.HttpErrorInfo;
import com.konate.music_application.ordersubdomain.Exceptions.InvalidInputException;
import com.konate.music_application.ordersubdomain.Exceptions.NotFoundException;
import com.konate.music_application.ordersubdomain.domainClientLayer.Artist.ArtistModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.Artist.ArtistServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceClientTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;

    private ArtistServiceClient client;

    private static final String BASE_URL = "http://localhost:7001/api/v1/artists";

    @BeforeEach
    void setUp() {
        client = new ArtistServiceClient(restTemplate, objectMapper, "localhost", "7001");
    }

    // =========================================================================
    // getArtistById
    // =========================================================================

    @Test
    void getArtistById_WhenArtistExists_ReturnsArtist() {
        ArtistModel expected = buildArtist("ART-001");
        when(restTemplate.getForObject(BASE_URL + "/ART-001", ArtistModel.class))
                .thenReturn(expected);

        ArtistModel result = client.getArtistById("ART-001");

        assertNotNull(result);
        assertEquals("ART-001", result.getArtistIdentifier());
    }

//    @Test
//    void getArtistById_WhenNotFound_ThrowsNotFoundException() throws IOException {
//        stubHttpError(HttpStatus.NOT_FOUND, "Artist not found");
//        when(restTemplate.getForObject(contains("ART-XXX"), eq(ArtistModel.class)))
//                .thenThrow(makeHttpError(HttpStatus.NOT_FOUND));
//
//        assertThrows(NotFoundException.class, () -> client.getArtistById("ART-XXX"));
//    }

//    @Test
//    void getArtistById_WhenUnprocessableEntity_ThrowsInvalidInputException() throws IOException {
//        stubHttpError(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid input");
//        when(restTemplate.getForObject(anyString(), eq(ArtistModel.class)))
//                .thenThrow(makeHttpError(HttpStatus.UNPROCESSABLE_ENTITY));
//
//        assertThrows(InvalidInputException.class, () -> client.getArtistById("ART-001"));
//    }

    @Test
    void getArtistById_WhenUnknownError_RethrowsOriginalException() {
        HttpClientErrorException ex = makeHttpError(HttpStatus.TOO_MANY_REQUESTS);
        when(restTemplate.getForObject(anyString(), eq(ArtistModel.class))).thenThrow(ex);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class,
                () -> client.getArtistById("ART-001"));
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, thrown.getStatusCode());
    }

    // =========================================================================
    // getAllArtist
    // =========================================================================

    @Test
    void getAllArtist_WhenArtistsExist_ReturnsList() {
        ArtistModel[] artists = { buildArtist("ART-001"), buildArtist("ART-002") };
        when(restTemplate.getForObject(BASE_URL, ArtistModel[].class)).thenReturn(artists);

        List<ArtistModel> result = client.getAllArtist();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

//    @Test
//    void getAllArtist_WhenNotFound_ThrowsNotFoundException() throws IOException {
//        stubHttpError(HttpStatus.NOT_FOUND, "not found");
//        when(restTemplate.getForObject(eq(BASE_URL), eq(ArtistModel[].class)))
//                .thenThrow(makeHttpError(HttpStatus.NOT_FOUND));
//
//        assertThrows(NotFoundException.class, () -> client.getAllArtist());
//    }

    // =========================================================================
    // createArtist
    // =========================================================================

    @Test
    void createArtist_WhenValid_ReturnsCreatedArtist() {
        ArtistModel artist = buildArtist("ART-001");
        when(restTemplate.postForObject(BASE_URL, artist, ArtistModel.class)).thenReturn(artist);

        ArtistModel result = client.createArtist(artist);

        assertNotNull(result);
        verify(restTemplate).postForObject(BASE_URL, artist, ArtistModel.class);
    }

//    @Test
//    void createArtist_WhenUnprocessableEntity_ThrowsInvalidInputException() throws IOException {
//        stubHttpError(HttpStatus.UNPROCESSABLE_ENTITY, "invalid");
//        when(restTemplate.postForObject(eq(BASE_URL), any(), eq(ArtistModel.class)))
//                .thenThrow(makeHttpError(HttpStatus.UNPROCESSABLE_ENTITY));
//
//        assertThrows(InvalidInputException.class, () -> client.createArtist(buildArtist("ART-001")));
//    }

    // =========================================================================
    // updateArtist
    // =========================================================================

    @Test
    void updateArtist_WhenValid_ReturnsUpdatedArtist() {
        ArtistModel artist = buildArtist("ART-001");
        // updateArtist calls put() then getArtistById()
        doNothing().when(restTemplate).put(anyString(), any(), eq(ArtistModel.class));
        when(restTemplate.getForObject(BASE_URL + "/ART-001", ArtistModel.class)).thenReturn(artist);

        ArtistModel result = client.updateArtist("ART-001", artist);

        assertNotNull(result);
        verify(restTemplate).put(BASE_URL + "/ART-001", artist, ArtistModel.class);
    }

    @Test
    void updateArtist_WhenNotFound_ThrowsNotFoundException() throws IOException {
        stubHttpError(HttpStatus.NOT_FOUND, "not found");
        doThrow(makeHttpError(HttpStatus.NOT_FOUND))
                .when(restTemplate).put(anyString(), any(), eq(ArtistModel.class));

        assertThrows(NotFoundException.class,
                () -> client.updateArtist("ART-XXX", buildArtist("ART-XXX")));
    }

    // =========================================================================
    // deleteArtist
    // =========================================================================

    @Test
    void deleteArtist_WhenValid_DeletesSuccessfully() {
        doNothing().when(restTemplate).delete(BASE_URL + "/ART-001");

        assertDoesNotThrow(() -> client.deleteArtist("ART-001"));
        verify(restTemplate).delete(BASE_URL + "/ART-001");
    }

    @Test
    void deleteArtist_WhenNotFound_ThrowsNotFoundException() throws IOException {
        stubHttpError(HttpStatus.NOT_FOUND, "not found");
        doThrow(makeHttpError(HttpStatus.NOT_FOUND)).when(restTemplate).delete(anyString());

        assertThrows(NotFoundException.class, () -> client.deleteArtist("ART-XXX"));
    }

    // =========================================================================
    // getArtistByLastName
    // =========================================================================

    @Test
    void getArtistByLastName_WhenExists_ReturnsArtist() {
        ArtistModel expected = buildArtist("ART-001");
        when(restTemplate.getForObject(BASE_URL + "/Doe", ArtistModel.class)).thenReturn(expected);

        ArtistModel result = client.getArtistByLastName("Doe");

        assertNotNull(result);
    }

//    @Test
//    void getArtistByLastName_WhenNotFound_ThrowsNotFoundException() throws IOException {
//        stubHttpError(HttpStatus.NOT_FOUND, "not found");
//        when(restTemplate.getForObject(contains("Ghost"), eq(ArtistModel.class)))
//                .thenThrow(makeHttpError(HttpStatus.NOT_FOUND));
//
//        assertThrows(NotFoundException.class, () -> client.getArtistByLastName("Ghost"));
//    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private ArtistModel buildArtist(String id) {
        return ArtistModel.builder()
                .artistIdentifier(id)
                .firstName("John").lastName("Doe").build();
    }

    private void stubHttpError(HttpStatus status, String message) throws IOException {
        HttpErrorInfo errorInfo = mock(HttpErrorInfo.class);
        when(errorInfo.getMessage()).thenReturn(message);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(errorInfo);
    }

    private HttpClientErrorException makeHttpError(HttpStatus status) {
        HttpClientErrorException ex = mock(HttpClientErrorException.class);
        when(ex.getStatusCode()).thenReturn(status);
        when(ex.getResponseBodyAsString()).thenReturn("{\"message\":\"error\"}");
        return ex;
    }
}