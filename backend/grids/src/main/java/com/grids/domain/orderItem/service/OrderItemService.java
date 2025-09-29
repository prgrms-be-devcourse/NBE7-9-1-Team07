package com.grids.domain.orderItem.service;

import com.grids.domain.item.entity.Item;
import com.grids.domain.order.dto.OrderItemDto;
import com.grids.domain.order.entity.Order;
import com.grids.domain.order.repository.OrderRepository;
import com.grids.domain.order.service.OrderService;
import com.grids.domain.orderItem.entity.OrderItem;
import com.grids.domain.orderItem.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository; // OrderService 대신 직접 주입

    @Transactional
    public void removeByOrderItemIds(List<Long> orderItemIds) {
        List<OrderItem> orderItems = getOrderItems(orderItemIds);
        updateOrdersPrice(orderItems);
        orderItemRepository.deleteByIdIn(orderItemIds);
    }

    @Transactional
    public OrderItem createOrderItem(Order order, Item item, int quantity) {
        Long subTotalPrice = item.getPrice() * quantity;
        return orderItemRepository.save(new OrderItem(order, item, subTotalPrice, quantity));
    }

    public List<OrderItem> getOrderItems(List<Long> ids) {
        return orderItemRepository.findAllById(ids);
    }

    private void updateOrdersPrice(List<OrderItem> orderItems) {
        Map<Order, Long> reduceMap = orderItems.stream()
                .collect(Collectors.groupingBy(
                        OrderItem::getOrder,
                        Collectors.summingLong(OrderItem::getSubTotalPrice)
                ));

        for (Map.Entry<Order, Long> entry : reduceMap.entrySet()) {
            Order order = entry.getKey();
            Long reduceAmount = entry.getValue();

            long newTotal = order.getTotalPrice() - reduceAmount;
            if (newTotal < 0) newTotal = 0;
            order.updateTotal(newTotal);
        }
    }
}
