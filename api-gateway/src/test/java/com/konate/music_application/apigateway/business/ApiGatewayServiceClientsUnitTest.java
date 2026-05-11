package com.konate.music_application.apigateway.business;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.konate.music_application.apigateway.Exceptions.*;
import com.konate.music_application.apigateway.PresentationLayer.Ad.AdRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Ad.AdResponseModel;
import com.konate.music_application.apigateway.PresentationLayer.Artist.ArtistRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Artist.ArtistResponseModel;
import com.konate.music_application.apigateway.PresentationLayer.Catalog.AlbumRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Catalog.AlbumResponseModel;
import com.konate.music_application.apigateway.PresentationLayer.Order.OrderRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Order.OrderResponseModel;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Episode.EpisodeRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Episode.EpisodeResponseModel;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Podcast.PodcastRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Podcast.Podcast.PodcastResponseModel;
import com.konate.music_application.apigateway.PresentationLayer.User.UserRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.User.UserResponseModel;
import com.konate.music_application.apigateway.domainClientLayer.Ad.AdServiceClient;
import com.konate.music_application.apigateway.domainClientLayer.Artist.ArtistServiceClient;
import com.konate.music_application.apigateway.domainClientLayer.Catalog.AlbumType;
import com.konate.music_application.apigateway.domainClientLayer.Catalog.CatalogServiceClient;
import com.konate.music_application.apigateway.domainClientLayer.Order.OrderServiceClient;
import com.konate.music_application.apigateway.domainClientLayer.Order.OrderStatus;
import com.konate.music_application.apigateway.domainClientLayer.Podcast.PodcastServiceClient;
import com.konate.music_application.apigateway.domainClientLayer.User.UserServiceClient;
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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiGatewayServiceClientsUnitTest {

    @Mock
    RestTemplate restTemplate;
    @Mock
    ObjectMapper  objectMapper;

    // ─── Shared helpers ───────────────────────────────────────────────────────

    private HttpClientErrorException makeException(HttpStatus status) {
        return HttpClientErrorException.create(
                status, status.getReasonPhrase(),
                HttpHeaders.EMPTY,
                "{\"message\":\"error\"}".getBytes(),
                StandardCharsets.UTF_8
        );
    }

    private void stubErrorMessage() throws IOException {
        HttpErrorInfo info = mock(HttpErrorInfo.class);
        when(info.getMessage()).thenReturn("mocked error message");
        when(objectMapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(info);
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

        // --- getAllArtist ---

        @Test
        void getAllArtist_WhenSuccess_ReturnsList() {
            when(restTemplate.getForObject(anyString(), eq(ArtistResponseModel[].class)))
                    .thenReturn(new ArtistResponseModel[]{ buildArtist("ART-001"), buildArtist("ART-002") });

            List<ArtistResponseModel> result = client.getAllArtist();

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        void getAllArtist_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(ArtistResponseModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAllArtist());
        }

        @Test
        void getAllArtist_When422_ThrowsInvalidInputException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(ArtistResponseModel[].class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InvalidInputException.class, () -> client.getAllArtist());
        }

        // --- getArtistById ---

        @Test
        void getArtistById_WhenSuccess_ReturnsArtist() {
            when(restTemplate.getForObject(anyString(), eq(ArtistResponseModel.class)))
                    .thenReturn(buildArtist("ART-001"));

            ArtistResponseModel result = client.getArtistById("ART-001");

            assertNotNull(result);
            assertEquals("ART-001", result.getArtistIdentifier());
        }

        @Test
        void getArtistById_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(ArtistResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getArtistById("bad-id"));
        }

        @Test
        void getArtistById_When422_ThrowsInvalidInputException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(ArtistResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InvalidInputException.class, () -> client.getArtistById("bad-id"));
        }

        @Test
        void getArtistById_WhenUnexpected_RethrowsHttpClientErrorException() {
            when(restTemplate.getForObject(anyString(), eq(ArtistResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.SERVICE_UNAVAILABLE));

            assertThrows(HttpClientErrorException.class, () -> client.getArtistById("id"));
        }

        // --- getArtistByLastName ---

        @Test
        void getArtistByLastName_WhenSuccess_ReturnsArtist() {
            when(restTemplate.getForObject(anyString(), eq(ArtistResponseModel.class)))
                    .thenReturn(buildArtist("ART-001"));

            ArtistResponseModel result = client.getArtistByLastName("Doe");

            assertNotNull(result);
        }

        @Test
        void getArtistByLastName_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(ArtistResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getArtistByLastName("Unknown"));
        }

        // --- createArtist ---

        @Test
        void createArtist_WhenSuccess_ReturnsCreatedArtist() {
            when(restTemplate.postForObject(anyString(), any(), eq(ArtistResponseModel.class)))
                    .thenReturn(buildArtist("ART-new"));

            ArtistResponseModel result = client.createArtist(buildRequest());

            assertNotNull(result);
            verify(restTemplate).postForObject(anyString(), any(ArtistRequestModel.class),
                    eq(ArtistResponseModel.class));
        }

        @Test
        void createArtist_When422_ThrowsInvalidInputException() throws IOException {
            stubErrorMessage();
            when(restTemplate.postForObject(anyString(), any(), eq(ArtistResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InvalidInputException.class, () -> client.createArtist(buildRequest()));
        }

        // --- updateArtist ---

        @Test
        void updateArtist_WhenSuccess_ReturnsUpdatedArtist() {
            // put is void; then getArtistById is called internally
            doNothing().when(restTemplate).put(anyString(), any(), (Object[]) any());
            when(restTemplate.getForObject(anyString(), eq(ArtistResponseModel.class)))
                    .thenReturn(buildArtist("ART-001"));

            ArtistResponseModel result = client.updateArtist("ART-001", buildRequest());

            assertNotNull(result);
        }

        @Test
        void updateArtist_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.NOT_FOUND))
                    .when(restTemplate).put(anyString(), any(), (Object[]) any());

            assertThrows(NotFoundException.class,
                    () -> client.updateArtist("bad-id", buildRequest()));
        }

        @Test
        void updateArtist_When422_ThrowsInvalidInputException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY))
                    .when(restTemplate).put(anyString(), any(), (Object[]) any());

            assertThrows(InvalidInputException.class,
                    () -> client.updateArtist("bad-id", buildRequest()));
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
            stubErrorMessage();
            doThrow(makeException(HttpStatus.NOT_FOUND)).when(restTemplate).delete(anyString());

            assertThrows(NotFoundException.class, () -> client.deleteArtist("bad-id"));
        }

        // Helpers
        private ArtistResponseModel buildArtist(String id) {
            ArtistResponseModel r = new ArtistResponseModel();
            r.setArtistIdentifier(id);
            r.setFirstName("John");
            r.setLastName("Doe");
            return r;
        }

        private ArtistRequestModel buildRequest() {
            return ArtistRequestModel.builder()
                    .firstName("John").lastName("Doe").biography("Bio").build();
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
            when(restTemplate.getForObject(anyString(), eq(AlbumResponseModel[].class)))
                    .thenReturn(new AlbumResponseModel[]{ buildAlbum("ALB-001"), buildAlbum("ALB-002") });

            List<AlbumResponseModel> result = client.getAllAlbums();

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        void getAllAlbums_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(AlbumResponseModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAllAlbums());
        }

        // --- getAlbum ---

        @Test
        void getAlbum_WhenSuccess_ReturnsAlbum() {
            when(restTemplate.getForObject(anyString(), eq(AlbumResponseModel.class)))
                    .thenReturn(buildAlbum("ALB-001"));

            AlbumResponseModel result = client.getAlbum("ALB-001");

            assertNotNull(result);
            assertEquals("ALB-001", result.getAlbumId());
        }

        @Test
        void getAlbum_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(AlbumResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAlbum("bad-id"));
        }

        @Test
        void getAlbum_When422_ThrowsInconsistentAlbumException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(AlbumResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InconsistentAlbumException.class, () -> client.getAlbum("bad-id"));
        }

        @Test
        void getAlbum_WhenUnexpected_RethrowsHttpClientErrorException() {
            when(restTemplate.getForObject(anyString(), eq(AlbumResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.SERVICE_UNAVAILABLE));

            assertThrows(HttpClientErrorException.class, () -> client.getAlbum("id"));
        }

        // --- createAlbum ---

        @Test
        void createAlbum_WhenSuccess_ReturnsCreatedAlbum() {
            when(restTemplate.postForObject(anyString(), any(), eq(AlbumResponseModel.class)))
                    .thenReturn(buildAlbum("ALB-new"));

            AlbumResponseModel result = client.createAlbum(buildRequest());

            assertNotNull(result);
        }

        @Test
        void createAlbum_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.postForObject(anyString(), any(), eq(AlbumResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.createAlbum(buildRequest()));
        }

        @Test
        void createAlbum_When422_ThrowsInconsistentAlbumException() throws IOException {
            stubErrorMessage();
            when(restTemplate.postForObject(anyString(), any(), eq(AlbumResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InconsistentAlbumException.class, () -> client.createAlbum(buildRequest()));
        }

        // --- updateAlbum ---

        @Test
        void updateAlbum_WhenSuccess_ReturnsUpdatedAlbum() {
            doNothing().when(restTemplate).put(anyString(), any(), (Object[]) any());
            when(restTemplate.getForObject(anyString(), eq(AlbumResponseModel.class)))
                    .thenReturn(buildAlbum("ALB-001"));

            AlbumResponseModel result = client.updateAlbum("ALB-001", buildRequest());

            assertNotNull(result);
        }

        @Test
        void updateAlbum_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.NOT_FOUND))
                    .when(restTemplate).put(anyString(), any(), (Object[]) any());

            assertThrows(NotFoundException.class,
                    () -> client.updateAlbum("bad-id", buildRequest()));
        }

        @Test
        void updateAlbum_When422_ThrowsInconsistentAlbumException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY))
                    .when(restTemplate).put(anyString(), any(), (Object[]) any());

            assertThrows(InconsistentAlbumException.class,
                    () -> client.updateAlbum("bad-id", buildRequest()));
        }

        // --- deleteAlbum ---

        @Test
        void deleteAlbum_WhenSuccess_DeletesWithoutError() {
            doNothing().when(restTemplate).delete(anyString());

            assertDoesNotThrow(() -> client.deleteAlbum("ALB-001"));
        }

        @Test
        void deleteAlbum_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.NOT_FOUND)).when(restTemplate).delete(anyString());

            assertThrows(NotFoundException.class, () -> client.deleteAlbum("bad-id"));
        }

        // Helpers
        private AlbumResponseModel buildAlbum(String id) {
            AlbumResponseModel r = new AlbumResponseModel();
            r.setAlbumId(id);
            r.setTitle("Cowboy Sunset");
            r.setAlbumType(AlbumType.LP);
            return r;
        }

        private AlbumRequestModel buildRequest() {
            return new AlbumRequestModel("Cowboy Sunset", "ART-001", null, AlbumType.LP, "Records", List.of());
        }
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

        // --- getAllUsers ---

        @Test
        void getAllUsers_WhenSuccess_ReturnsList() {
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel[].class)))
                    .thenReturn(new UserResponseModel[]{ buildUser("user-001"), buildUser("user-002") });

            List<UserResponseModel> result = client.getAllUsers();

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        void getAllUsers_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAllUsers());
        }

        // --- getUserById ---

        @Test
        void getUserById_WhenSuccess_ReturnsUser() {
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel.class)))
                    .thenReturn(buildUser("user-001"));

            UserResponseModel result = client.getUserById("user-001");

            assertNotNull(result);
            assertEquals("user-001", result.getUserId());
        }

        @Test
        void getUserById_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getUserById("bad-id"));
        }

        @Test
        void getUserById_When422_ThrowsInvalidInputException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InvalidInputException.class, () -> client.getUserById("bad-id"));
        }

        @Test
        void getUserById_When409_ThrowsUserFound() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.CONFLICT));

            assertThrows(UserFound.class, () -> client.getUserById("dup-id"));
        }

        @Test
        void getUserById_WhenUnexpected_RethrowsHttpClientErrorException() {
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.SERVICE_UNAVAILABLE));

            assertThrows(HttpClientErrorException.class, () -> client.getUserById("id"));
        }

        // --- getUserByUsername ---

        @Test
        void getUserByUsername_WhenSuccess_ReturnsUser() {
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel.class)))
                    .thenReturn(buildUser("user-001"));

            UserResponseModel result = client.getUserByUsername("malick");

            assertNotNull(result);
        }

        @Test
        void getUserByUsername_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getUserByUsername("ghost"));
        }

        // --- getUserByEmail ---

        @Test
        void getUserByEmail_WhenSuccess_ReturnsUser() {
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel.class)))
                    .thenReturn(buildUser("user-001"));

            UserResponseModel result = client.getUserByEmail("malick@email.com");

            assertNotNull(result);
        }

        @Test
        void getUserByEmail_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getUserByEmail("bad@email.com"));
        }

        @Test
        void getUserByEmail_When409_ThrowsUserFound() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.CONFLICT));

            assertThrows(UserFound.class, () -> client.getUserByEmail("dup@email.com"));
        }

        // --- createUser ---

        @Test
        void createUser_WhenSuccess_ReturnsCreatedUser() {
            when(restTemplate.postForObject(anyString(), any(), eq(UserResponseModel.class)))
                    .thenReturn(buildUser("user-new"));

            UserResponseModel result = client.createUser(buildRequest());

            assertNotNull(result);
        }

        @Test
        void createUser_When409_ThrowsUserFound() throws IOException {
            stubErrorMessage();
            when(restTemplate.postForObject(anyString(), any(), eq(UserResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.CONFLICT));

            assertThrows(UserFound.class, () -> client.createUser(buildRequest()));
        }

        @Test
        void createUser_When422_ThrowsInvalidInputException() throws IOException {
            stubErrorMessage();
            when(restTemplate.postForObject(anyString(), any(), eq(UserResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InvalidInputException.class, () -> client.createUser(buildRequest()));
        }

        // --- updateUser ---

        @Test
        void updateUser_WhenSuccess_ReturnsUpdatedUser() {
            doNothing().when(restTemplate).put(anyString(), any(), (Object[]) any());
            when(restTemplate.getForObject(anyString(), eq(UserResponseModel.class)))
                    .thenReturn(buildUser("user-001"));

            UserResponseModel result = client.updateUser("malick", buildRequest());

            assertNotNull(result);
        }

        @Test
        void updateUser_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.NOT_FOUND))
                    .when(restTemplate).put(anyString(), any(), (Object[]) any());

            assertThrows(NotFoundException.class, () -> client.updateUser("ghost", buildRequest()));
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
            stubErrorMessage();
            doThrow(makeException(HttpStatus.NOT_FOUND)).when(restTemplate).delete(anyString());

            assertThrows(NotFoundException.class, () -> client.deleteUser("ghost"));
        }

        // Helpers
        private UserResponseModel buildUser(String id) {
            UserResponseModel r = new UserResponseModel();
            r.setUserId(id);
            r.setUsername("malick");
            r.setEmail("malick@email.com");
            r.setCountry("Canada");
            return r;
        }

        private UserRequestModel buildRequest() {
            return UserRequestModel.builder()
                    .username("malick").email("malick@email.com")
                    .password("secret").fullname("Malick Konate")
                    .age(25).country("Canada").build();
        }
    }

    // =========================================================================
    // OrderServiceClient
    // NOTE: 400 → InvalidOrderStateException, 409 → OrderConflictException
    //       (no 422 mapped in this client)
    // =========================================================================

    @Nested
    class OrderServiceClientTest {

        OrderServiceClient client;

        @BeforeEach
        void setUp() {
            client = new OrderServiceClient(restTemplate, objectMapper, "localhost", "7005");
        }

        // --- getAllOrder ---

        @Test
        void getAllOrder_WhenSuccess_ReturnsList() {
            when(restTemplate.getForObject(anyString(), eq(OrderResponseModel[].class)))
                    .thenReturn(new OrderResponseModel[]{ buildOrder("ord-001"), buildOrder("ord-002") });

            List<OrderResponseModel> result = client.getAllOrder();

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        void getAllOrder_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(OrderResponseModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAllOrder());
        }

        // --- getOrderById ---

        @Test
        void getOrderById_WhenSuccess_ReturnsOrder() {
            when(restTemplate.getForObject(anyString(), eq(OrderResponseModel.class)))
                    .thenReturn(buildOrder("ord-001"));

            OrderResponseModel result = client.getOrderById("ord-001");

            assertNotNull(result);
            assertEquals("ord-001", result.getOrderId());
        }

        @Test
        void getOrderById_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(OrderResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getOrderById("bad-id"));
        }

        @Test
        void getOrderById_When409_ThrowsOrderConflictException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(OrderResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.CONFLICT));

            assertThrows(OrderConflictException.class, () -> client.getOrderById("id"));
        }

        @Test
        void getOrderById_WhenUnexpected_RethrowsHttpClientErrorException() {
            when(restTemplate.getForObject(anyString(), eq(OrderResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.SERVICE_UNAVAILABLE));

            assertThrows(HttpClientErrorException.class, () -> client.getOrderById("id"));
        }

        // --- createOrder ---

        @Test
        void createOrder_WhenSuccess_ReturnsCreatedOrder() {
            when(restTemplate.postForObject(anyString(), any(), eq(OrderResponseModel.class)))
                    .thenReturn(buildOrder("ord-new"));

            OrderResponseModel result = client.createOrder(buildRequest());

            assertNotNull(result);
        }

        @Test
        void createOrder_When400_ThrowsInvalidOrderStateException() throws IOException {
            stubErrorMessage();
            when(restTemplate.postForObject(anyString(), any(), eq(OrderResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.BAD_REQUEST));

            assertThrows(InvalidOrderStateException.class, () -> client.createOrder(buildRequest()));
        }

        @Test
        void createOrder_When409_ThrowsOrderConflictException() throws IOException {
            stubErrorMessage();
            when(restTemplate.postForObject(anyString(), any(), eq(OrderResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.CONFLICT));

            assertThrows(OrderConflictException.class, () -> client.createOrder(buildRequest()));
        }

        @Test
        void createOrder_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.postForObject(anyString(), any(), eq(OrderResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.createOrder(buildRequest()));
        }

        // --- updateOrder ---

        @Test
        void updateOrder_WhenSuccess_ReturnsUpdatedOrder() {
            // put is void; then getOrderById is called internally
            doNothing().when(restTemplate).put(anyString(), any());
            when(restTemplate.getForObject(anyString(), eq(OrderResponseModel.class)))
                    .thenReturn(buildOrder("ord-001"));

            OrderResponseModel result = client.updateOrder("ord-001", buildRequest());

            assertNotNull(result);
        }

        @Test
        void updateOrder_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.NOT_FOUND))
                    .when(restTemplate).put(anyString(), any());

            assertThrows(NotFoundException.class,
                    () -> client.updateOrder("bad-id", buildRequest()));
        }

        @Test
        void updateOrder_When409_ThrowsOrderConflictException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.CONFLICT))
                    .when(restTemplate).put(anyString(), any());

            assertThrows(OrderConflictException.class,
                    () -> client.updateOrder("bad-id", buildRequest()));
        }

        // --- cancelOrder ---

        @Test
        void cancelOrder_WhenSuccess_DeletesWithoutError() {
            doNothing().when(restTemplate).delete(anyString());

            assertDoesNotThrow(() -> client.cancelOrder("ord-001"));
            verify(restTemplate).delete(anyString());
        }

        @Test
        void cancelOrder_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.NOT_FOUND)).when(restTemplate).delete(anyString());

            assertThrows(NotFoundException.class, () -> client.cancelOrder("bad-id"));
        }

        // --- getOrdersByCustomerId ---

        @Test
        void getOrdersByCustomerId_WhenSuccess_ReturnsList() {
            when(restTemplate.getForObject(anyString(), eq(OrderResponseModel[].class)))
                    .thenReturn(new OrderResponseModel[]{ buildOrder("ord-001") });

            List<OrderResponseModel> result = client.getOrdersByCustomerId("user-001");

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void getOrdersByCustomerId_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(OrderResponseModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getOrdersByCustomerId("bad-user"));
        }

        // Helpers
        private OrderResponseModel buildOrder(String id) {
            OrderResponseModel r = new OrderResponseModel();
            r.setOrderId(id);
            r.setOrderStatus(OrderStatus.PENDING);
            r.setEmail("malick@email.com");
            return r;
        }

        private OrderRequestModel buildRequest() {
            return new OrderRequestModel("malick@email.com", OrderStatus.PENDING, List.of(), List.of());
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
            when(restTemplate.getForObject(anyString(), eq(PodcastResponseModel[].class)))
                    .thenReturn(new PodcastResponseModel[]{ buildPodcast("POD-001"), buildPodcast("POD-002") });

            List<PodcastResponseModel> result = client.getAllPodcasts();

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        void getAllPodcasts_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(PodcastResponseModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAllPodcasts());
        }

        // --- getPodcastById ---

        @Test
        void getPodcastById_WhenSuccess_ReturnsPodcast() {
            when(restTemplate.getForObject(anyString(), eq(PodcastResponseModel.class)))
                    .thenReturn(buildPodcast("POD-001"));

            PodcastResponseModel result = client.getPodcastById("POD-001");

            assertNotNull(result);
            assertEquals("POD-001", result.getPodcastId());
        }

        @Test
        void getPodcastById_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(PodcastResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getPodcastById("bad-id"));
        }

        @Test
        void getPodcastById_When422_ThrowsInconsistentPodcastException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(PodcastResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InconsistentPodcastException.class, () -> client.getPodcastById("bad-id"));
        }

        @Test
        void getPodcastById_WhenUnexpected_RethrowsHttpClientErrorException() {
            when(restTemplate.getForObject(anyString(), eq(PodcastResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.SERVICE_UNAVAILABLE));

            assertThrows(HttpClientErrorException.class, () -> client.getPodcastById("id"));
        }

        // --- getPodcastByHostname ---

        @Test
        void getPodcastByHostname_WhenSuccess_ReturnsList() {
            when(restTemplate.getForObject(anyString(), eq(PodcastResponseModel[].class)))
                    .thenReturn(new PodcastResponseModel[]{ buildPodcast("POD-001") });

            List<PodcastResponseModel> result = client.getPodcastByHostname("John Host");

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void getPodcastByHostname_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(PodcastResponseModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getPodcastByHostname("Unknown"));
        }

        // --- createPodcast ---

        @Test
        void createPodcast_WhenSuccess_ReturnsCreatedPodcast() {
            when(restTemplate.postForObject(anyString(), any(), eq(PodcastResponseModel.class)))
                    .thenReturn(buildPodcast("POD-new"));

            PodcastResponseModel result = client.createPodcast(buildPodcastRequest());

            assertNotNull(result);
        }

        @Test
        void createPodcast_When422_ThrowsInconsistentPodcastException() throws IOException {
            stubErrorMessage();
            when(restTemplate.postForObject(anyString(), any(), eq(PodcastResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InconsistentPodcastException.class,
                    () -> client.createPodcast(buildPodcastRequest()));
        }

        // --- updatePodcast ---

        @Test
        void updatePodcast_WhenSuccess_ReturnsUpdatedPodcast() {
            doNothing().when(restTemplate).put(anyString(), any(), (Object[]) any());
            when(restTemplate.getForObject(anyString(), eq(PodcastResponseModel.class)))
                    .thenReturn(buildPodcast("POD-001"));

            PodcastResponseModel result = client.updatePodcast("POD-001", buildPodcastRequest());

            assertNotNull(result);
        }

        @Test
        void updatePodcast_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.NOT_FOUND))
                    .when(restTemplate).put(anyString(), any(), (Object[]) any());

            assertThrows(NotFoundException.class,
                    () -> client.updatePodcast("bad-id", buildPodcastRequest()));
        }

        // --- deletePodcast ---

        @Test
        void deletePodcast_WhenSuccess_DeletesWithoutError() {
            doNothing().when(restTemplate).delete(anyString());

            assertDoesNotThrow(() -> client.deletePodcast("POD-001"));
        }

        @Test
        void deletePodcast_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.NOT_FOUND)).when(restTemplate).delete(anyString());

            assertThrows(NotFoundException.class, () -> client.deletePodcast("bad-id"));
        }

        // --- createEpisode ---

        @Test
        void createEpisode_WhenSuccess_ReturnsCreatedEpisode() {
            when(restTemplate.postForObject(anyString(), any(), eq(EpisodeResponseModel.class)))
                    .thenReturn(buildEpisode("EP-001"));

            EpisodeResponseModel result = client.createEpisode("POD-001", buildEpisodeRequest());

            assertNotNull(result);
            assertEquals("EP-001", result.getEpisodeId());
        }

        @Test
        void createEpisode_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.postForObject(anyString(), any(), eq(EpisodeResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class,
                    () -> client.createEpisode("bad-pod", buildEpisodeRequest()));
        }

        @Test
        void createEpisode_When422_ThrowsInconsistentPodcastException() throws IOException {
            stubErrorMessage();
            when(restTemplate.postForObject(anyString(), any(), eq(EpisodeResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InconsistentPodcastException.class,
                    () -> client.createEpisode("POD-001", buildEpisodeRequest()));
        }

        // --- getEpisode ---

        @Test
        void getEpisode_WhenSuccess_ReturnsEpisode() {
            when(restTemplate.getForObject(anyString(), eq(EpisodeResponseModel.class)))
                    .thenReturn(buildEpisode("EP-001"));

            EpisodeResponseModel result = client.getEpisode("POD-001", "EP-001");

            assertNotNull(result);
        }

        @Test
        void getEpisode_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(EpisodeResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getEpisode("POD-001", "bad-ep"));
        }

        // --- getEpisodes ---

        @Test
        void getEpisodes_WhenSuccess_ReturnsList() {
            when(restTemplate.getForObject(anyString(), eq(EpisodeResponseModel[].class)))
                    .thenReturn(new EpisodeResponseModel[]{ buildEpisode("EP-001") });

            List<EpisodeResponseModel> result = client.getEpisodes("POD-001");

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void getEpisodes_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(EpisodeResponseModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getEpisodes("bad-pod"));
        }

        // --- deleteEpisode ---

        @Test
        void deleteEpisode_WhenSuccess_DeletesWithoutError() {
            doNothing().when(restTemplate).delete(anyString(), eq(EpisodeResponseModel.class));

            assertDoesNotThrow(() -> client.deleteEpisode("POD-001", "EP-001"));
        }

        @Test
        void deleteEpisode_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.NOT_FOUND))
                    .when(restTemplate).delete(anyString(), eq(EpisodeResponseModel.class));

            assertThrows(NotFoundException.class,
                    () -> client.deleteEpisode("POD-001", "bad-ep"));
        }
// --- updateEpisode ---

        @Test
        void updateEpisode_WhenSuccess_ReturnsUpdatedEpisode() {
            // Note: Your client calls put() and then patchForObject()
            doNothing().when(restTemplate).put(anyString(), any(), (Object[]) any());
            when(restTemplate.patchForObject(anyString(), any(), eq(EpisodeResponseModel.class)))
                    .thenReturn(buildEpisode("EP-001"));

            EpisodeResponseModel result = client.updateEpisode("POD-001", "EP-001", buildEpisodeRequest());

            assertNotNull(result);
            assertEquals("EP-001", result.getEpisodeId());
        }

        @Test
        void updateEpisode_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            // The exception is thrown on the first call, which is put()
            doThrow(makeException(HttpStatus.NOT_FOUND))
                    .when(restTemplate).put(anyString(), any(), (Object[]) any());

            assertThrows(NotFoundException.class,
                    () -> client.updateEpisode("POD-001", "bad-ep", buildEpisodeRequest()));
        }

        @Test
        void updateEpisode_When422_ThrowsInconsistentPodcastException() throws IOException {
            stubErrorMessage();
            doThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY))
                    .when(restTemplate).put(anyString(), any(), (Object[]) any());

            assertThrows(InconsistentPodcastException.class,
                    () -> client.updateEpisode("POD-001", "bad-ep", buildEpisodeRequest()));
        }
        // Helpers
        private PodcastResponseModel buildPodcast(String id) {
            PodcastResponseModel r = new PodcastResponseModel();
            r.setPodcastId(id);
            r.setTitle("Tech Talk Daily");
            r.setHostname("John Host");
            return r;
        }

        private EpisodeResponseModel buildEpisode(String id) {
            EpisodeResponseModel r = new EpisodeResponseModel();
            r.setEpisodeId(id);
            r.setEpisodeTitle("Episode 1");
            return r;
        }

        private PodcastRequestModel buildPodcastRequest() {
            return new PodcastRequestModel("Tech Talk Daily", "John Host", "A tech podcast", null);
        }

        private EpisodeRequestModel buildEpisodeRequest() {
            return new EpisodeRequestModel("Episode 1", null, null, null);
        }
    }


    // =========================================================================
    // AdServiceClient
    // =========================================================================

    @Nested
    class AdServiceClientTest {

        AdServiceClient client;

        @BeforeEach
        void setUp() {
            client = new AdServiceClient(restTemplate, objectMapper, "localhost", "7006");
        }

        // --- getAllAds ---

        @Test
        void getAllAds_WhenSuccess_ReturnsList() {
            when(restTemplate.getForObject(anyString(), eq(AdResponseModel[].class)))
                    .thenReturn(new AdResponseModel[]{ buildAdResponse("AD-001"), buildAdResponse("AD-002") });

            List<AdResponseModel> result = client.getAllAds();

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        void getAllAds_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(AdResponseModel[].class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAllAds());
        }

        // --- getAdById ---

        @Test
        void getAdById_WhenSuccess_ReturnsAd() {
            when(restTemplate.getForObject(anyString(), eq(AdResponseModel.class)))
                    .thenReturn(buildAdResponse("AD-001"));

            AdResponseModel result = client.getAdById("AD-001");

            assertNotNull(result);
        }

        @Test
        void getAdById_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(AdResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.NOT_FOUND));

            assertThrows(NotFoundException.class, () -> client.getAdById("bad-id"));
        }

        @Test
        void getAdById_When422_ThrowsInvalidAdTargetException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(AdResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InvalidAdTargetException.class, () -> client.getAdById("bad-id"));
        }

        @Test
        void getAdById_When409_ThrowsInvalidCampaignStateException() throws IOException {
            stubErrorMessage();
            when(restTemplate.getForObject(anyString(), eq(AdResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.CONFLICT));

            assertThrows(InvalidCampaignStateException.class, () -> client.getAdById("id"));
        }

        // --- createAd ---

        @Test
        void createAd_WhenSuccess_ReturnsCreatedAd() {
            when(restTemplate.postForObject(anyString(), any(), eq(AdResponseModel.class)))
                    .thenReturn(buildAdResponse("AD-new"));

            AdResponseModel result = client.createAd(buildAdRequest());

            assertNotNull(result);
        }

        @Test
        void createAd_When422_ThrowsInvalidAdTargetException() throws IOException {
            stubErrorMessage();
            when(restTemplate.postForObject(anyString(), any(), eq(AdResponseModel.class)))
                    .thenThrow(makeException(HttpStatus.UNPROCESSABLE_ENTITY));

            assertThrows(InvalidAdTargetException.class, () -> client.createAd(buildAdRequest()));
        }

        // --- updateAd ---

        @Test
        void updateAd_WhenSuccess_ReturnsUpdatedAd() {
            // Removed the (Object[]) any() vararg matcher
            doNothing().when(restTemplate).put(anyString(), any());

            when(restTemplate.getForObject(anyString(), eq(AdResponseModel.class)))
                    .thenReturn(buildAdResponse("AD-001"));

            AdResponseModel result = client.updateAd("AD-001", buildAdRequest());

            assertNotNull(result);
            verify(restTemplate).put(anyString(), any());
        }

        @Test
        void updateAd_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();

            // Removed the (Object[]) any() vararg matcher
            doThrow(makeException(HttpStatus.NOT_FOUND))
                    .when(restTemplate).put(anyString(), any());

            assertThrows(NotFoundException.class,
                    () -> client.updateAd("bad-id", buildAdRequest()));
        }

        // --- deleteAd ---

        @Test
        void deleteAd_WhenSuccess_DeletesWithoutError() {
            // Removed the (Object[]) any() vararg matcher
            doNothing().when(restTemplate).delete(anyString());

            assertDoesNotThrow(() -> client.deleteAd("AD-001"));
            verify(restTemplate).delete(anyString()); // Now matches the stub perfectly
        }

        @Test
        void deleteAd_When404_ThrowsNotFoundException() throws IOException {
            stubErrorMessage();

            // Removed the (Object[]) any() vararg matcher
            doThrow(makeException(HttpStatus.NOT_FOUND)).when(restTemplate).delete(anyString());

            assertThrows(NotFoundException.class, () -> client.deleteAd("bad-id"));
        }
        // --- activateAd ---

        @Test
        void activateAd_WhenSuccess_DoesNotThrow() {
            when(restTemplate.patchForObject(anyString(), isNull(), eq(Void.class)))
                    .thenReturn(null);

            assertDoesNotThrow(() -> client.activateAd("AD-001"));
        }

        @Test
        void activateAd_When409_ThrowsInvalidCampaignStateException() throws IOException {
            stubErrorMessage();
            when(restTemplate.patchForObject(anyString(), isNull(), eq(Void.class)))
                    .thenThrow(makeException(HttpStatus.CONFLICT));

            assertThrows(InvalidCampaignStateException.class, () -> client.activateAd("bad-id"));
        }

        // --- pauseAd ---

        @Test
        void pauseAd_WhenSuccess_DoesNotThrow() {
            when(restTemplate.patchForObject(anyString(), isNull(), eq(Void.class)))
                    .thenReturn(null);

            assertDoesNotThrow(() -> client.pauseAd("AD-001"));
        }

        // --- resumeAd ---

        @Test
        void resumeAd_WhenSuccess_DoesNotThrow() {
            when(restTemplate.patchForObject(anyString(), isNull(), eq(Void.class)))
                    .thenReturn(null);

            assertDoesNotThrow(() -> client.resumeAd("AD-001"));
        }

        // Helpers
        private AdResponseModel buildAdResponse(String id) {
            AdResponseModel response = new AdResponseModel();
            // Assuming your response model has a setter for the ID
            // response.setAdId(id);
            return response;
        }

        private AdRequestModel buildAdRequest() {
            return new AdRequestModel();
        }
    }
}