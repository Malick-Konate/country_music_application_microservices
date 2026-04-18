package com.konate.music_application.ordersubdomain.BusinessLayer;


import com.konate.music_application.ordersubdomain.PresentationLayer.OrderRequestModel;
import com.konate.music_application.ordersubdomain.PresentationLayer.OrderResponseModel;

import java.util.List;

public interface OrderService {
    List<OrderResponseModel> getAllOrders();

    OrderResponseModel getOrderById(String orderId);

    List<OrderResponseModel> getAllOrdersForUser(String userId);

    void deleteOrder(String orderId);

    OrderResponseModel createOrder(OrderRequestModel requestModel);

    OrderResponseModel updateOrder(String orderId, OrderRequestModel requestModel);
}
