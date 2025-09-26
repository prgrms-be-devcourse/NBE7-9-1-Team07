package com.grids.domain.order.dto;

import com.grids.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    // 주문 자체 정보
    private Long orderId;
    private String orderName; // 주문 시점
    private String status; // 주문 상태
    private Long totalPrice;
    private ShippingDetailsDto shippingDetails; // 구매자 정보 Dto
    private List<OrderItemDto> orderItems; // 구매한 물건들

    // 여기서 주문 내역 필드 매핑 시작
    public OrderResponseDto(Order order) {
        this.orderId = order.getId();
        this.orderName = order.getCreatedAt().toString();
        this.status = order.getStatus().toString();
        this.totalPrice = order.getTotalPrice();
        this.shippingDetails = new ShippingDetailsDto(order); // 구매자 정보
        this.orderItems = order.getOrderItems().stream() // 구매한 물건들을 Dto로 다시 매핑
                .map(OrderItemDto::new)
                .collect(Collectors.toList());
    }

    public OrderResponseDto(Long id, String status, Long totalPrice) {
        this.orderId = id;
        this.status = status;
        this.totalPrice = totalPrice;
    }
}
