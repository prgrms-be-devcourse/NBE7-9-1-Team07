package com.grids.domain.order.service;



import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import com.grids.domain.order.dto.OrderRequestDto;
import com.grids.domain.order.dto.OrderResponseDto;
import com.grids.domain.order.entity.Order;
import com.grids.domain.order.repository.OrderRepository;
import com.grids.domain.orderItem.entity.OrderItem;
import com.grids.domain.orderItem.dto.OrderItemRequestDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        // 1. 주문 합치기 기준 시간 정의
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDateTime = now.withHour(14).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startDateTime;

        if (now.isBefore(endDateTime)) {
            startDateTime = endDateTime.minusDays(1);
        } else {
            startDateTime = endDateTime;
            endDateTime = endDateTime.plusDays(1);
        }

        // 2. 기준 시간 내 기존 주문 조회
        Optional<Order> existingOrderOpt = orderRepository.findFirstByUserEmailAndCreatedAtBetweenOrderByCreatedAtDesc(
                requestDto.getEmail(), startDateTime, endDateTime);



        Order finalOrder = existingOrderOpt
                .map(order -> mergeIntoExistingOrder(order, requestDto)) // 주문이 있으면 merge
                .orElseGet(() -> createNewOrder(requestDto));           // 주문이 없으면 신규 생성


        // 4. 응답 DTO 반환
        return new OrderResponseDto(finalOrder.getId(), finalOrder.getStatus(), finalOrder.getTotalPrice());
    }

    public Order createNewOrder(OrderRequestDto requestDto) {
        System.out.println("기존 주문이 없어 새로 생성합니다. (OrderCreator)");

        List<OrderItem> newOrderItems = new ArrayList<>();
        long totalNewPrice = 0;

        for (OrderItemRequestDto itemRequest : requestDto.getOrderItems()) {
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

    public Order mergeIntoExistingOrder(Order existingOrder, OrderRequestDto requestDto) {
        System.out.println("기존 주문을 찾았습니다. 주문 ID: " + existingOrder.getId() + " (OrderMerger)");

        long newItemsTotalPrice = 0;
        for (OrderItemRequestDto itemRequest : requestDto.getOrderItems()) {
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