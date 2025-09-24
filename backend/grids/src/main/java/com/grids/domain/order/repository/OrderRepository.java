package com.grids.domain.order.repository;

import com.grids.domain.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    //주문 취소
    void deleteAllByIdIn(List<Integer> orderNumbers);
}
