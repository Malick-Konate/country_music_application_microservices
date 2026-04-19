package com.konate.music_application.ordersubdomain.DataLayer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer>{

    Order findAllByOrderIdentifier_OrderId(String orderId);
//    List<Order> findAllByUserIdentifier_UserId(String userId);

    List<Order> findOrderByUserIdentifier(String userIdentifier);
//    List<Order> findAllByUserIdentifier_(UserIdentifier userIdentifier);
}
