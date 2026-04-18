package com.konate.music_application.apigateway.PresentationLayer.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.konate.music_application.apigateway.domainClientLayer.Order.OrderItem;
import com.konate.music_application.apigateway.domainClientLayer.Order.OrderStatus;
import com.konate.music_application.apigateway.domainClientLayer.Order.Payment;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestModel {
    String userEmail;

    //enum
    OrderStatus orderStatus;

    //from the embedded objects
    List<OrderItem> orderItems;
    List<Payment> payments;

}
