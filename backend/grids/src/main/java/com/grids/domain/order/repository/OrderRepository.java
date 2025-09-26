package com.grids.domain.order.repository;

import com.grids.domain.order.entity.Order;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    //bean 설정 때문에 이름이 많이 길어요
    Optional<Order> findFirstByUserEmailAndCreatedAtBetweenOrderByCreatedAtDesc(
            String userEmail,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );


}
