package com.grids.domain.order.dto;

import com.grids.domain.orderItem.entity.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemDto {
    private Long itemId;
    private String itemName;
    private int quantity;
    private Long price;

    public OrderItemDto(OrderItem orderItem) {
        this.itemId = orderItem.getItem().getId();
        this.itemName = orderItem.getItem().getName();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getSubTotalPrice();
    }
}