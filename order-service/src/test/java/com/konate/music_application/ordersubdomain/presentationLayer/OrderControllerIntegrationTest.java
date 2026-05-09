package com.konate.music_application.ordersubdomain.presentationLayer;

import com.konate.music_application.ordersubdomain.DataLayer.*;
import com.konate.music_application.ordersubdomain.PresentationLayer.OrderRequestModel;
import com.konate.music_application.ordersubdomain.PresentationLayer.OrderResponseModel;

// Import your domain clients and models
import com.konate.music_application.ordersubdomain.domainClientLayer.Catalog.AlbumType;
import com.konate.music_application.ordersubdomain.domainClientLayer.User.UserServiceClient;
import com.konate.music_application.ordersubdomain.domainClientLayer.User.UserModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.Catalog.CatalogServiceClient;
import com.konate.music_application.ordersubdomain.domainClientLayer.Artist.ArtistServiceClient;
import com.konate.music_application.ordersubdomain.domainClientLayer.Podcast.PodcastServiceClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.konate.music_application.ordersubdomain.domainClientLayer.Catalog.AlbumModel;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = {
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        }
)
@ActiveProfiles("test")
@Sql({"/data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    // --- MOCK THE EXTERNAL SERVICE CLIENTS ---
    @MockitoBean
    private UserServiceClient userServiceClient;

    @MockitoBean
    private CatalogServiceClient catalogServiceClient;

    @MockitoBean
    private ArtistServiceClient artistServiceClient;

    @MockitoBean
    private PodcastServiceClient podcastServiceClient;

    private final String BASE_URI = "/api/v1/orders";
    private final String VALID_ORDER_ID = "ord_103";
    private final String INVALID_ORDER_ID = "non-existent-order";
    private final String VALID_USER_ID = "user-004";
    private final String VALID_USER_EMAIL = "malick@email.com";

    @BeforeEach
    void setUpMocks() {
        // --- User mock ---
        UserModel mockUser = UserModel.builder()
                .userId(VALID_USER_ID)
                .fullname("Malick Konate")
                .email(VALID_USER_EMAIL)
                .country("Canada")
                .build();

        when(userServiceClient.getUserByEmail(anyString())).thenReturn(mockUser);
        when(userServiceClient.getUserById(anyString())).thenReturn(mockUser);

        // --- Album mock (needed whenever createOrder processes ALBUM_PURCHASE items) ---
        AlbumModel mockAlbum = AlbumModel.builder()
                .albumId("ALB-004")
                .title("Cowboy Sunset")
                .artistFirstName("Reba")
                .artistLastName("McEntire")
                .albumType(AlbumType.LP)
                .recordLabel("Gold Boot Records")
                .releaseDate("2023-01-10")
                .song(new ArrayList<>())
                .build();

        when(catalogServiceClient.getAlbumByTitle(anyString())).thenReturn(mockAlbum);
    }

    // =========================================================================
    // SUCCESS SCENARIOS
    // =========================================================================

    @Test
    void whenOrdersExist_thenReturnAllOrders() {
        webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(OrderResponseModel.class)
                .value(orders -> {
                    assertNotNull(orders);
                    assertFalse(orders.isEmpty());
                });
    }

    @Test
    void whenValidOrderId_thenReturnOrder() {
        webTestClient.get()
                .uri(BASE_URI + "/" + VALID_ORDER_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderResponseModel.class)
                .value(order -> {
                    assertNotNull(order);
                    assertEquals(VALID_ORDER_ID, order.getOrderId());
                });
    }

    @Test
    void whenValidUserId_thenReturnUserOrders() {
        webTestClient.get()
                .uri(BASE_URI + "/user/" + VALID_USER_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OrderResponseModel.class)
                .value(orders -> assertNotNull(orders));
    }

    @Test
    void whenValidRequest_thenCreateOrder() {
        OrderRequestModel request = buildSampleOrderRequest();

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderResponseModel.class)
                .value(created -> {
                    assertNotNull(created);
                    assertNotNull(created.getOrderId());
                    // createOrder always forces PENDING regardless of what the request says
                    assertEquals(OrderStatus.PENDING, created.getOrderStatus());
                    assertEquals(VALID_USER_EMAIL, created.getEmail());
                    assertFalse(created.getOrderItems().isEmpty());
                    assertFalse(created.getPayments().isEmpty());
                });
    }

    @Test
    void whenUpdateValidOrder_thenReturnUpdatedOrder() {
        // ord_103 must be in PENDING state in data.sql for this to work
        OrderRequestModel updatedRequest = buildSampleOrderRequest();
        updatedRequest.setOrderStatus(OrderStatus.COMPLETED);

        webTestClient.put()
                .uri(BASE_URI + "/" + VALID_ORDER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updatedRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderResponseModel.class)
                .value(updated -> {
                    assertNotNull(updated);
                    assertEquals(OrderStatus.COMPLETED, updated.getOrderStatus());
                });
    }

    @Test
    void whenDeleteValidOrder_thenReturnNoContent() {
        // Delete the order
        webTestClient.delete()
                .uri(BASE_URI + "/" + VALID_ORDER_ID + "/cancel")
                .exchange()
                .expectStatus().isNoContent();

        // Verify it's gone
        webTestClient.get()
                .uri(BASE_URI + "/" + VALID_ORDER_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

    // =========================================================================
    // NOT FOUND SCENARIOS
    // =========================================================================

    @Test
    void whenOrderIdNotFound_thenReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URI + "/" + INVALID_ORDER_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUpdateInvalidOrder_thenReturnNotFound() {
        webTestClient.put()
                .uri(BASE_URI + "/" + INVALID_ORDER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildSampleOrderRequest())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenDeleteInvalidOrder_thenReturnNotFound() {
        webTestClient.delete()
                .uri(BASE_URI + "/" + INVALID_ORDER_ID + "/cancel")
                .exchange()
                .expectStatus().isNotFound();
    }

    // =========================================================================
    // BAD REQUEST SCENARIOS (400)
    // =========================================================================

    @Test
    void whenCreateOrderWithEmptyJsonBody_thenBadRequest() {
        // {} → all fields null → email validation fails → 400
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenCreateOrderWithMalformedJson_thenBadRequest() {
        // Unparseable body → HttpMessageNotReadableException → 400
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("not-json-at-all")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUpdateWithMalformedBody_thenBadRequest() {
        webTestClient.put()
                .uri(BASE_URI + "/" + VALID_ORDER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("not-json-at-all")
                .exchange()
                .expectStatus().isBadRequest();
    }

    // =========================================================================
    // CONFLICT SCENARIOS (409)
    // =========================================================================

    @Test
    void whenCreateOrderWithDigitalProductQuantityOver1_thenConflict() {
        // INVARIANT: Digital goods cannot have quantity > 1
        List<OrderItem> items = List.of(
                new OrderItem(ProductType.ALBUM_PURCHASE, "Cowboy Sunset", "Reba McEntire",
                        new BigDecimal("14.99"), 2) // quantity = 2 → conflict
        );
        List<Payment> payments = List.of(
                new Payment(new BigDecimal("29.98"), LocalDateTime.now(),
                        PaymentMethod.CREDIT_CARD, PaymentStatus.PENDING, "USD")
        );

        OrderRequestModel request = new OrderRequestModel(VALID_USER_EMAIL, OrderStatus.PENDING, items, payments);

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void whenCreateOrderWithPaymentMismatch_thenConflict() {
        // INVARIANT: Payment total must match order total
        List<OrderItem> items = List.of(
                new OrderItem(ProductType.ALBUM_PURCHASE, "Cowboy Sunset", "Reba McEntire",
                        new BigDecimal("14.99"), 1) // order total = 14.99
        );
        List<Payment> payments = List.of(
                new Payment(new BigDecimal("5.00"), LocalDateTime.now(), // wrong amount
                        PaymentMethod.CREDIT_CARD, PaymentStatus.PENDING, "USD")
        );

        OrderRequestModel request = new OrderRequestModel(VALID_USER_EMAIL, OrderStatus.PENDING, items, payments);

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private OrderRequestModel buildSampleOrderRequest() {
        List<OrderItem> items = List.of(
                new OrderItem(
                        ProductType.ALBUM_PURCHASE,
                        "Cowboy Sunset",
                        "Reba McEntire",
                        new BigDecimal("14.99"),
                        1
                )
        );

        List<Payment> payments = List.of(
                new Payment(
                        new BigDecimal("14.99"),      // matches 1 × 14.99
                        LocalDateTime.now(),
                        PaymentMethod.CREDIT_CARD,
                        PaymentStatus.PENDING,
                        "USD"
                )
        );

        return new OrderRequestModel(
                VALID_USER_EMAIL,
                OrderStatus.PENDING,
                items,
                payments
        );
    }
}

