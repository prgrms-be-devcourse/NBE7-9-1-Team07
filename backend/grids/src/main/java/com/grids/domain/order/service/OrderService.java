package com.grids.domain.order.service;

import com.grids.domain.order.dto.OrderResponseDto;
import com.grids.domain.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public List<OrderResponseDto> findOrdersByEmail(String email) {

        return orderRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }
}
