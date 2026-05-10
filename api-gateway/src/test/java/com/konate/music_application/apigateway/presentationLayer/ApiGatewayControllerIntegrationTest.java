package com.konate.music_application.apigateway.presentationLayer;


import com.konate.music_application.apigateway.BusinessLayer.Artist.ArtistService;
import com.konate.music_application.apigateway.BusinessLayer.Catalog.AlbumService;
import com.konate.music_application.apigateway.BusinessLayer.Order.OrderService;
import com.konate.music_application.apigateway.BusinessLayer.User.UserService;
import com.konate.music_application.apigateway.BusinessLayer.ad.AdCampaignService;
import com.konate.music_application.apigateway.Exceptions.NotFoundException;
import com.konate.music_application.apigateway.PresentationLayer.Ad.AdRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Ad.AdResponseModel;
import com.konate.music_application.apigateway.PresentationLayer.Artist.ArtistRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Artist.ArtistResponseModel;
import com.konate.music_application.apigateway.PresentationLayer.Catalog.AlbumRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Catalog.AlbumResponseModel;
import com.konate.music_application.apigateway.PresentationLayer.Order.OrderRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Order.OrderResponseModel;
import com.konate.music_application.apigateway.PresentationLayer.User.UserRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.User.UserResponseModel;
import com.konate.music_application.apigateway.domainClientLayer.Order.OrderStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = {
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        }
)
@ActiveProfiles("test")
public class ApiGatewayControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;


    @MockitoBean
    ArtistService artistService;
    @MockitoBean
    AlbumService albumService;
    @MockitoBean
    OrderService orderService;
    @MockitoBean
    UserService userService;
    @MockitoBean
    AdCampaignService adCampaignService;

    // =========================================================================
    // ArtistController — /api/v1/artists
    // =========================================================================

    @Nested
    class ArtistControllerTest {

        final String BASE = "/api/v1/artists";
        final String VALID_ID = "ART-001";
        final String INVALID_ID = "non-existent";

        ArtistResponseModel buildArtist(String id) {
            ArtistResponseModel r = new ArtistResponseModel();
            r.setArtistIdentifier(id);
            r.setFirstName("John");
            r.setLastName("Doe");
            return r;
        }

        ArtistRequestModel buildRequest() {
            return ArtistRequestModel.builder()
                    .firstName("John").lastName("Doe").biography("Bio").build();
        }

        @Test
        void getAllArtists_WhenExist_ReturnsOk() {
            when(artistService.getAllArtists()).thenReturn(List.of(buildArtist(VALID_ID)));

            webTestClient.get().uri(BASE).accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(ArtistResponseModel.class)
                    .value(list -> {
                        assertFalse(list.isEmpty());
                        assertEquals(VALID_ID, list.get(0).getArtistIdentifier());
                    });
        }

        @Test
        void getAllArtists_WhenNone_ReturnsEmptyList() {
            when(artistService.getAllArtists()).thenReturn(List.of());

            webTestClient.get().uri(BASE).accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(ArtistResponseModel.class)
                    .value(list -> assertTrue(list.isEmpty()));
        }

        @Test
        void getArtistById_WhenValid_ReturnsArtist() {
            when(artistService.getArtistById(VALID_ID)).thenReturn(buildArtist(VALID_ID));

            webTestClient.get().uri(BASE + "/" + VALID_ID)
                    .accept(MediaType.APPLICATION_JSON).exchange()
                    .expectStatus().isOk()
                    .expectBody(ArtistResponseModel.class)
                    .value(a -> assertEquals(VALID_ID, a.getArtistIdentifier()));
        }

        @Test
        void getArtistById_WhenNotFound_Returns404() {
            when(artistService.getArtistById(INVALID_ID))
                    .thenThrow(new NotFoundException("Artist not found"));

            webTestClient.get().uri(BASE + "/" + INVALID_ID).exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void createArtist_WhenValid_ReturnsCreated() {
            when(artistService.createArtist(any())).thenReturn(buildArtist(VALID_ID));

            webTestClient.post().uri(BASE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isCreated()
                    .expectBody(ArtistResponseModel.class)
                    .value(a -> assertNotNull(a.getArtistIdentifier()));
        }

        @Test
        void createArtist_WhenMalformedJson_Returns4xx() {
            webTestClient.post().uri(BASE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("not-json").exchange()
                    .expectStatus().is4xxClientError();
        }

        @Test
        void updateArtist_WhenValid_ReturnsOk() {
            when(artistService.updateArtist(eq(VALID_ID), any())).thenReturn(buildArtist(VALID_ID));

            webTestClient.put().uri(BASE + "/" + VALID_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isOk();
        }

        @Test
        void updateArtist_WhenNotFound_Returns404() {
            when(artistService.updateArtist(eq(INVALID_ID), any()))
                    .thenThrow(new NotFoundException("not found"));

            webTestClient.put().uri(BASE + "/" + INVALID_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void deleteArtist_WhenValid_ReturnsNoContent() {
            doNothing().when(artistService).deleteArtist(VALID_ID);

            webTestClient.delete().uri(BASE + "/" + VALID_ID).exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        void deleteArtist_WhenNotFound_Returns404() {
            doThrow(new NotFoundException("not found")).when(artistService).deleteArtist(INVALID_ID);

            webTestClient.delete().uri(BASE + "/" + INVALID_ID).exchange()
                    .expectStatus().isNotFound();
        }
    }

    // =========================================================================
    // CatalogController — /api/v1/album
    // =========================================================================

    @Nested
    class CatalogControllerTest {

        final String BASE = "/api/v1/album";
        final String VALID_ID = "ALB-001";
        final String INVALID_ID = "non-existent";

        AlbumResponseModel buildAlbum(String id) {
            AlbumResponseModel r = new AlbumResponseModel();
            r.setAlbumId(id);
            r.setTitle("Cowboy Sunset");
            r.setArtistFirstName("Reba");
            r.setArtistLastName("McEntire");
            return r;
        }

        AlbumRequestModel buildRequest() {
            return new AlbumRequestModel("Cowboy Sunset", "ART-001",
                    null, null, "Gold Records", List.of());
        }

        @Test
        void getAllAlbums_WhenExist_ReturnsOk() {
            when(albumService.getAllAlbums()).thenReturn(List.of(buildAlbum(VALID_ID)));

            webTestClient.get().uri(BASE).accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(AlbumResponseModel.class)
                    .value(list -> assertFalse(list.isEmpty()));
        }

        @Test
        void getAlbumById_WhenValid_ReturnsAlbum() {
            when(albumService.getAlbumById(VALID_ID)).thenReturn(buildAlbum(VALID_ID));

            webTestClient.get().uri(BASE + "/" + VALID_ID)
                    .accept(MediaType.APPLICATION_JSON).exchange()
                    .expectStatus().isOk()
                    .expectBody(AlbumResponseModel.class)
                    .value(a -> assertEquals(VALID_ID, a.getAlbumId()));
        }

        @Test
        void getAlbumById_WhenNotFound_Returns404() {
            when(albumService.getAlbumById(INVALID_ID))
                    .thenThrow(new NotFoundException("Album not found"));

            webTestClient.get().uri(BASE + "/" + INVALID_ID).exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void createAlbum_WhenValid_ReturnsCreated() {
            when(albumService.createAlbum(any())).thenReturn(buildAlbum(VALID_ID));

            webTestClient.post().uri(BASE + "/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isCreated();
        }

        @Test
        void createAlbum_WhenMalformedJson_Returns4xx() {
            webTestClient.post().uri(BASE + "/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("bad-json").exchange()
                    .expectStatus().is4xxClientError();
        }

        @Test
        void updateAlbum_WhenValid_ReturnsOk() {
            when(albumService.updateAlbum(eq(VALID_ID), any())).thenReturn(buildAlbum(VALID_ID));

            webTestClient.put().uri(BASE + "/update/" + VALID_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isOk();
        }

        @Test
        void updateAlbum_WhenNotFound_Returns404() {
            when(albumService.updateAlbum(eq(INVALID_ID), any()))
                    .thenThrow(new NotFoundException("not found"));

            webTestClient.put().uri(BASE + "/update/" + INVALID_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void deleteAlbum_WhenValid_ReturnsNoContent() {
            doNothing().when(albumService).deleteAlbum(VALID_ID);

            webTestClient.delete().uri(BASE + "/delete/" + VALID_ID).exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        void deleteAlbum_WhenNotFound_Returns404() {
            doThrow(new NotFoundException("not found")).when(albumService).deleteAlbum(INVALID_ID);

            webTestClient.delete().uri(BASE + "/delete/" + INVALID_ID).exchange()
                    .expectStatus().isNotFound();
        }
    }

    // =========================================================================
    // UserController — /api/v1/users
    // =========================================================================

    @Nested
    class UserControllerTest {

        final String BASE = "/api/v1/users";
        final String VALID_ID = "user-001";
        final String VALID_USERNAME = "malick";
        final String INVALID_USERNAME = "ghost";

        UserResponseModel buildUser(String id) {
            UserResponseModel r = new UserResponseModel();
            r.setUserId(id);
            r.setUsername(VALID_USERNAME);
            r.setEmail("malick@email.com");
            r.setFullname("Malick Konate");
            r.setCountry("Canada");
            return r;
        }

        UserRequestModel buildRequest() {
            return UserRequestModel.builder()
                    .username(VALID_USERNAME).email("malick@email.com")
                    .password("secret").fullname("Malick Konate")
                    .age(25).country("Canada").build();
        }

        @Test
        void getAllUsers_WhenExist_ReturnsOk() {
            when(userService.getAllUsers()).thenReturn(List.of(buildUser(VALID_ID)));

            webTestClient.get().uri(BASE).accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(UserResponseModel.class)
                    .value(list -> assertFalse(list.isEmpty()));
        }

        @Test
        void getUserById_WhenValid_ReturnsUser() {
            when(userService.getUserById(VALID_ID)).thenReturn(buildUser(VALID_ID));

            webTestClient.get().uri(BASE + "/id/" + VALID_ID)
                    .accept(MediaType.APPLICATION_JSON).exchange()
                    .expectStatus().isOk()
                    .expectBody(UserResponseModel.class)
                    .value(u -> assertEquals(VALID_ID, u.getUserId()));
        }

        @Test
        void getUserById_WhenNotFound_Returns404() {
            when(userService.getUserById("bad-id"))
                    .thenThrow(new NotFoundException("User not found"));

            webTestClient.get().uri(BASE + "/id/bad-id").exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void getUserByUsername_WhenValid_ReturnsUser() {
            when(userService.getUserByUsername(VALID_USERNAME)).thenReturn(buildUser(VALID_ID));

            webTestClient.get().uri(BASE + "/" + VALID_USERNAME)
                    .accept(MediaType.APPLICATION_JSON).exchange()
                    .expectStatus().isOk();
        }

        @Test
        void getUserByUsername_WhenNotFound_Returns404() {
            when(userService.getUserByUsername(INVALID_USERNAME))
                    .thenThrow(new NotFoundException("not found"));

            webTestClient.get().uri(BASE + "/" + INVALID_USERNAME).exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void createUser_WhenValid_ReturnsCreated() {
            when(userService.registerUser(any())).thenReturn(buildUser(VALID_ID));

            webTestClient.post().uri(BASE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isCreated();
        }

        @Test
        void createUser_WhenMalformedJson_Returns4xx() {
            webTestClient.post().uri(BASE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("bad-json").exchange()
                    .expectStatus().is4xxClientError();
        }

        @Test
        void updateUser_WhenValid_ReturnsOk() {
            when(userService.updateUser(eq(VALID_USERNAME), any())).thenReturn(buildUser(VALID_ID));

            webTestClient.put().uri(BASE + "/" + VALID_USERNAME)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isOk();
        }

        @Test
        void updateUser_WhenNotFound_Returns404() {
            when(userService.updateUser(eq(INVALID_USERNAME), any()))
                    .thenThrow(new NotFoundException("not found"));

            webTestClient.put().uri(BASE + "/" + INVALID_USERNAME)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void deleteUser_WhenValid_ReturnsNoContent() {
            doNothing().when(userService).deleteUser(VALID_USERNAME);

            webTestClient.delete().uri(BASE + "/" + VALID_USERNAME).exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        void deleteUser_WhenNotFound_Returns404() {
            doThrow(new NotFoundException("not found")).when(userService).deleteUser(INVALID_USERNAME);

            webTestClient.delete().uri(BASE + "/" + INVALID_USERNAME).exchange()
                    .expectStatus().isNotFound();
        }
    }

    // =========================================================================
    // OrderController — /api/v1/orders
    // =========================================================================

    @Nested
    class OrderControllerTest {

        final String BASE = "/api/v1/orders";
        final String VALID_ID = "ord-001";
        final String INVALID_ID = "non-existent";

        OrderResponseModel buildOrder(String id) {
            OrderResponseModel r = new OrderResponseModel();
            r.setOrderId(id);
            r.setEmail("malick@email.com");
            r.setFullname("Malick Konate");
            r.setOrderStatus(OrderStatus.PENDING);
            return r;
        }

        OrderRequestModel buildRequest() {
            return new OrderRequestModel("malick@email.com", OrderStatus.PENDING,
                    List.of(), List.of());
        }

        @Test
        void getAllOrders_WhenExist_ReturnsOk() {
            when(orderService.getAllOrders()).thenReturn(List.of(buildOrder(VALID_ID)));

            webTestClient.get().uri(BASE).accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(OrderResponseModel.class)
                    .value(list -> assertFalse(list.isEmpty()));
        }

        @Test
        void getOrderById_WhenValid_ReturnsOrder() {
            when(orderService.getOrderById(VALID_ID)).thenReturn(buildOrder(VALID_ID));

            webTestClient.get().uri(BASE + "/" + VALID_ID)
                    .accept(MediaType.APPLICATION_JSON).exchange()
                    .expectStatus().isOk()
                    .expectBody(OrderResponseModel.class)
                    .value(o -> assertEquals(VALID_ID, o.getOrderId()));
        }

        @Test
        void getOrderById_WhenNotFound_Returns404() {
            when(orderService.getOrderById(INVALID_ID))
                    .thenThrow(new NotFoundException("Order not found"));

            webTestClient.get().uri(BASE + "/" + INVALID_ID).exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void getAllOrdersForUser_WhenValid_ReturnsOk() {
            when(orderService.getAllOrdersForUser("user-001"))
                    .thenReturn(List.of(buildOrder(VALID_ID)));

            webTestClient.get().uri(BASE + "/by-user/user-001")
                    .accept(MediaType.APPLICATION_JSON).exchange()
                    .expectStatus().isOk();
        }

        @Test
        void createOrder_WhenValid_ReturnsCreated() {
            when(orderService.createOrder(any())).thenReturn(buildOrder(VALID_ID));

            webTestClient.post().uri(BASE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isCreated();
        }

        @Test
        void createOrder_WhenMalformedJson_Returns4xx() {
            webTestClient.post().uri(BASE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("bad-json").exchange()
                    .expectStatus().is4xxClientError();
        }

        @Test
        void updateOrder_WhenValid_ReturnsOk() {
            when(orderService.updateOrder(eq(VALID_ID), any())).thenReturn(buildOrder(VALID_ID));

            webTestClient.put().uri(BASE + "/" + VALID_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isOk();
        }

        @Test
        void updateOrder_WhenNotFound_Returns404() {
            when(orderService.updateOrder(eq(INVALID_ID), any()))
                    .thenThrow(new NotFoundException("not found"));

            webTestClient.put().uri(BASE + "/" + INVALID_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void deleteOrder_WhenValid_ReturnsOk() {
            doNothing().when(orderService).deleteOrder(VALID_ID);

            webTestClient.delete().uri(BASE + "/" + VALID_ID).exchange()
                    .expectStatus().isOk();
        }

        @Test
        void deleteOrder_WhenNotFound_Returns404() {
            doThrow(new NotFoundException("not found")).when(orderService).deleteOrder(INVALID_ID);

            webTestClient.delete().uri(BASE + "/" + INVALID_ID).exchange()
                    .expectStatus().isNotFound();
        }
    }

    // =========================================================================
    // AdController — /api/v1/ads
    // =========================================================================

    @Nested
    class AdControllerTest {

        final String BASE = "/api/v1/ads";
        final String VALID_ID = "ad-001";
        final String INVALID_ID = "non-existent";

        AdResponseModel buildAd(String id) {
            AdResponseModel r = new AdResponseModel();
            r.setAd_id(id);
            r.setName("Summer Campaign");
            r.setBudget(new BigDecimal("500.00"));
            r.setUserName("malick");
            return r;
        }

        AdRequestModel buildRequest() {
            AdRequestModel r = new AdRequestModel();
            r.setName("Summer Campaign");
            r.setBudget(new BigDecimal("500.00"));
            r.setUserEmail("malick@email.com");
            return r;
        }

        @Test
        void getAllAdCampaigns_WhenExist_ReturnsOk() {
            when(adCampaignService.getAllAdCampaigns()).thenReturn(List.of(buildAd(VALID_ID)));

            webTestClient.get().uri(BASE).accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(AdResponseModel.class)
                    .value(list -> assertFalse(list.isEmpty()));
        }

        @Test
        void getAdCampaignById_WhenValid_ReturnsAd() {
            when(adCampaignService.getAdCampaign(VALID_ID)).thenReturn(buildAd(VALID_ID));

            webTestClient.get().uri(BASE + "/" + VALID_ID)
                    .accept(MediaType.APPLICATION_JSON).exchange()
                    .expectStatus().isOk()
                    .expectBody(AdResponseModel.class)
                    .value(a -> assertEquals(VALID_ID, a.getAd_id()));
        }

        @Test
        void getAdCampaignById_WhenNotFound_Returns404() {
            when(adCampaignService.getAdCampaign(INVALID_ID))
                    .thenThrow(new NotFoundException("Ad not found"));

            webTestClient.get().uri(BASE + "/" + INVALID_ID).exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void createAdCampaign_WhenValid_ReturnsCreated() {
            when(adCampaignService.createAdCampaign(any())).thenReturn(buildAd(VALID_ID));

            webTestClient.post().uri(BASE + "/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isCreated();
        }

        @Test
        void createAdCampaign_WhenMalformedJson_Returns4xx() {
            webTestClient.post().uri(BASE + "/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("bad-json").exchange()
                    .expectStatus().is4xxClientError();
        }

        @Test
        void activateAdCampaign_WhenValid_ReturnsNoContent() {
            doNothing().when(adCampaignService).activateAdCampaign(VALID_ID);

            webTestClient.patch().uri(BASE + "/" + VALID_ID + "/activate").exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        void activateAdCampaign_WhenNotFound_Returns404() {
            doThrow(new NotFoundException("not found"))
                    .when(adCampaignService).activateAdCampaign(INVALID_ID);

            webTestClient.patch().uri(BASE + "/" + INVALID_ID + "/activate").exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void pauseAdCampaign_WhenValid_ReturnsNoContent() {
            doNothing().when(adCampaignService).pauseAdCampaign(VALID_ID);

            webTestClient.patch().uri(BASE + "/" + VALID_ID + "/pause").exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        void pauseAdCampaign_WhenNotFound_Returns404() {
            doThrow(new NotFoundException("not found"))
                    .when(adCampaignService).pauseAdCampaign(INVALID_ID);

            webTestClient.patch().uri(BASE + "/" + INVALID_ID + "/pause").exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void resumeAdCampaign_WhenValid_ReturnsNoContent() {
            doNothing().when(adCampaignService).resumeAdCampaign(VALID_ID);

            webTestClient.patch().uri(BASE + "/" + VALID_ID + "/resume").exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        void resumeAdCampaign_WhenNotFound_Returns404() {
            doThrow(new NotFoundException("not found"))
                    .when(adCampaignService).resumeAdCampaign(INVALID_ID);

            webTestClient.patch().uri(BASE + "/" + INVALID_ID + "/resume").exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void updateAdCampaign_WhenValid_ReturnsOk() {
            when(adCampaignService.updateAdCampaign(eq(VALID_ID), any())).thenReturn(buildAd(VALID_ID));

            webTestClient.put().uri(BASE + "/" + VALID_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isOk();
        }

        @Test
        void updateAdCampaign_WhenNotFound_Returns404() {
            when(adCampaignService.updateAdCampaign(eq(INVALID_ID), any()))
                    .thenThrow(new NotFoundException("not found"));

            webTestClient.put().uri(BASE + "/" + INVALID_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest()).exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void deleteAdCampaign_WhenValid_ReturnsNoContent() {
            doNothing().when(adCampaignService).deleteAdCampaign(VALID_ID);

            webTestClient.delete().uri(BASE + "/" + VALID_ID).exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        void deleteAdCampaign_WhenNotFound_Returns404() {
            doThrow(new NotFoundException("not found"))
                    .when(adCampaignService).deleteAdCampaign(INVALID_ID);

            webTestClient.delete().uri(BASE + "/" + INVALID_ID).exchange()
                    .expectStatus().isNotFound();
        }
    }
}