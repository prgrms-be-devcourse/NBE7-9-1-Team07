package com.grids.domain.order.service;

import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import com.grids.domain.order.dto.OrderRequestDto;
import com.grids.domain.order.entity.Order;
import com.grids.domain.order.repository.OrderRepository;
import com.grids.domain.orderItem.dto.OrderItemRequestDto;
import com.grids.domain.orderItem.entity.OrderItem;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreator {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    public Order createNewOrder(OrderRequestDto requestDto) {
        System.out.println("기존 주문이 없어 새로 생성합니다. (OrderCreator)");

        List<OrderItem> newOrderItems = new ArrayList<>();
        long totalNewPrice = 0;

        for (OrderItemRequestDto itemRequest : requestDto.getItems()) {
            Item item = findItemById(itemRequest.getItemId());
            long subTotalPrice = item.getPrice() * itemRequest.getQuantity();
            totalNewPrice += subTotalPrice;

            OrderItem orderItem = OrderItem.builder()
                    .item(item)
                    .subTotalPrice(subTotalPrice)
                    .quantity(itemRequest.getQuantity())
                    .build();
            newOrderItems.add(orderItem);
        }

        Order newOrder = Order.builder()
                .userEmail(requestDto.getEmail())
                .userAddress(requestDto.getUserAddress())
                .userZipCode(requestDto.getUserZipCode())
                .totalPrice(totalNewPrice)
                .status("ORDERED")
                .build();

        for (OrderItem orderItem : newOrderItems) {
            newOrder.addOrderItem(orderItem);
        }

        return orderRepository.save(newOrder);
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. ID: " + itemId));
    }
}