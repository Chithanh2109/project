package com.skincare.repository;

import com.skincare.model.Order;
import com.skincare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
    List<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status);
} 