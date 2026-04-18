package com.konate.music_application.apigateway.BusinessLayer.Order;

import com.konate.music_application.apigateway.PresentationLayer.Order.OrderRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Order.OrderResponseModel;
import com.konate.music_application.apigateway.domainClientLayer.Order.OrderServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{
    private final OrderServiceClient orderServiceClient;

    public OrderServiceImpl(OrderServiceClient orderServiceClient) {
        this.orderServiceClient = orderServiceClient;
    }

    @Override
    public List<OrderResponseModel> getAllOrders() {
        return orderServiceClient.getAllOrder();
    }

    @Override
    public OrderResponseModel getOrderById(String orderId) {
        return orderServiceClient.getOrderById(orderId);
    }

    @Override
    public List<OrderResponseModel> getAllOrdersForUser(String userId) {
        return orderServiceClient.getOrdersByCustomerId(userId);
    }

    @Override
    public void deleteOrder(String orderId) {
        orderServiceClient.cancelOrder(orderId);
    }

    @Override
    public OrderResponseModel createOrder(OrderRequestModel requestModel) {
        return orderServiceClient.createOrder(requestModel);
    }

    @Override
    public OrderResponseModel updateOrder(String orderId, OrderRequestModel requestModel) {
        return orderServiceClient.updateOrder(orderId, requestModel);
    }
}
