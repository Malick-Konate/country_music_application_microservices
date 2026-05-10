package com.konate.music_application.ordersubdomain.BusinessLayer;

import com.konate.music_application.ordersubdomain.DataLayer.*;
import com.konate.music_application.ordersubdomain.Exceptions.*;
import com.konate.music_application.ordersubdomain.MappingLayer.OrderRequestMapper;
import com.konate.music_application.ordersubdomain.MappingLayer.OrderResponseMapper;
import com.konate.music_application.ordersubdomain.PresentationLayer.OrderRequestModel;
import com.konate.music_application.ordersubdomain.PresentationLayer.OrderResponseModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.Artist.ArtistModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.Artist.ArtistServiceClient;
import com.konate.music_application.ordersubdomain.domainClientLayer.Catalog.AlbumModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.Catalog.AlbumType;
import com.konate.music_application.ordersubdomain.domainClientLayer.Catalog.CatalogServiceClient;
import com.konate.music_application.ordersubdomain.domainClientLayer.Podcast.PodcastModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.Podcast.PodcastPricing;
import com.konate.music_application.ordersubdomain.domainClientLayer.Podcast.PodcastServiceClient;
import com.konate.music_application.ordersubdomain.domainClientLayer.User.UserModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.User.UserServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {

    // -------------------------------------------------------------------------
    // Mocks
    // -------------------------------------------------------------------------

    @Mock private OrderRepository       repository;
    @Mock private OrderRequestMapper    requestMapper;
    @Mock private OrderResponseMapper   responseMapper;
    @Mock private UserServiceClient     userService;
    @Mock private PodcastServiceClient  podcastService;
    @Mock private CatalogServiceClient  albumService;
    @Mock private ArtistServiceClient   artistService;

    @InjectMocks
    private OrderServiceImpl orderService;

    // =========================================================================
    // getAllOrders
    // =========================================================================

    @Test
    void getAllOrders_WhenOrdersExist_ReturnsAllOrders() {
        Order order = buildOrder("ord-001", "user-001", OrderStatus.PENDING);
        UserModel user = buildUser("user-001");
        OrderResponseModel response = new OrderResponseModel();

        when(repository.findAll()).thenReturn(List.of(order));
        when(userService.getUserById("user-001")).thenReturn(user);
        when(responseMapper.toRespondModel(order)).thenReturn(response);

        List<OrderResponseModel> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findAll();
        verify(userService).getUserById("user-001");
    }

    @Test
    void getAllOrders_WhenNoOrdersExist_ReturnsEmptyList() {
        when(repository.findAll()).thenReturn(new ArrayList<>());

        List<OrderResponseModel> result = orderService.getAllOrders();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
        verifyNoInteractions(userService);
    }

    // =========================================================================
    // getOrderById
    // =========================================================================

    @Test
    void getOrderById_WhenValidId_ReturnsOrder() {
        Order order = buildOrder("ord-001", "user-001", OrderStatus.PENDING);
        UserModel user = buildUser("user-001");
        OrderResponseModel response = new OrderResponseModel();

        when(repository.findAllByOrderIdentifier_OrderId("ord-001")).thenReturn(order);
        when(userService.getUserById("user-001")).thenReturn(user);
        when(responseMapper.toRespondModel(order)).thenReturn(response);

        OrderResponseModel result = orderService.getOrderById("ord-001");

        assertNotNull(result);
        verify(repository).findAllByOrderIdentifier_OrderId("ord-001");
    }

    @Test
    void getOrderById_WhenInvalidId_ThrowsNotFoundException() {
        when(repository.findAllByOrderIdentifier_OrderId("bad-id")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> orderService.getOrderById("bad-id"));
        verify(repository).findAllByOrderIdentifier_OrderId("bad-id");
        verifyNoInteractions(userService);
    }

    // =========================================================================
    // getAllOrdersForUser
    // =========================================================================

    @Test
    void getAllOrdersForUser_WhenUserHasOrders_ReturnsOrders() {
        Order order = buildOrder("ord-001", "user-001", OrderStatus.PENDING);
        UserModel user = buildUser("user-001");
        OrderResponseModel response = new OrderResponseModel();

        when(userService.getUserById("user-001")).thenReturn(user);
        when(repository.findOrderByUserIdentifier("user-001")).thenReturn(List.of(order));
        when(responseMapper.toRespondModel(order)).thenReturn(response);

        List<OrderResponseModel> result = orderService.getAllOrdersForUser("user-001");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllOrdersForUser_WhenUserHasNoOrders_ReturnsEmptyList() {
        UserModel user = buildUser("user-001");

        when(userService.getUserById("user-001")).thenReturn(user);
        when(repository.findOrderByUserIdentifier("user-001")).thenReturn(new ArrayList<>());

        List<OrderResponseModel> result = orderService.getAllOrdersForUser("user-001");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // deleteOrder
    // =========================================================================

    @Test
    void deleteOrder_WhenValidId_DeletesSuccessfully() {
        Order order = buildOrder("ord-001", "user-001", OrderStatus.PENDING);
        when(repository.findAllByOrderIdentifier_OrderId("ord-001")).thenReturn(order);

        orderService.deleteOrder("ord-001");

        verify(repository).delete(order);
    }

    @Test
    void deleteOrder_WhenInvalidId_ThrowsNotFoundException() {
        when(repository.findAllByOrderIdentifier_OrderId("bad-id")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> orderService.deleteOrder("bad-id"));
        verify(repository, never()).delete(any());
    }

    // =========================================================================
    // createOrder — Input Validation (InvalidInputException)
    // =========================================================================

    @Test
    void createOrder_WhenNullRequest_ThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> orderService.createOrder(null));
        verifyNoInteractions(repository, userService, albumService, podcastService, artistService);
    }

    @Test
    void createOrder_WhenNullEmail_ThrowsInvalidInputException() {
        OrderRequestModel request = new OrderRequestModel(null, OrderStatus.PENDING,
                List.of(buildAlbumItem(1)), List.of(buildPayment("14.99")));

        assertThrows(InvalidInputException.class, () -> orderService.createOrder(request));
        verifyNoInteractions(userService);
    }

    @Test
    void createOrder_WhenEmptyEmail_ThrowsInvalidInputException() {
        OrderRequestModel request = new OrderRequestModel("   ", OrderStatus.PENDING,
                List.of(buildAlbumItem(1)), List.of(buildPayment("14.99")));

        assertThrows(InvalidInputException.class, () -> orderService.createOrder(request));
        verifyNoInteractions(userService);
    }

    @Test
    void createOrder_WhenNullOrderItems_ThrowsInvalidInputException() {
        OrderRequestModel request = new OrderRequestModel("test@email.com", OrderStatus.PENDING,
                null, List.of(buildPayment("14.99")));

        assertThrows(InvalidInputException.class, () -> orderService.createOrder(request));
        verifyNoInteractions(userService);
    }

    @Test
    void createOrder_WhenEmptyOrderItems_ThrowsInvalidInputException() {
        OrderRequestModel request = new OrderRequestModel("test@email.com", OrderStatus.PENDING,
                new ArrayList<>(), List.of(buildPayment("14.99")));

        assertThrows(InvalidInputException.class, () -> orderService.createOrder(request));
        verifyNoInteractions(userService);
    }

    @Test
    void createOrder_WhenNullPayments_ThrowsInvalidInputException() {
        OrderRequestModel request = new OrderRequestModel("test@email.com", OrderStatus.PENDING,
                List.of(buildAlbumItem(1)), null);

        assertThrows(InvalidInputException.class, () -> orderService.createOrder(request));
        verifyNoInteractions(userService);
    }

    @Test
    void createOrder_WhenEmptyPayments_ThrowsInvalidInputException() {
        OrderRequestModel request = new OrderRequestModel("test@email.com", OrderStatus.PENDING,
                List.of(buildAlbumItem(1)), new ArrayList<>());

        assertThrows(InvalidInputException.class, () -> orderService.createOrder(request));
        verifyNoInteractions(userService);
    }

    // =========================================================================
    // createOrder — Business Invariants (OrderConflictException)
    // =========================================================================

    @Test
    void createOrder_WhenAlbumPurchaseQuantityOver1_ThrowsOrderConflictException() {
        OrderRequestModel request = buildRequestWithItems(
                List.of(buildAlbumItem(2)),            // quantity 2 — invalid
                List.of(buildPayment("29.98"))
        );
        when(userService.getUserByEmail(anyString())).thenReturn(buildUser("user-001"));

        assertThrows(OrderConflictException.class, () -> orderService.createOrder(request));
        verifyNoInteractions(albumService);
    }

    @Test
    void createOrder_WhenPodcastSubscriptionQuantityOver1_ThrowsOrderConflictException() {
        OrderItem podcastItem = new OrderItem(ProductType.PODCAST_SUBSCRIPTION,
                "Tech Talk Daily", "John Host", new BigDecimal("4.99"), 2);
        OrderRequestModel request = buildRequestWithItems(
                List.of(podcastItem), List.of(buildPayment("9.98"))
        );
        when(userService.getUserByEmail(anyString())).thenReturn(buildUser("user-001"));

        assertThrows(OrderConflictException.class, () -> orderService.createOrder(request));
        verifyNoInteractions(podcastService);
    }

    @Test
    void createOrder_WhenArtistDonationQuantityOver1_ThrowsOrderConflictException() {
        OrderItem donationItem = new OrderItem(ProductType.ARTIST_DONATION,
                "Artist Support Donation", "John Doe", new BigDecimal("10.00"), 2);
        OrderRequestModel request = buildRequestWithItems(
                List.of(donationItem), List.of(buildPayment("20.00"))
        );
        when(userService.getUserByEmail(anyString())).thenReturn(buildUser("user-001"));

        assertThrows(OrderConflictException.class, () -> orderService.createOrder(request));
        verifyNoInteractions(artistService);
    }

    @Test
    void createOrder_WhenPaymentAmountIsNull_ThrowsInvalidInputException() {
        Payment nullAmountPayment = new Payment(null, LocalDateTime.now(),
                PaymentMethod.CREDIT_CARD, PaymentStatus.PENDING, "USD");
        OrderRequestModel request = buildRequestWithItems(
                List.of(buildAlbumItem(1)), List.of(nullAmountPayment)
        );
        when(userService.getUserByEmail(anyString())).thenReturn(buildUser("user-001"));
        when(albumService.getAlbumByTitle(anyString())).thenReturn(buildAlbumModel());

        assertThrows(InvalidInputException.class, () -> orderService.createOrder(request));
    }

    @Test
    void createOrder_WhenPaymentAmountMismatch_ThrowsOrderConflictException() {
        // Album costs 14.99 but we pay only 5.00
        OrderRequestModel request = buildRequestWithItems(
                List.of(buildAlbumItem(1)), List.of(buildPayment("5.00"))
        );
        when(userService.getUserByEmail(anyString())).thenReturn(buildUser("user-001"));
        when(albumService.getAlbumByTitle(anyString())).thenReturn(buildAlbumModel());

        assertThrows(OrderConflictException.class, () -> orderService.createOrder(request));
        verify(repository, never()).save(any());
    }

    // =========================================================================
    // createOrder — Success paths (all 3 product types + podcast pricing)
    // =========================================================================

    @Test
    void createOrder_WhenValidAlbumPurchase_ReturnsCreatedOrder() {
        // album price is hardcoded at 14.99 in service
        OrderRequestModel request = buildRequestWithItems(
                List.of(buildAlbumItem(1)), List.of(buildPayment("14.99"))
        );
        UserModel user = buildUser("user-001");
        Order savedOrder = buildOrder("ord-new", "user-001", OrderStatus.PENDING);
        OrderResponseModel response = new OrderResponseModel();

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(albumService.getAlbumByTitle(anyString())).thenReturn(buildAlbumModel());
        when(requestMapper.toOder(any(), any(), any())).thenReturn(savedOrder);
        when(repository.save(any())).thenReturn(savedOrder);
        when(responseMapper.toRespondModel(savedOrder)).thenReturn(response);

        OrderResponseModel result = orderService.createOrder(request);

        assertNotNull(result);
        verify(albumService).getAlbumByTitle("Cowboy Sunset");
        verify(repository).save(any());
    }

    @Test
    void createOrder_WhenFreePodcastSubscription_ReturnsCreatedOrderWithZeroPrice() {
        // FREE podcast → price = 0, payment must also be 0
        OrderItem podcastItem = new OrderItem(ProductType.PODCAST_SUBSCRIPTION,
                "Free Show", "Host", BigDecimal.ZERO, 1);
        OrderRequestModel request = buildRequestWithItems(
                List.of(podcastItem), List.of(buildPayment("0.00"))
        );
        UserModel user = buildUser("user-001");
        Order savedOrder = buildOrder("ord-new", "user-001", OrderStatus.PENDING);
        OrderResponseModel response = new OrderResponseModel();

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(podcastService.getPodcastByTitle(anyString())).thenReturn(
                buildPodcastModel("Free Show", PodcastPricing.FREE));
        when(requestMapper.toOder(any(), any(), any())).thenReturn(savedOrder);
        when(repository.save(any())).thenReturn(savedOrder);
        when(responseMapper.toRespondModel(savedOrder)).thenReturn(response);

        OrderResponseModel result = orderService.createOrder(request);

        assertNotNull(result);
        verify(podcastService).getPodcastByTitle("Free Show");
    }

    @Test
    void createOrder_WhenSubscriptionPodcast_PriceIs4_99() {
        OrderItem podcastItem = new OrderItem(ProductType.PODCAST_SUBSCRIPTION,
                "Premium Show", "Host", new BigDecimal("4.99"), 1);
        OrderRequestModel request = buildRequestWithItems(
                List.of(podcastItem), List.of(buildPayment("4.99"))
        );
        UserModel user = buildUser("user-001");
        Order savedOrder = buildOrder("ord-new", "user-001", OrderStatus.PENDING);

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(podcastService.getPodcastByTitle(anyString())).thenReturn(
                buildPodcastModel("Premium Show", PodcastPricing.SUBSCRIPTION));
        when(requestMapper.toOder(any(), any(), any())).thenReturn(savedOrder);
        when(repository.save(any())).thenReturn(savedOrder);
        when(responseMapper.toRespondModel(savedOrder)).thenReturn(new OrderResponseModel());

        OrderResponseModel result = orderService.createOrder(request);

        assertNotNull(result);
    }

    @Test
    void createOrder_WhenPerEpisodePodcast_PriceIs7_99() {
        OrderItem podcastItem = new OrderItem(ProductType.PODCAST_SUBSCRIPTION,
                "Episode Show", "Host", new BigDecimal("7.99"), 1);
        OrderRequestModel request = buildRequestWithItems(
                List.of(podcastItem), List.of(buildPayment("7.99"))
        );
        UserModel user = buildUser("user-001");
        Order savedOrder = buildOrder("ord-new", "user-001", OrderStatus.PENDING);

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(podcastService.getPodcastByTitle(anyString())).thenReturn(
                buildPodcastModel("Episode Show", PodcastPricing.PER_EPISODE));
        when(requestMapper.toOder(any(), any(), any())).thenReturn(savedOrder);
        when(repository.save(any())).thenReturn(savedOrder);
        when(responseMapper.toRespondModel(savedOrder)).thenReturn(new OrderResponseModel());

        OrderResponseModel result = orderService.createOrder(request);

        assertNotNull(result);
    }

    @Test
    void createOrder_WhenValidArtistDonation_ReturnsCreatedOrder() {
        // Price is user-defined for donations
        OrderItem donationItem = new OrderItem(ProductType.ARTIST_DONATION,
                "Artist Support Donation", "John Doe", new BigDecimal("20.00"), 1);
        OrderRequestModel request = buildRequestWithItems(
                List.of(donationItem), List.of(buildPayment("20.00"))
        );
        UserModel user = buildUser("user-001");
        Order savedOrder = buildOrder("ord-new", "user-001", OrderStatus.PENDING);

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(artistService.getArtistByLastName("Doe")).thenReturn(
                ArtistModel.builder().firstName("John").lastName("Doe").build());
        when(requestMapper.toOder(any(), any(), any())).thenReturn(savedOrder);
        when(repository.save(any())).thenReturn(savedOrder);
        when(responseMapper.toRespondModel(savedOrder)).thenReturn(new OrderResponseModel());

        OrderResponseModel result = orderService.createOrder(request);

        assertNotNull(result);
        verify(artistService).getArtistByLastName("Doe");
    }

    @Test
    void createOrder_AlwaysSetsPendingStatus_RegardlessOfRequest() {
        OrderRequestModel request = buildRequestWithItems(
                List.of(buildAlbumItem(1)), List.of(buildPayment("14.99"))
        );
        UserModel user = buildUser("user-001");
        Order capturedOrder = buildOrder("ord-new", "user-001", OrderStatus.PENDING);
        OrderResponseModel response = new OrderResponseModel();

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(albumService.getAlbumByTitle(anyString())).thenReturn(buildAlbumModel());
        when(requestMapper.toOder(any(), any(), any())).thenReturn(capturedOrder);
        when(repository.save(any(Order.class))).thenAnswer(inv -> {
            Order saved = inv.getArgument(0);
            // Verify the service forces PENDING regardless of input
            assertEquals(OrderStatus.PENDING, saved.getOrderStatus());
            return saved;
        });
        when(responseMapper.toRespondModel(any())).thenReturn(response);

        orderService.createOrder(request);

        verify(repository).save(any());
    }

    // =========================================================================
    // updateOrder — Not Found
    // =========================================================================

    @Test
    void updateOrder_WhenInvalidOrderId_ThrowsNotFoundException() {
        when(repository.findAllByOrderIdentifier_OrderId("bad-id")).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> orderService.updateOrder("bad-id", buildUpdateRequest(OrderStatus.COMPLETED)));
        verify(repository, never()).save(any());
    }

    // =========================================================================
    // updateOrder — Input Validation
    // =========================================================================

    @Test
    void updateOrder_WhenNullRequest_ThrowsInvalidInputException() {
        Order order = buildOrder("ord-001", "user-001", OrderStatus.PENDING);
        when(repository.findAllByOrderIdentifier_OrderId("ord-001")).thenReturn(order);

        assertThrows(InvalidInputException.class,
                () -> orderService.updateOrder("ord-001", null));
    }

    @Test
    void updateOrder_WhenNullEmail_ThrowsInvalidInputException() {
        Order order = buildOrder("ord-001", "user-001", OrderStatus.PENDING);
        when(repository.findAllByOrderIdentifier_OrderId("ord-001")).thenReturn(order);

        OrderRequestModel request = new OrderRequestModel(null, OrderStatus.COMPLETED,
                List.of(buildAlbumItem(1)), List.of(buildPayment("14.99")));

        assertThrows(InvalidInputException.class,
                () -> orderService.updateOrder("ord-001", request));
    }

    @Test
    void updateOrder_WhenEmptyOrderItems_ThrowsInvalidInputException() {
        Order order = buildOrder("ord-001", "user-001", OrderStatus.PENDING);
        when(repository.findAllByOrderIdentifier_OrderId("ord-001")).thenReturn(order);

        OrderRequestModel request = new OrderRequestModel("test@email.com", OrderStatus.COMPLETED,
                new ArrayList<>(), List.of(buildPayment("14.99")));

        assertThrows(InvalidInputException.class,
                () -> orderService.updateOrder("ord-001", request));
    }

    @Test
    void updateOrder_WhenEmptyPayments_ThrowsInvalidInputException() {
        Order order = buildOrder("ord-001", "user-001", OrderStatus.PENDING);
        when(repository.findAllByOrderIdentifier_OrderId("ord-001")).thenReturn(order);

        OrderRequestModel request = new OrderRequestModel("test@email.com", OrderStatus.COMPLETED,
                List.of(buildAlbumItem(1)), new ArrayList<>());

        assertThrows(InvalidInputException.class,
                () -> orderService.updateOrder("ord-001", request));
    }

    // =========================================================================
    // updateOrder — State Guard (InvalidOrderStateException)
    // =========================================================================

    @Test
    void updateOrder_WhenOrderIsCancelled_ThrowsInvalidOrderStateException() {
        Order cancelledOrder = buildOrder("ord-001", "user-001", OrderStatus.CANCELLED);
        when(repository.findAllByOrderIdentifier_OrderId("ord-001")).thenReturn(cancelledOrder);

        assertThrows(InvalidOrderStateException.class,
                () -> orderService.updateOrder("ord-001", buildUpdateRequest(OrderStatus.PENDING)));
        verify(repository, never()).save(any());
    }

    @Test
    void updateOrder_WhenOrderIsAlreadyCompleted_ThrowsInvalidOrderStateException() {
        Order completedOrder = buildOrder("ord-001", "user-001", OrderStatus.COMPLETED);
        when(repository.findAllByOrderIdentifier_OrderId("ord-001")).thenReturn(completedOrder);

        assertThrows(InvalidOrderStateException.class,
                () -> orderService.updateOrder("ord-001", buildUpdateRequest(OrderStatus.COMPLETED)));
        verify(repository, never()).save(any());
    }

    // =========================================================================
    // updateOrder — Success
    // =========================================================================

    @Test
    void updateOrder_WhenValidUpdate_ReturnsUpdatedOrder() {
        Order existingOrder = buildOrder("ord-001", "user-001", OrderStatus.PENDING);
        Order savedOrder   = buildOrder("ord-001", "user-001", OrderStatus.COMPLETED);
        UserModel user = buildUser("user-001");
        OrderResponseModel response = new OrderResponseModel();

        when(repository.findAllByOrderIdentifier_OrderId("ord-001")).thenReturn(existingOrder);
        when(repository.save(existingOrder)).thenReturn(savedOrder);
        when(userService.getUserById("user-001")).thenReturn(user);
        when(responseMapper.toRespondModel(savedOrder)).thenReturn(response);

        OrderResponseModel result = orderService.updateOrder("ord-001",
                buildUpdateRequest(OrderStatus.COMPLETED));

        assertNotNull(result);
        verify(repository).save(existingOrder);
    }

    @Test
    void updateOrder_WhenSetToCompleted_PaymentsAreMarkedCompleted() {
        // Order has a PENDING payment
        Payment pendingPayment = new Payment(new BigDecimal("14.99"), LocalDateTime.now(),
                PaymentMethod.CREDIT_CARD, PaymentStatus.PENDING, "USD");
        Order existingOrder = buildOrder("ord-001", "user-001", OrderStatus.PENDING);
        existingOrder.setPayments(new ArrayList<>(List.of(pendingPayment)));

        when(repository.findAllByOrderIdentifier_OrderId("ord-001")).thenReturn(existingOrder);
        when(repository.save(existingOrder)).thenReturn(existingOrder);
        when(userService.getUserById("user-001")).thenReturn(buildUser("user-001"));
        when(responseMapper.toRespondModel(existingOrder)).thenReturn(new OrderResponseModel());

        orderService.updateOrder("ord-001", buildUpdateRequest(OrderStatus.COMPLETED));

        // After update, payments should have COMPLETED status
        existingOrder.getPayments().forEach(p ->
                assertEquals(PaymentStatus.COMPLETED, p.getPaymentStatus()));
    }

    @Test
    void updateOrder_StatusNotChangedWhenRequestStatusIsNull() {
        Order existingOrder = buildOrder("ord-001", "user-001", OrderStatus.PENDING);
        OrderRequestModel request = new OrderRequestModel("test@email.com", null,
                List.of(buildAlbumItem(1)), List.of(buildPayment("14.99")));

        when(repository.findAllByOrderIdentifier_OrderId("ord-001")).thenReturn(existingOrder);
        when(repository.save(existingOrder)).thenReturn(existingOrder);
        when(userService.getUserById("user-001")).thenReturn(buildUser("user-001"));
        when(responseMapper.toRespondModel(existingOrder)).thenReturn(new OrderResponseModel());

        orderService.updateOrder("ord-001", request);

        // Status must remain PENDING since null was passed
        assertEquals(OrderStatus.PENDING, existingOrder.getOrderStatus());
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Order buildOrder(String orderId, String userId, OrderStatus status) {
        Order order = new Order();
        order.setOrderIdentifier(new OrderIdentifier(orderId));
        order.setUserIdentifier(userId);
        order.setOrderStatus(status);
        order.setOrderItems(new ArrayList<>(List.of(buildAlbumItem(1))));
        order.setPayments(new ArrayList<>(List.of(
                new Payment(new BigDecimal("14.99"), LocalDateTime.now(),
                        PaymentMethod.CREDIT_CARD, PaymentStatus.PENDING, "USD")
        )));
        return order;
    }

    private UserModel buildUser(String userId) {
        return UserModel.builder()
                .userId(userId)
                .fullname("Malick Konate")
                .email("malick@email.com")
                .country("Canada")
                .build();
    }

    private AlbumModel buildAlbumModel() {
        return AlbumModel.builder()
                .albumId("ALB-001")
                .title("Cowboy Sunset")
                .artistFirstName("Reba")
                .artistLastName("McEntire")
                .albumType(AlbumType.LP)
                .build();
    }

    private PodcastModel buildPodcastModel(String title, PodcastPricing pricing) {
        return PodcastModel.builder()
                .podcastId("POD-001")
                .title(title)
                .hostname("John Host")
                .pricingModel(pricing)
                .build();
    }

    /** Single ALBUM_PURCHASE item — price field is display only; service hardcodes 14.99 */
    private OrderItem buildAlbumItem(int quantity) {
        return new OrderItem(ProductType.ALBUM_PURCHASE, "Cowboy Sunset",
                "Reba McEntire", new BigDecimal("14.99"), quantity);
    }

    private Payment buildPayment(String amount) {
        return new Payment(new BigDecimal(amount), LocalDateTime.now(),
                PaymentMethod.CREDIT_CARD, PaymentStatus.PENDING, "USD");
    }

    /** Request with a valid email + explicit items/payments */
    private OrderRequestModel buildRequestWithItems(List<OrderItem> items, List<Payment> payments) {
        return new OrderRequestModel("test@email.com", OrderStatus.PENDING,
                new ArrayList<>(items), new ArrayList<>(payments));
    }

    /** Request used for update scenarios */
    private OrderRequestModel buildUpdateRequest(OrderStatus status) {
        return new OrderRequestModel("test@email.com", status,
                List.of(buildAlbumItem(1)), List.of(buildPayment("14.99")));
    }
}