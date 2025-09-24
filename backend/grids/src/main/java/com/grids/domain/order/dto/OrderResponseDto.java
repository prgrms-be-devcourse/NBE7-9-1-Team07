package com.grids.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    // 주문 자체 정보
    private Long orderId;
    private String orderName;
    private String orderStatus;
    private Long totalPrice;
    private ShippingDetailsDto shippingDetails;
    private List<OrderItemDto> orderItems;

    // 내부 Dto1: 구매자 정보
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingDetailsDto {
        private String recipientEmail;
        private String address;
        private String postCode;
    }

    // 내부 Dto2: 구매한 물건들
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private Long itemId;
        private String itemName;
        private int quantity;
        private Long price;
    }
}
