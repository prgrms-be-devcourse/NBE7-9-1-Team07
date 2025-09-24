package com.grids.domain.order.repository;

import com.grids.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // userEmail을 기준으로 조회, 생성일 기준 내림차순으로 정렬
    List<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail);
}
