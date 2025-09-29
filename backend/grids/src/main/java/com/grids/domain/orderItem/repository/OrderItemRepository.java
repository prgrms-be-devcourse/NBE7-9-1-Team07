package com.grids.domain.orderItem.repository;

import com.grids.domain.orderItem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    //주문 항목 취소
    void deleteAllByIdIn(List<Long> orderItemIds);
}
