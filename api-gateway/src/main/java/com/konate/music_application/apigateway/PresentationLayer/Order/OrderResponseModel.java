package com.konate.music_application.apigateway.PresentationLayer.Order;

import com.konate.music_application.apigateway.domainClientLayer.Order.OrderItem;
import com.konate.music_application.apigateway.domainClientLayer.Order.OrderStatus;
import com.konate.music_application.apigateway.domainClientLayer.Order.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseModel extends RepresentationModel<OrderResponseModel> {

    String orderId;

    //from user service
    String email;
    String fullname;
    String country;

    //enum
    OrderStatus orderStatus;

    //from the embedded objects
    List<OrderItem> orderItems;
    List<Payment> payments;


}
