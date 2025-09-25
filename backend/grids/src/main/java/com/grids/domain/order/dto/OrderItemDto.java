package com.grids.domain.order.dto;

import com.grids.domain.orderItem.entity.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemDto {
    private Long itemId;
    private Long orderItemId;
    private String orderItemName;
    private int orderQuantity;
    private Long orderPrice;

    public OrderItemDto(OrderItem orderItem) {
        this.itemId = orderItem.getItem().getId();
        this.orderItemId = orderItem.getId();
        this.orderItemName = orderItem.getItem().getName();
        this.orderQuantity = orderItem.getQuantity();
        this.orderPrice = orderItem.getSubTotalPrice();
    }
}