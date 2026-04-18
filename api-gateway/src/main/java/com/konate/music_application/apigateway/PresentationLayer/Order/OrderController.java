package com.konate.music_application.apigateway.PresentationLayer.Order;

import com.konate.music_application.apigateway.BusinessLayer.Order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(
            produces = "application/json"

    )
    public ResponseEntity<List<OrderResponseModel>> getAllOrders() {
        log.info("Getting all orders");
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @GetMapping(
            value = "/{orderId}",
            produces = "application/json"
    )
    public ResponseEntity<OrderResponseModel> getOrderById(@PathVariable String orderId) {
        log.info("Getting order by id: {}", orderId);
        return new ResponseEntity<>(orderService.getOrderById(orderId), HttpStatus.OK);
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<OrderResponseModel> createOrder(@RequestBody OrderRequestModel orderRequestModel) {
        log.info("Creating order with request: {}", orderRequestModel);
        return new ResponseEntity<>(orderService.createOrder(orderRequestModel), HttpStatus.CREATED);
    }

    @PutMapping(
            value = "/{orderId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<OrderResponseModel> updateOrder(@PathVariable String orderId, @RequestBody OrderRequestModel orderRequestModel) {
        log.info("Updating order with id: {}, request: {}", orderId, orderRequestModel);
        return new ResponseEntity<>(orderService.updateOrder(orderId, orderRequestModel), HttpStatus.OK);
    }

    @GetMapping(
            value = "/{userId}",
            produces = "application/json"
    )
    public ResponseEntity<List<OrderResponseModel>> getAllOrdersForUser(@PathVariable String userId) {
        log.info("Getting all orders for user with id: {}", userId);
        return new ResponseEntity<>(orderService.getAllOrdersForUser(userId), HttpStatus.OK);
    }

    @DeleteMapping(
            value = "/{orderId}",
            produces = "application/json"
    )
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        log.info("Deleting order with id: {}", orderId);
        orderService.deleteOrder(orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
