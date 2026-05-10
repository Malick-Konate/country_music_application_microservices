package com.konate.music_application.ordersubdomain.DataLayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        orderRepository.deleteAll();
    }

    // =========================================================================
    // FIND ALL
    // =========================================================================

    @Test
    public void whenOrdersExist_thenReturnAllOrders() {
        orderRepository.save(buildSampleOrder("ord-001", "user-001"));
        orderRepository.save(buildSampleOrder("ord-002", "user-002"));

        List<Order> orders = orderRepository.findAll();

        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    @Test
    public void whenNoOrdersExist_thenReturnEmptyList() {
        List<Order> orders = orderRepository.findAll();

        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    // =========================================================================
    // FIND BY ORDER ID
    // =========================================================================

    @Test
    public void whenOrderExists_thenReturnOrderByOrderId() {
        String targetOrderId = "ord-999";
        orderRepository.save(buildSampleOrder(targetOrderId, "user-001"));

        Order found = orderRepository.findAllByOrderIdentifier_OrderId(targetOrderId);

        assertNotNull(found);
        assertEquals(targetOrderId, found.getOrderIdentifier().getOrderId());
        assertEquals(OrderStatus.PENDING, found.getOrderStatus());
    }

    @Test
    public void whenOrderDoesNotExist_thenReturnNull() {
        Order found = orderRepository.findAllByOrderIdentifier_OrderId("non-existent-id");

        assertNull(found);
    }

    // =========================================================================
    // FIND BY USER ID
    // =========================================================================

    @Test
    public void whenUserHasOrders_thenReturnAllOrdersForUser() {
        // 2 orders for user-001, 1 for user-002
        orderRepository.save(buildSampleOrder("ord-001", "user-001"));
        orderRepository.save(buildSampleOrder("ord-002", "user-001"));
        orderRepository.save(buildSampleOrder("ord-003", "user-002"));

        List<Order> userOrders = orderRepository.findOrderByUserIdentifier("user-001");

        assertNotNull(userOrders);
        assertEquals(2, userOrders.size());
        userOrders.forEach(o -> assertEquals("user-001", o.getUserIdentifier()));
    }

    @Test
    public void whenUserHasNoOrders_thenReturnEmptyList() {
        List<Order> orders = orderRepository.findOrderByUserIdentifier("user-no-orders");

        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    // =========================================================================
    // PERSIST — Items & Payments
    // =========================================================================

    @Test
    public void whenSaveOrder_thenOrderItemsArePersisted() {
        orderRepository.save(buildSampleOrder("ord-100", "user-001"));

        Order found = orderRepository.findAllByOrderIdentifier_OrderId("ord-100");

        assertNotNull(found);
        assertNotNull(found.getOrderItems());
        assertEquals(1, found.getOrderItems().size());
        assertEquals(ProductType.ALBUM_PURCHASE, found.getOrderItems().get(0).getProductType());
        assertEquals("Cowboy Sunset", found.getOrderItems().get(0).getDisplayName());
        assertEquals(new BigDecimal("14.99"), found.getOrderItems().get(0).getPrice());
    }

    @Test
    public void whenSaveOrder_thenPaymentsArePersisted() {
        orderRepository.save(buildSampleOrder("ord-200", "user-001"));

        Order found = orderRepository.findAllByOrderIdentifier_OrderId("ord-200");

        assertNotNull(found);
        assertNotNull(found.getPayments());
        assertEquals(1, found.getPayments().size());
        assertEquals(PaymentMethod.CREDIT_CARD, found.getPayments().get(0).getMethod());
        assertEquals(PaymentStatus.PENDING, found.getPayments().get(0).getPaymentStatus());
        assertEquals(new BigDecimal("14.99"), found.getPayments().get(0).getAmount());
    }

    @Test
    public void whenSaveOrderWithMultipleItems_thenAllItemsPersisted() {
        Order order = buildSampleOrder("ord-300", "user-001");
        order.getOrderItems().add(new OrderItem(
                ProductType.PODCAST_SUBSCRIPTION,
                "Tech Talk Daily",
                "John Host",
                new BigDecimal("4.99"),
                1
        ));
        orderRepository.save(order);

        Order found = orderRepository.findAllByOrderIdentifier_OrderId("ord-300");

        assertNotNull(found);
        assertEquals(2, found.getOrderItems().size());
    }

    @Test
    public void whenSaveOrderWithMultiplePayments_thenAllPaymentsPersisted() {
        Order order = buildSampleOrder("ord-400", "user-001");
        order.getPayments().add(new Payment(
                new BigDecimal("5.00"),
                LocalDateTime.now(),
                PaymentMethod.PAYPAL,
                PaymentStatus.PENDING,
                "USD"
        ));
        orderRepository.save(order);

        Order found = orderRepository.findAllByOrderIdentifier_OrderId("ord-400");

        assertNotNull(found);
        assertEquals(2, found.getPayments().size());
    }

    // =========================================================================
    // DELETE
    // =========================================================================

    @Test
    public void whenDeleteOrder_thenNotFound() {
        Order saved = orderRepository.save(buildSampleOrder("ord-000", "user-001"));
        String savedOrderId = saved.getOrderIdentifier().getOrderId();

        orderRepository.delete(saved);
        Order found = orderRepository.findAllByOrderIdentifier_OrderId(savedOrderId);

        assertNull(found, "Order should no longer exist after deletion");
    }

    @Test
    public void whenDeleteOneOrder_thenOtherOrdersRemain() {
        Order toDelete = orderRepository.save(buildSampleOrder("ord-del", "user-001"));
        orderRepository.save(buildSampleOrder("ord-keep", "user-001"));

        orderRepository.delete(toDelete);

        assertNull(orderRepository.findAllByOrderIdentifier_OrderId("ord-del"));
        assertNotNull(orderRepository.findAllByOrderIdentifier_OrderId("ord-keep"));
        assertEquals(1, orderRepository.findAll().size());
    }

    // =========================================================================
    // UPDATE
    // =========================================================================

    @Test
    public void whenUpdateOrderStatus_thenStatusIsChanged() {
        // ✅ Fix: use new ArrayList<>() in buildSampleOrder so Hibernate can mutate the collection
        orderRepository.save(buildSampleOrder("ord-555", "user-001"));

        Order existing = orderRepository.findAllByOrderIdentifier_OrderId("ord-555");
        existing.setOrderStatus(OrderStatus.COMPLETED);
        orderRepository.save(existing);

        Order updated = orderRepository.findAllByOrderIdentifier_OrderId("ord-555");

        assertNotNull(updated);
        assertEquals(OrderStatus.COMPLETED, updated.getOrderStatus());
    }

    @Test
    public void whenCancelOrder_thenStatusIsCancelled() {
        orderRepository.save(buildSampleOrder("ord-666", "user-001"));

        Order existing = orderRepository.findAllByOrderIdentifier_OrderId("ord-666");
        existing.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(existing);

        Order updated = orderRepository.findAllByOrderIdentifier_OrderId("ord-666");

        assertNotNull(updated);
        assertEquals(OrderStatus.CANCELLED, updated.getOrderStatus());
    }

    @Test
    public void whenSaveOrderWithCustomId_thenRetrieveByIdWorks() {
        Order order = buildSampleOrder("ignored", "user-001");
        order.setOrderIdentifier(new OrderIdentifier("ORD-CUSTOM-999"));

        orderRepository.save(order);
        Order found = orderRepository.findAllByOrderIdentifier_OrderId("ORD-CUSTOM-999");

        assertNotNull(found);
        assertEquals("ORD-CUSTOM-999", found.getOrderIdentifier().getOrderId());
    }

    // =========================================================================
    // Helper — ArrayList is REQUIRED: List.of() is immutable and
    // Hibernate needs to clear/repopulate @ElementCollection lists internally
    // =========================================================================

    private Order buildSampleOrder(String orderId, String userId) {
        Order order = new Order();
        order.setOrderIdentifier(new OrderIdentifier(orderId));
        order.setUserIdentifier(userId);
        order.setOrderStatus(OrderStatus.PENDING);

        order.setOrderItems(new ArrayList<>(List.of(
                new OrderItem(
                        ProductType.ALBUM_PURCHASE,
                        "Cowboy Sunset",
                        "Reba McEntire",
                        new BigDecimal("14.99"),
                        1
                )
        )));

        order.setPayments(new ArrayList<>(List.of(
                new Payment(
                        new BigDecimal("14.99"),
                        LocalDateTime.now(),
                        PaymentMethod.CREDIT_CARD,
                        PaymentStatus.PENDING,
                        "USD"
                )
        )));

        return order;
    }
}