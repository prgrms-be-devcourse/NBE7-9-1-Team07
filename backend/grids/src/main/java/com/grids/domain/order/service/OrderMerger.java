package com.grids.domain.order.service;

import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import com.grids.domain.order.dto.OrderRequestDto;
import com.grids.domain.order.entity.Order;
import com.grids.domain.orderItem.dto.OrderItemRequestDto;
import com.grids.domain.orderItem.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMerger {

    private final ItemRepository itemRepository;

    public Order mergeIntoExistingOrder(Order existingOrder, OrderRequestDto requestDto) {
        System.out.println("기존 주문을 찾았습니다. 주문 ID: " + existingOrder.getId() + " (OrderMerger)");

        long newItemsTotalPrice = 0;
        for (OrderItemRequestDto itemRequest : requestDto.getItems()) {
            Item item = findItemById(itemRequest.getItemId());
            long subTotalPrice = item.getPrice() * itemRequest.getQuantity();
            newItemsTotalPrice += subTotalPrice;

            OrderItem orderItem = OrderItem.builder()
                    .item(item)
                    .subTotalPrice(subTotalPrice)
                    .quantity(itemRequest.getQuantity())
                    .build();

            existingOrder.addOrderItem(orderItem);
        }

        existingOrder.updateTotal(existingOrder.getTotalPrice() + newItemsTotalPrice);
        return existingOrder;
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. ID: " + itemId));
    }
}