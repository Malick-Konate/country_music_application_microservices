package com.konate.music_application.ordersubdomain.PresentationLayer;

import com.konate.music_application.ordersubdomain.DataLayer.OrderItem;
import com.konate.music_application.ordersubdomain.DataLayer.OrderStatus;
import com.konate.music_application.ordersubdomain.DataLayer.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
