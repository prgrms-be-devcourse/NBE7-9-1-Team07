package com.grids.domain.order.service;

import com.grids.domain.order.dto.OrderResponseDto;
import com.grids.domain.order.entity.Order;
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
        List<Order> orders = orderRepository.findByUserEmailOrderByCreatedAtDesc(email);

        return orders.stream()
                .map(this::mapOrderToResponseDto)
                .collect(Collectors.toList());
    }

    // Order Entity를 OrderResponseDto로 변환하는 메서드
    private OrderResponseDto mapOrderToResponseDto(Order order) {
        // 1. ShippingDetailsDto 생성
        OrderResponseDto.ShippingDetailsDto shippingDetails = new OrderResponseDto.ShippingDetailsDto(
                order.getUserEmail(),
                order.getUserAddress(),
                order.getUserZipCode()
        );

        // 2. OrderItemDto 리스트 생성
        List<OrderResponseDto.OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                .map(orderItem -> new OrderResponseDto.OrderItemDto(
                        orderItem.getItem().getId(),
                        orderItem.getItem().getName(),
                        orderItem.getQuantity(),
                        orderItem.getSubTotalPrice()
                )).collect(Collectors.toList());

        // 3. 최종 OrderResponseDto 생성 및 반환
        return new OrderResponseDto(
                order.getId(),
                order.getCreatedAt().toString(),
                order.getStatus(),
                order.getTotalPrice(),
                shippingDetails,
                orderItemDtos
        );
    }
}
