package com.konate.music_application.ordersubdomain.BusinessLayer;

import com.konate.music_application.ordersubdomain.DataLayer.*;
import com.konate.music_application.ordersubdomain.Exceptions.InvalidInputException;
import com.konate.music_application.ordersubdomain.Exceptions.InvalidOrderStateException;
import com.konate.music_application.ordersubdomain.Exceptions.NotFoundException;
import com.konate.music_application.ordersubdomain.Exceptions.OrderConflictException;
import com.konate.music_application.ordersubdomain.MappingLayer.OrderRequestMapper;
import com.konate.music_application.ordersubdomain.MappingLayer.OrderResponseMapper;
import com.konate.music_application.ordersubdomain.PresentationLayer.OrderRequestModel;
import com.konate.music_application.ordersubdomain.PresentationLayer.OrderResponseModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.Artist.ArtistModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.Artist.ArtistServiceClient;
import com.konate.music_application.ordersubdomain.domainClientLayer.Catalog.AlbumModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.Catalog.CatalogServiceClient;
import com.konate.music_application.ordersubdomain.domainClientLayer.Podcast.PodcastModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.Podcast.PodcastPricing;
import com.konate.music_application.ordersubdomain.domainClientLayer.Podcast.PodcastServiceClient;
import com.konate.music_application.ordersubdomain.domainClientLayer.User.UserModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.User.UserServiceClient;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;
    private final OrderRequestMapper requestMapper;
    private final OrderResponseMapper responseMapper;
    private final UserServiceClient userService;
    private final PodcastServiceClient podcastService;
    private final CatalogServiceClient albumService;
    private final ArtistServiceClient artistService;

    public OrderServiceImpl(OrderRepository repository, OrderRequestMapper requestMapper, OrderResponseMapper responseMapper,
                            UserServiceClient userService, PodcastServiceClient podcastService, CatalogServiceClient albumService, ArtistServiceClient artistService) {
        this.repository = repository;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
        this.userService = userService;
        this.podcastService = podcastService;
        this.albumService = albumService;
        this.artistService = artistService;
    }

    @Override
    public List<OrderResponseModel> getAllOrders() {
        List<Order> orders = repository.findAll();
        List<OrderResponseModel> responseModels = new ArrayList<>();

        for (Order order : orders) {
            UserModel user = userService.getUserById(order.getUserIdentifier());

            OrderResponseModel orderResponseModel = responseMapper.toRespondModel(order);
            orderResponseModel.setFullname(user.getFullname());
            orderResponseModel.setCountry(user.getCountry());
            orderResponseModel.setEmail(user.getEmail());
            responseModels.add(orderResponseModel);
        }
        return responseModels;
    }

    @Override
    public OrderResponseModel getOrderById(String orderId) {
        Order order = repository.findAllByOrderIdentifier_OrderId(orderId);
        if (order == null)
            throw new NotFoundException("order not found with this id: " + orderId);
        UserModel user = userService.getUserById(order.getUserIdentifier());

        OrderResponseModel orderResponseModel = responseMapper.toRespondModel(order);
        orderResponseModel.setFullname(user.getFullname());
        orderResponseModel.setCountry(user.getCountry());
        orderResponseModel.setEmail(user.getEmail());

        return orderResponseModel;
    }

    @Override
    public List<OrderResponseModel> getAllOrdersForUser(String userId) {
        UserModel user = userService.getUserById(userId);
        List<Order> orders = repository.findOrderByUserIdentifier(userId);
        List<OrderResponseModel> responseModels = new ArrayList<>();

//        do {
//            Order order = repository.findAllByOrderIdentifier_OrderId(userId);
//
//            OrderResponseModel orderResponseModel = responseMapper.toRespondModel(order);
//            orderResponseModel.setFullname(user.getFullname());
//            orderResponseModel.setCountry(user.getCountry());
//            orderResponseModel.setEmail(user.getEmail());
//
//            responseModels.add(orderResponseModel);
//
//        }while(Objects.equals(userId, user.getUserId()));



        for (Order order : orders) {


            OrderResponseModel orderResponseModel = responseMapper.toRespondModel(order);
            orderResponseModel.setFullname(user.getFullname());
            orderResponseModel.setCountry(user.getCountry());
            orderResponseModel.setEmail(user.getEmail());

            responseModels.add(orderResponseModel);
        }
        return responseModels;
    }

    @Override
    public void deleteOrder(String orderId) {
        Order order = repository.findAllByOrderIdentifier_OrderId(orderId);
        if (order == null)
            throw new NotFoundException("order not found with this id: " + orderId);

        repository.delete(order);
    }


    @Override
    @Transactional
    public OrderResponseModel createOrder(OrderRequestModel requestModel) {
        if (requestModel == null)
            throw new InvalidInputException("The request cannot be null.");

        // 1. Validate and Fetch User
        UserModel user = userService.getUserByEmail(requestModel.getUserEmail());

        // 2. (Fetch details from other services)
        List<OrderItem> hydratedItems = new ArrayList<>();
        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        for (OrderItem item : requestModel.getOrderItems()) {
            // 1. INVARIANT: Digital Goods Quantity Conflict
            if ((item.getProductType() == ProductType.ALBUM_PURCHASE ||
                    item.getProductType() == ProductType.PODCAST_SUBSCRIPTION) &&
                    item.getQuantity() > 1) {
                throw new OrderConflictException("Conflict: You cannot purchase more than 1 quantity of a digital product (" + item.getDisplayName() + ").");
            }
            if(item.getProductType() == ProductType.ARTIST_DONATION && item.getQuantity() > 1)
                throw new OrderConflictException("Conflict: You cannot make more than 1 artist donation at the time.");

            OrderItem hydratedItem = null;

            if (item.getProductType() == ProductType.ALBUM_PURCHASE) {
                // Use the ID from the request to fetch the real album data
                AlbumModel album = albumService.getAlbumByTitle(item.getDisplayName());
                BigDecimal albumPrice = new BigDecimal("14.99");

                hydratedItem = new OrderItem(
                        ProductType.ALBUM_PURCHASE,
                        album.getTitle(),
                        album.getArtistFirstName() + " " + album.getArtistLastName(),
                        albumPrice,
                        item.getQuantity()
                );
            } else if (item.getProductType() == ProductType.PODCAST_SUBSCRIPTION) {


                PodcastModel podcast = podcastService.getPodcastByTitle(item.getDisplayName());

                BigDecimal podcastPrice = determinePodcastPrice(podcast);

                hydratedItem = new OrderItem(
                        ProductType.PODCAST_SUBSCRIPTION,
                        podcast.getTitle(),
                        podcast.getHostname(),
                        podcastPrice,
                        item.getQuantity()
                );
            } else if (item.getProductType() == ProductType.ARTIST_DONATION) {
                String fullName = item.getArtistName();
                String[] nameParts = fullName.split(" ");
                String lastName = nameParts[nameParts.length - 1];
                ArtistModel artist = artistService.getArtistByLastName(lastName);
                hydratedItem = new OrderItem(
                        ProductType.ARTIST_DONATION,
                        "Artist Support Donation",
                        artist.getFirstName() + " " + artist.getLastName(),
                        item.getPrice(), // User defined amount
                        item.getQuantity()
                );
            }

            if (hydratedItem != null) {
                hydratedItems.add(hydratedItem);
                // Calculate total: price * quantity
                totalOrderAmount = totalOrderAmount.add(
                        hydratedItem.getPrice().multiply(BigDecimal.valueOf(hydratedItem.getQuantity()))
                );
            }
        }

// 2. INVARIANT: Financial Discrepancy Conflict
        BigDecimal totalProvidedPayment = BigDecimal.ZERO;
        if (requestModel.getPayments() == null || requestModel.getPayments().isEmpty()) {
            throw new OrderConflictException("Conflict: An order must have at least one valid payment method attached.");
        }

        for (Payment p : requestModel.getPayments()) {
            // ADD THIS NULL CHECK:
            if (p.getAmount() == null) {
                throw new InvalidInputException("Payment amount cannot be null.");
            }
            totalProvidedPayment = totalProvidedPayment.add(p.getAmount());
        }

        if (totalProvidedPayment.compareTo(totalOrderAmount) != 0) {
            throw new OrderConflictException("Conflict: Payment amount discrepancy. Expected $" + totalOrderAmount + " but received $" + totalProvidedPayment);
        }
        // Map the payments correctly using the client's provided amounts now that they are validated
        List<Payment> paymentList = new ArrayList<>();
        for (Payment p : requestModel.getPayments()) {
            paymentList.add(new Payment(
                    p.getAmount(), // Using the validated amount
                    null,
                    p.getMethod(),
                    PaymentStatus.PENDING,
                    p.getCurrency() != null ? p.getCurrency() : "USD"
            ));
        }

        // 4. Assemble and Save Order
        Order orderNew = requestMapper.toOder(
                requestModel,
                new OrderIdentifier(),
                user
        );
        orderNew.setOrderStatus(OrderStatus.PENDING);
        orderNew.setOrderItems(hydratedItems);
        orderNew.setPayments(paymentList);
        orderNew.setUserIdentifier(user.getUserId());

        Order savedOrder = repository.save(orderNew);

        // 5. Map to Response
        OrderResponseModel response = responseMapper.toRespondModel(savedOrder);
        response.setFullname(user.getFullname());
        response.setEmail(user.getEmail());
        response.setCountry(user.getCountry());

        return response;
    }

    // Help method to keep the create method clean
    private BigDecimal determinePodcastPrice(PodcastModel podcast) {
        if (podcast.getPricingModel().equals(PodcastPricing.FREE)) return BigDecimal.ZERO;
        if (podcast.getPricingModel().equals(PodcastPricing.SUBSCRIPTION)) return new BigDecimal("4.99");
        if (podcast.getPricingModel().equals(PodcastPricing.PER_EPISODE)) return new BigDecimal("7.99");
        return BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public OrderResponseModel updateOrder(String orderId, OrderRequestModel requestModel) {
        // 1. Find the existing order
        Order existingOrder = repository.findAllByOrderIdentifier_OrderId(orderId);
        if (existingOrder == null) {
            throw new NotFoundException("Order not found with id: " + orderId);
        }

        // 2. Business Logic: Prevent updates to cancelled or completed orders
        // AKA aggregate invariant
        if (existingOrder.getOrderStatus() == OrderStatus.CANCELLED ||
                existingOrder.getOrderStatus() == OrderStatus.COMPLETED) {
            throw new InvalidOrderStateException(
                    orderId,
                    existingOrder.getOrderStatus()
            );
        }

        // 3. Update the Status (If provided in the request)

        if (requestModel.getOrderStatus() != null) {
            existingOrder.setOrderStatus(requestModel.getOrderStatus());
        }

        // 4. Update Payment Status if needed
        // Typically, if Order becomes COMPLETED, we mark all payments as COMPLETED
        if (existingOrder.getOrderStatus() == OrderStatus.COMPLETED) {
            for (Payment payment : existingOrder.getPayments()) {
                // Note: Since Payment is a Value Object, we replace the list or use a helper
                // For simplicity in this aggregate:
                existingOrder.setPayments(updatePaymentStatus(existingOrder.getPayments(), PaymentStatus.COMPLETED));
            }
        }

        // 5. Save and Return
        Order updatedOrder = repository.save(existingOrder);

        // Map to response and add links (HATEOAS)
        UserModel user = userService.getUserById(updatedOrder.getUserIdentifier());
        OrderResponseModel response = responseMapper.toRespondModel(updatedOrder);
        response.setFullname(user.getFullname());
        response.setEmail(user.getEmail());
        response.setCountry(user.getCountry());
        return response;
    }


    private List<Payment> updatePaymentStatus(List<Payment> currentPayments, PaymentStatus newStatus) {
        List<Payment> updatedPayments = new ArrayList<>();
        for (Payment p : currentPayments) {
            updatedPayments.add(new Payment(
                    p.getAmount(), java.time.LocalDateTime.now(), p.getMethod(), newStatus, p.getCurrency()
            ));
        }
        return updatedPayments;
    }
}
