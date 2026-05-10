package com.konate.music_application.ordersubdomain.BusinessLayer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.konate.music_application.ordersubdomain.Exceptions.*;
import com.konate.music_application.ordersubdomain.domainClientLayer.Artist.ArtistModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.Artist.ArtistServiceClient;
import com.konate.music_application.ordersubdomain.domainClientLayer.Catalog.AlbumModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.Catalog.AlbumType;
import com.konate.music_application.ordersubdomain.domainClientLayer.Catalog.CatalogServiceClient;
import com.konate.music_application.ordersubdomain.domainClientLayer.Podcast.*;
import com.konate.music_application.ordersubdomain.domainClientLayer.User.UserModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.User.UserServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceClientsUnitTest {

    // Shared mocks across all nested classes
    @Mock
    RestTemplate restTemplate;
    @Mock
    ObjectMapper objectMapper;

    // =========================================================================
    // Shared helpers
    // =========================================================================

    private HttpClientErrorException makeException(HttpStatus status) {
        return HttpClientErrorException.create(
                status, status.getReasonPhrase(),
                HttpHeaders.EMPTY,
                "{\"message\":\"error from service\"}".getBytes(),
                StandardCharsets.UTF_8
        );
    }

    /** Stub objectMapper so getErrorMessage() can parse the fake JSON body */
    private void stubErrorMessage(String message) throws IOException {
        HttpErrorInfo info = mock(HttpErrorInfo.class);
        when(info.getMessage()).thenReturn(message);
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(info);
    }

    // =========================================================================
    // UserServiceClient
    // =========================================================================

    @Nested
    class UserServiceClientTest {

        UserServiceClient client;

        @BeforeEach
        void setUp() {
            client = new UserServiceClient(restTemplate, objectMapper, "localhost", "7002");
        }

        // --- getUserById ---

        @Test
        void getUserById_WhenSuccess_ReturnsUser() {
            UserModel expected = buildUser("user-001");
            when(restTemplate.getForObject(anyString(), eq(UserModel.class))).thenReturn(expected);

            UserModel result = client.getUserById("user-001");

            assertNotNull(result);
            assertEquals("user-001", result.getUserId());
        }

        @Test
        void getUserById_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("User not found");
            when(restTemplate.getForObject(anyString(), eq(UserModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getUserById("bad-id"));
        }

        @Test
        void getUserById_When422_ThrowsInvalidInputException() throws IOException {
            stubErrorMessage("Invalid input");
            when(restTemplate.getForObject(anyString(), eq(UserModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InvalidInputException.class, () -> client.getUserById("bad-id"));
        }

        @Test
        void getUserById_When409_ThrowsUserFound() throws IOException {
            stubErrorMessage("User already exists");
            when(restTemplate.getForObject(anyString(), eq(UserModel.class)))
                    .thenThrow(makeException(HttpStatus.CONFLICT));

            assertThrows(UserFound.class, () -> client.getUserById("dup-id"));
        }

        @Test
        void getUserById_WhenUnexpectedError_RethrowsHttpClientErrorException() {
            when(restTemplate.getForObject(anyString(), eq(UserModel.class)))
                    .thenThrow(makeException(HttpStatus.INTERNAL_SERVER_ERROR));

            assertThrows(HttpClientErrorException.class, () -> client.getUserById("id"));
        }

        // --- getUserByEmail ---

        @Test
        void getUserByEmail_WhenSuccess_ReturnsUser() {
            UserModel expected = buildUser("user-001");
            when(restTemplate.getForObject(anyString(), eq(UserModel.class))).thenReturn(expected);

            UserModel result = client.getUserByEmail("malick@email.com");

            assertNotNull(result);
        }

        @Test
        void getUserByEmail_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("User not found");
            when(restTemplate.getForObject(anyString(), eq(UserModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getUserByEmail("bad@email.com"));
        }

        @Test
        void getUserByEmail_When409_ThrowsUserFound() throws IOException {
            stubErrorMessage("conflict");
            when(restTemplate.getForObject(anyString(), eq(UserModel.class)))
                    .thenThrow(makeException(HttpStatus.CONFLICT));

            assertThrows(UserFound.class, () -> client.getUserByEmail("dup@email.com"));
        }

        // --- getAllUsers ---

        @Test
        void getAllUsers_WhenSuccess_ReturnsList() {
            when(restTemplate.getForObject(anyString(), eq(UserModel[].class)))
                    .thenReturn(new UserModel[]{buildUser("user-001"), buildUser("user-002")});

            List<UserModel> result = client.getAllUsers();

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        void getAllUsers_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("Not found");
            when(restTemplate.getForObject(anyString(), eq(UserModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAllUsers());
        }

        // --- createUser ---

        @Test
        void createUser_WhenSuccess_ReturnsCreatedUser() {
            UserModel input = buildUser("user-new");
            when(restTemplate.postForObject(anyString(), any(), eq(UserModel.class))).thenReturn(input);

            UserModel result = client.createUser(input);

            assertNotNull(result);
            verify(restTemplate).postForObject(anyString(), eq(input), eq(UserModel.class));
        }

        @Test
        void createUser_When409_ThrowsUserFound() throws IOException {
            stubErrorMessage("duplicate");
            when(restTemplate.postForObject(anyString(), any(), eq(UserModel.class)))
                    .thenThrow(makeException(HttpStatus.CONFLICT));

            assertThrows(UserFound.class, () -> client.createUser(buildUser("dup")));
        }

        // --- updateUser ---

        @Test
        void updateUser_WhenSuccess_ReturnsUpdatedUser() {
            UserModel updated = buildUser("user-001");
            // put returns void; then getUserByUsername is called internally
            doNothing().when(restTemplate).put(anyString(), any(), (Object[]) any());
            when(restTemplate.getForObject(anyString(), eq(UserModel.class))).thenReturn(updated);

            UserModel result = client.updateUser("malick", updated);

            assertNotNull(result);
        }

        @Test
        void updateUser_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            doThrow(makeException(HttpStatus.NOT_FOUND))
                    .when(restTemplate).put(anyString(), any(), (Object[]) any());
            assertThrows(NotFoundException.class,
                    () -> client.updateUser("bad-user", buildUser("x")));
        }

        // --- deleteUser ---

        @Test
        void deleteUser_WhenSuccess_DeletesWithoutError() {
            doNothing().when(restTemplate).delete(anyString());

            assertDoesNotThrow(() -> client.deleteUser("malick"));
            verify(restTemplate).delete(anyString());
        }

        @Test
        void deleteUser_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            doThrow(makeException(HttpStatus.NOT_FOUND)).when(restTemplate).delete(anyString());

            assertThrows(NotFoundException.class, () -> client.deleteUser("bad-user"));
        }

        // Helper
        private UserModel buildUser(String userId) {
            return UserModel.builder().userId(userId).fullname("Malick Konate")
                    .email("malick@email.com").country("Canada").build();
        }
    }

    // =========================================================================
    // ArtistServiceClient
    // =========================================================================

    @Nested
    class ArtistServiceClientTest {

        ArtistServiceClient client;

        @BeforeEach
        void setUp() {
            client = new ArtistServiceClient(restTemplate, objectMapper, "localhost", "7001");
        }

        // --- getArtistById ---

        @Test
        void getArtistById_WhenSuccess_ReturnsArtist() {
            ArtistModel expected = buildArtist("ART-001");
            when(restTemplate.getForObject(anyString(), eq(ArtistModel.class))).thenReturn(expected);

            ArtistModel result = client.getArtistById("ART-001");

            assertNotNull(result);
            assertEquals("ART-001", result.getArtistIdentifier());
        }

        @Test
        void getArtistById_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("Artist not found");
            when(restTemplate.getForObject(anyString(), eq(ArtistModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getArtistById("bad-id"));
        }

        @Test
        void getArtistById_When422_ThrowsInvalidInputException() throws IOException {
            stubErrorMessage("Invalid");
            when(restTemplate.getForObject(anyString(), eq(ArtistModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InvalidInputException.class, () -> client.getArtistById("bad-id"));
        }

        @Test
        void getArtistById_WhenUnexpectedError_RethrowsHttpClientErrorException() {
            when(restTemplate.getForObject(anyString(), eq(ArtistModel.class)))
                    .thenThrow(makeException(HttpStatus.SERVICE_UNAVAILABLE));

            assertThrows(HttpClientErrorException.class, () -> client.getArtistById("id"));
        }

        // --- getAllArtist ---

        @Test
        void getAllArtist_WhenSuccess_ReturnsList() {
            when(restTemplate.getForObject(anyString(), eq(ArtistModel[].class)))
                    .thenReturn(new ArtistModel[]{buildArtist("ART-001"), buildArtist("ART-002")});

            List<ArtistModel> result = client.getAllArtist();

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        void getAllArtist_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            when(restTemplate.getForObject(anyString(), eq(ArtistModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAllArtist());
        }

        // --- getArtistByLastName ---

        @Test
        void getArtistByLastName_WhenSuccess_ReturnsArtist() {
            ArtistModel expected = buildArtist("ART-001");
            when(restTemplate.getForObject(anyString(), eq(ArtistModel.class))).thenReturn(expected);

            ArtistModel result = client.getArtistByLastName("Doe");

            assertNotNull(result);
        }

        @Test
        void getArtistByLastName_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            when(restTemplate.getForObject(anyString(), eq(ArtistModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getArtistByLastName("Unknown"));
        }

        // --- createArtist ---

        @Test
        void createArtist_WhenSuccess_ReturnsCreatedArtist() {
            ArtistModel artist = buildArtist("ART-new");
            when(restTemplate.postForObject(anyString(), any(), eq(ArtistModel.class))).thenReturn(artist);

            ArtistModel result = client.createArtist(artist);

            assertNotNull(result);
        }

        @Test
        void createArtist_When422_ThrowsInvalidInputException() throws IOException {
            stubErrorMessage("invalid");
            when(restTemplate.postForObject(anyString(), any(), eq(ArtistModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InvalidInputException.class, () -> client.createArtist(buildArtist("x")));
        }

        // --- updateArtist ---

        @Test
        void updateArtist_WhenSuccess_ReturnsUpdatedArtist() {
            ArtistModel updated = buildArtist("ART-001");
            doNothing().when(restTemplate).put(anyString(), any(), (Object[]) any());
            when(restTemplate.getForObject(anyString(), eq(ArtistModel.class))).thenReturn(updated);

            ArtistModel result = client.updateArtist("ART-001", updated);

            assertNotNull(result);
        }

        @Test
        void updateArtist_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            doThrow(makeException(HttpStatus.NOT_FOUND))
                    .when(restTemplate).put(anyString(), any(), (Object[]) any());
            assertThrows(NotFoundException.class,
                    () -> client.updateArtist("bad-id", buildArtist("x")));
        }

        // --- deleteArtist ---

        @Test
        void deleteArtist_WhenSuccess_DeletesWithoutError() {
            doNothing().when(restTemplate).delete(anyString());

            assertDoesNotThrow(() -> client.deleteArtist("ART-001"));
            verify(restTemplate).delete(anyString());
        }

        @Test
        void deleteArtist_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            doThrow(makeException(HttpStatus.NOT_FOUND)).when(restTemplate).delete(anyString());

            assertThrows(NotFoundException.class, () -> client.deleteArtist("bad-id"));
        }

        // Helper
        private ArtistModel buildArtist(String id) {
            return ArtistModel.builder().artistIdentifier(id)
                    .firstName("John").lastName("Doe").build();
        }
    }

    // =========================================================================
    // CatalogServiceClient
    // =========================================================================

    @Nested
    class CatalogServiceClientTest {

        CatalogServiceClient client;

        @BeforeEach
        void setUp() {
            client = new CatalogServiceClient(restTemplate, objectMapper, "localhost", "7004");
        }

        // --- getAllAlbums ---

        @Test
        void getAllAlbums_WhenSuccess_ReturnsList() {
            when(restTemplate.getForObject(anyString(), eq(AlbumModel[].class)))
                    .thenReturn(new AlbumModel[]{buildAlbum("ALB-001"), buildAlbum("ALB-002")});

            List<AlbumModel> result = client.getAllAlbums();

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        void getAllAlbums_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            when(restTemplate.getForObject(anyString(), eq(AlbumModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAllAlbums());
        }

        // --- getAlbum ---

        @Test
        void getAlbum_WhenSuccess_ReturnsAlbum() {
            when(restTemplate.getForObject(anyString(), eq(AlbumModel.class))).thenReturn(buildAlbum("ALB-001"));

            AlbumModel result = client.getAlbum("ALB-001");

            assertNotNull(result);
            assertEquals("ALB-001", result.getAlbumId());
        }

        @Test
        void getAlbum_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            when(restTemplate.getForObject(anyString(), eq(AlbumModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAlbum("bad-id"));
        }

        @Test
        void getAlbum_When422_ThrowsInconsistentAlbumException() throws IOException {
            stubErrorMessage("inconsistent");
            when(restTemplate.getForObject(anyString(), eq(AlbumModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InconsistentAlbumException.class, () -> client.getAlbum("bad-id"));
        }

        @Test
        void getAlbum_WhenUnexpectedError_RethrowsHttpClientErrorException() {
            when(restTemplate.getForObject(anyString(), eq(AlbumModel.class)))
                    .thenThrow(makeException(HttpStatus.SERVICE_UNAVAILABLE));

            assertThrows(HttpClientErrorException.class, () -> client.getAlbum("id"));
        }

        // --- getAlbumByTitle ---

        @Test
        void getAlbumByTitle_WhenSuccess_ReturnsAlbum() {
            when(restTemplate.getForObject(anyString(), eq(AlbumModel.class)))
                    .thenReturn(buildAlbum("ALB-001"));

            AlbumModel result = client.getAlbumByTitle("Cowboy Sunset");

            assertNotNull(result);
        }

        @Test
        void getAlbumByTitle_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            when(restTemplate.getForObject(anyString(), eq(AlbumModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAlbumByTitle("Unknown"));
        }

        @Test
        void getAlbumByTitle_When422_ThrowsInconsistentAlbumException() throws IOException {
            stubErrorMessage("inconsistent");
            when(restTemplate.getForObject(anyString(), eq(AlbumModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InconsistentAlbumException.class, () -> client.getAlbumByTitle("bad"));
        }

        // --- createAlbum ---

        @Test
        void createAlbum_WhenSuccess_ReturnsCreatedAlbum() {
            AlbumModel album = buildAlbum("ALB-new");
            when(restTemplate.postForObject(anyString(), any(), eq(AlbumModel.class))).thenReturn(album);

            AlbumModel result = client.createAlbum(album);

            assertNotNull(result);
        }

        @Test
        void createAlbum_When422_ThrowsInconsistentAlbumException() throws IOException {
            stubErrorMessage("inconsistent");
            when(restTemplate.postForObject(anyString(), any(), eq(AlbumModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InconsistentAlbumException.class,
                    () -> client.createAlbum(buildAlbum("x")));
        }

        // --- updateAlbum ---

        @Test
        void updateAlbum_WhenSuccess_ReturnsUpdatedAlbum() {
            AlbumModel updated = buildAlbum("ALB-001");
            doNothing().when(restTemplate).put(anyString(), any(), (Object[]) any());
            when(restTemplate.getForObject(anyString(), eq(AlbumModel.class))).thenReturn(updated);

            AlbumModel result = client.updateAlbum("ALB-001", updated);

            assertNotNull(result);
        }

        @Test
        void updateAlbum_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            doThrow(makeException(HttpStatus.NOT_FOUND))
                    .when(restTemplate).put(anyString(), any(), (Object[]) any());
            assertThrows(NotFoundException.class,
                    () -> client.updateAlbum("bad-id", buildAlbum("x")));
        }

        // --- deleteAlbum ---

        @Test
        void deleteAlbum_WhenSuccess_DeletesWithoutError() {
            doNothing().when(restTemplate).delete(anyString());

            assertDoesNotThrow(() -> client.deleteAlbum("ALB-001"));
        }

        @Test
        void deleteAlbum_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            doThrow(makeException(HttpStatus.NOT_FOUND)).when(restTemplate).delete(anyString());

            assertThrows(NotFoundException.class, () -> client.deleteAlbum("bad-id"));
        }

        // Helper
        private AlbumModel buildAlbum(String id) {
            return AlbumModel.builder().albumId(id).title("Cowboy Sunset")
                    .artistFirstName("Reba").artistLastName("McEntire")
                    .albumType(AlbumType.LP).build();
        }
    }

    // =========================================================================
    // PodcastServiceClient
    // =========================================================================

    @Nested
    class PodcastServiceClientTest {

        PodcastServiceClient client;

        @BeforeEach
        void setUp() {
            client = new PodcastServiceClient(restTemplate, objectMapper, "localhost", "7003");
        }

        // --- getAllPodcasts ---

        @Test
        void getAllPodcasts_WhenSuccess_ReturnsList() {
            when(restTemplate.getForObject(anyString(), eq(PodcastModel[].class)))
                    .thenReturn(new PodcastModel[]{buildPodcast("POD-001"), buildPodcast("POD-002")});

            List<PodcastModel> result = client.getAllPodcasts();

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        void getAllPodcasts_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            when(restTemplate.getForObject(anyString(), eq(PodcastModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAllPodcasts());
        }

        // --- getPodcastById ---

        @Test
        void getPodcastById_WhenSuccess_ReturnsPodcast() {
            when(restTemplate.getForObject(anyString(), eq(PodcastModel.class)))
                    .thenReturn(buildPodcast("POD-001"));

            PodcastModel result = client.getPodcastById("POD-001");

            assertNotNull(result);
        }

        @Test
        void getPodcastById_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            when(restTemplate.getForObject(anyString(), eq(PodcastModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getPodcastById("bad-id"));
        }

        @Test
        void getPodcastById_When422_ThrowsInconsistentPodcastException() throws IOException {
            stubErrorMessage("inconsistent");
            when(restTemplate.getForObject(anyString(), eq(PodcastModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InconsistentPodcastException.class, () -> client.getPodcastById("bad-id"));
        }

        @Test
        void getPodcastById_WhenUnexpectedError_RethrowsHttpClientErrorException() {
            when(restTemplate.getForObject(anyString(), eq(PodcastModel.class)))
                    .thenThrow(makeException(HttpStatus.SERVICE_UNAVAILABLE));

            assertThrows(HttpClientErrorException.class, () -> client.getPodcastById("id"));
        }

        // --- getPodcastByTitle ---

        @Test
        void getPodcastByTitle_WhenSuccess_ReturnsPodcast() {
            when(restTemplate.getForObject(anyString(), eq(PodcastModel.class)))
                    .thenReturn(buildPodcast("POD-001"));

            PodcastModel result = client.getPodcastByTitle("Tech Talk");

            assertNotNull(result);
        }

        @Test
        void getPodcastByTitle_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            when(restTemplate.getForObject(anyString(), eq(PodcastModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getPodcastByTitle("Unknown"));
        }

        @Test
        void getPodcastByTitle_When422_ThrowsInconsistentPodcastException() throws IOException {
            stubErrorMessage("inconsistent");
            when(restTemplate.getForObject(anyString(), eq(PodcastModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InconsistentPodcastException.class,
                    () -> client.getPodcastByTitle("bad"));
        }

        // --- getPodcastByHostname ---

        @Test
        void getPodcastByHostname_WhenSuccess_ReturnsList() {
            when(restTemplate.getForObject(anyString(), eq(PodcastModel[].class)))
                    .thenReturn(new PodcastModel[]{buildPodcast("POD-001")});

            List<PodcastModel> result = client.getPodcastByHostname("John");

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void getPodcastByHostname_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            when(restTemplate.getForObject(anyString(), eq(PodcastModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getPodcastByHostname("Unknown"));
        }

        // --- createPodcast ---

        @Test
        void createPodcast_WhenSuccess_ReturnsCreatedPodcast() {
            PodcastModel podcast = buildPodcast("POD-new");
            when(restTemplate.postForObject(anyString(), any(), eq(PodcastModel.class)))
                    .thenReturn(podcast);

            PodcastModel result = client.createPodcast(podcast);

            assertNotNull(result);
        }

        @Test
        void createPodcast_When422_ThrowsInconsistentPodcastException() throws IOException {
            stubErrorMessage("inconsistent");
            when(restTemplate.postForObject(anyString(), any(), eq(PodcastModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InconsistentPodcastException.class,
                    () -> client.createPodcast(buildPodcast("x")));
        }

        // --- updatePodcast ---

        @Test
        void updatePodcast_WhenSuccess_ReturnsUpdatedPodcast() {
            PodcastModel updated = buildPodcast("POD-001");
// ✅ Resolves ambiguity — tells Java this is the varargs overload
            doNothing().when(restTemplate).put(anyString(), any(), (Object[]) any());
            when(restTemplate.getForObject(anyString(), eq(PodcastModel.class))).thenReturn(updated);

            PodcastModel result = client.updatePodcast("POD-001", updated);

            assertNotNull(result);
        }

        @Test
        void updatePodcast_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            doThrow(makeException(HttpStatus.NOT_FOUND))
                    .when(restTemplate).put(anyString(), any(), (Object[]) any());
            assertThrows(NotFoundException.class,
                    () -> client.updatePodcast("bad-id", buildPodcast("x")));
        }

        // --- deletePodcast ---

        @Test
        void deletePodcast_WhenSuccess_DeletesWithoutError() {
            doNothing().when(restTemplate).delete(anyString());

            assertDoesNotThrow(() -> client.deletePodcast("POD-001"));
        }

        @Test
        void deletePodcast_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("not found");
            doThrow(makeException(HttpStatus.NOT_FOUND)).when(restTemplate).delete(anyString());

            assertThrows(NotFoundException.class, () -> client.deletePodcast("bad-id"));
        }
// --- Episode Tests ---

        @Test
        void getEpisodes_WhenSuccess_ReturnsList() {
            EpisodeModel episode = buildEpisode("EP-001");
            EpisodeModel[] episodes = {episode};

            when(restTemplate.getForObject(anyString(), eq(EpisodeModel[].class))).thenReturn(episodes);

            List<EpisodeModel> result = client.getEpisodes("POD-001");

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("EP-001", result.get(0).getEpisodeId());
        }

        @Test
        void getEpisodeById_WhenSuccess_ReturnsEpisode() {
            EpisodeModel episode = buildEpisode("EP-001");
            when(restTemplate.getForObject(anyString(), eq(EpisodeModel.class))).thenReturn(episode);

            EpisodeModel result = client.getEpisode("POD-001", "EP-001");

            assertNotNull(result);
            // FIX: Removed the .get(0) that I accidentally included last time!
            assertEquals("EP-001", result.getEpisodeId());
        }
        @Test
        void createEpisode_WhenSuccess_ReturnsCreatedEpisode() {
            EpisodeModel request = buildEpisode("EP-001");
            when(restTemplate.postForObject(anyString(), any(), eq(EpisodeModel.class))).thenReturn(request);

            EpisodeModel result = client.createEpisode("POD-001", request);

            assertNotNull(result);
            assertEquals("EP-001", result.getEpisodeId());
        }

        @Test
        void updateEpisode_WhenSuccess_ReturnsUpdatedEpisode() {
            EpisodeModel updated = buildEpisode("EP-001");
            updated.setEpisodeTitle("Updated Title");


            doNothing().when(restTemplate).put(anyString(), any(), (Object[]) any());
            when(restTemplate.getForObject(anyString(), eq(EpisodeModel.class))).thenReturn(updated);

            EpisodeModel result = client.updateEpisode("POD-001", "EP-001", updated);

            assertNotNull(result);
            assertEquals("Updated Title", result.getEpisodeTitle());
        }

        @Test
        void deleteEpisode_WhenSuccess_DeletesWithoutError() {

            doNothing().when(restTemplate).delete(anyString(), (Object[]) any());

            assertDoesNotThrow(() -> client.deleteEpisode("POD-001", "EP-001"));
        }
        @Test
        void getEpisodeById_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage("episode not found");
            when(restTemplate.getForObject(anyString(), eq(EpisodeModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getEpisode("POD-001", "bad-id"));
        }
        // Helper
        private PodcastModel buildPodcast(String id) {
            return PodcastModel.builder().podcastId(id).title("Tech Talk Daily")
                    .hostname("John Host").pricingModel(PodcastPricing.FREE).build();
        }
        // Helper for Episode
        private EpisodeModel buildEpisode(String id) {
            return EpisodeModel.builder()
                    .episodeId(id)
                    .episodeTitle("How to Code")
                    .duration(java.sql.Time.valueOf("00:45:00"))
                    .status(EpisodeStatus.PUBLISHED)
                    .build();
        }
    }
}