package com.grids.domain.orderItem.service;

import com.grids.domain.orderItem.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public void removeByOrderItemIds(List<Long> orderItemIds) {
        orderItemRepository.deleteAllByIdIn(orderItemIds);
    }
}
