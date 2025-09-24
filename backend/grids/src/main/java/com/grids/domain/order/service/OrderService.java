package com.grids.domain.order.service;

import com.grids.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;



    //주문 취소
    @Transactional
    public void remove(List<Integer> orderNumbers) {
        orderRepository.deleteAllByIdIn(orderNumbers);
    }
}
