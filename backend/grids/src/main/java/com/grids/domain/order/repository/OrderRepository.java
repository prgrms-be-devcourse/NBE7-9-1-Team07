package com.grids.domain.order.repository;

import com.grids.domain.order.entity.Order;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // userEmail을 기준으로 조회, 생성일 기준 내림차순으로 정렬
    List<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    //bean 설정 때문에 이름이 많이 길어요
    Optional<Order> findFirstByUserEmailAndCreatedAtBetweenOrderByCreatedAtDesc(
            String userEmail,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );
}
