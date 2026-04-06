package com.konate.music_application.catalogsubdomain.PresentationLayer;

import com.konate.music_application.catalogsubdomain.DataLayer.AlbumType;
import com.konate.music_application.catalogsubdomain.DataLayer.Song;
import com.konate.music_application.catalogsubdomain.domainClientLayer.ArtistModel;
import com.konate.music_application.catalogsubdomain.domainClientLayer.ArtistServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean; // Use @MockitoBean if on Spring Boot 3.4+
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Sql({"/data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CatalogControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ArtistServiceClient artistServiceClient; // This prevents the "Connection Refused" error

    private final String BASE_URL = "/api/v1/album";
    private final String VALID_ALBUM_ID = "ALB-013";
    private final String INVALID_ALBUM_ID = "album";
    private final String VALID_TITLE = "Cowboy Sunset"; // Ensure this matches buildSampleAlbum or data.sql
    private final String NOT_FOUND_TITLE = "Non Existent Title";

    @BeforeEach
    void setupMock() {
        // We create a fake Artist response to return whenever the Catalog service asks for one
        ArtistModel mockArtist = new ArtistModel();
        mockArtist.setArtistIdentifier("ART-001");
        mockArtist.setFirstName("Mocked");
        mockArtist.setLastName("Artist");

        // Stub the method.
        Mockito.when(artistServiceClient.getArtistById(anyString()))
                .thenReturn(mockArtist);
    }

    @Test
    void whenAlbumsExist_thenReturnAllAlbums() {
        webTestClient.get()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(AlbumResponseModel.class)
                .value(albums -> {
                    assertNotNull(albums);
                    assertFalse(albums.isEmpty());
                });
    }

    @Test
    void whenValidAlbumId_thenReturnAlbum() {
        webTestClient.get()
                .uri(BASE_URL + "/" + VALID_ALBUM_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AlbumResponseModel.class)
                .value(album -> {
                    assertNotNull(album);
                    assertEquals(VALID_ALBUM_ID, album.getAlbumId());
                });
    }

    @Test
    void whenInvalidAlbumId_thenReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URL + "/" + INVALID_ALBUM_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenValidRequest_thenCreateAlbum() {
        AlbumRequestModel request = buildSampleAlbum();

        webTestClient.post()
                .uri(BASE_URL + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AlbumResponseModel.class)
                .value(created -> {
                    assertNotNull(created);
                    assertEquals(request.getTitle(), created.getTitle());
                });
    }

    @Test
    void whenUpdatedAlbum_thenReturnUpdatedAlbum() {
        AlbumRequestModel updatedRequest = buildSampleAlbum();
        updatedRequest.setTitle("Updated Album Title");

        webTestClient.put()
                .uri(BASE_URL + "/update/" + VALID_ALBUM_ID) // Path from AlbumController
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updatedRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AlbumResponseModel.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("Updated Album Title", response.getTitle());
                });
    }

    @Test
    void whenUpdatedInvalid_thenReturnNotFound() {
        AlbumRequestModel updatedRequest = buildSampleAlbum();

        webTestClient.put()
                .uri(BASE_URL + "/update/" + INVALID_ALBUM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenAlbumDeleted_thenReturnNoContent() {
        webTestClient.delete()
                .uri(BASE_URL + "/delete/" + VALID_ALBUM_ID) // Path from AlbumController
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenDeleteInvalidAlbum_thenNotFound() {
        webTestClient.delete()
                .uri(BASE_URL + "/delete/invalid-id")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenCreateAlbumWithInvalidBody_thenBadRequest() {
        webTestClient.post()
                .uri(BASE_URL + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("") // Empty body
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void whenValidTitle_thenReturnAlbum() {
        webTestClient.get()
                .uri(BASE_URL + "/title/" + VALID_TITLE)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AlbumResponseModel.class)
                .value(album -> {
                    assertNotNull(album);
                    assertEquals(VALID_TITLE, album.getTitle());
                });
    }

    @Test
    void whenTitleNotFound_thenReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URL + "/title/" + NOT_FOUND_TITLE)
                .exchange()
                .expectStatus().isNotFound(); // Matches logic in image_c4384a.png
    }
    @Test
    void whenCreateWithNonExistentArtist_thenReturnNotFound() {
        // Arrange
        String nonExistentArtistId = "ART-NOT-FOUND";
        AlbumRequestModel request = buildSampleAlbum();
        request.setArtistId(nonExistentArtistId);

        // CRITICAL: We must tell the Mockito stub to return null for this specific ID
        Mockito.when(artistServiceClient.getArtistById(nonExistentArtistId))
                .thenReturn(null);

        // Act & Assert
        webTestClient.post()
                .uri(BASE_URL + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenCreateWithNoSongs_thenReturnUnprocessableEntity() {

        AlbumRequestModel request = buildSampleAlbum();
        request.setSong(new ArrayList<>());

        // Act & Assert
        webTestClient.post()
                .uri(BASE_URL + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                // Updated to expect 422 based on your invariant
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void whenUpdateNonExistentAlbum_thenReturnNotFound() {
        AlbumRequestModel request = buildSampleAlbum();

        webTestClient.put()
                .uri(BASE_URL + "/update/" + INVALID_ALBUM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound(); // Matches logic in image_c4382a.png
    }

    @Test
    void whenDeleteInvalidAlbum_thenReturnNotFound() {
        webTestClient.delete()
                .uri(BASE_URL + "/delete/invalid-id")
                .exchange()
                .expectStatus().isNotFound();
    }

    private AlbumRequestModel buildSampleAlbum() {
        return AlbumRequestModel.builder()
                .title("Sample Album")
                .albumType(AlbumType.SINGLE)
                .artistId("ART-001")
                .releaseDate(new Date())
                .recordLabel("Sony Music")
                .song(List.of(
                        new Song("Song One", Time.valueOf("00:03:30"), "Lyrics here")
                ))
                .build();
    }
}