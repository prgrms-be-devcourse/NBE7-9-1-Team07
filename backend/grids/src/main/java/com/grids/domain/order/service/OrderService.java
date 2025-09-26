package com.grids.domain.order.service;

import com.grids.domain.order.dto.OrderItemDto;
import com.grids.domain.order.dto.OrderResponseDto;
import com.grids.domain.order.repository.OrderRepository;
import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import com.grids.domain.order.dto.OrderRequestDto;
import com.grids.domain.order.entity.Order;
import com.grids.domain.orderItem.entity.OrderItem;
import com.grids.domain.orderItem.dto.OrderItemRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    // 상품 주문 서비스
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


    // 주문 내역 조회 서비스
    public List<OrderResponseDto> findOrdersByEmail(String email) {

        // DTO로 변환
        List<OrderResponseDto> responseDtos = orderRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());

        return sortOrdersAsRequired(responseDtos);
    }

    // 책임 분리1: 정렬 메서드
    private List<OrderResponseDto> sortOrdersAsRequired(List<OrderResponseDto> responseDtos) {
        // 각 주문 내의 상품 목록을 상품명 기준 오름차순 정렬
        responseDtos.forEach(dto -> dto.getOrderItems()
                .sort(Comparator.comparing(OrderItemDto::getOrderItemName)));

        // 전체 주문 목록을 단위기간 기준 최신순 정렬
        responseDtos.sort(Comparator.comparing(
                        (OrderResponseDto dto) -> getUnitPeriodEndDate(LocalDateTime.parse(dto.getOrderName())))
                .reversed()); //내림차순

        return responseDtos;
    }

    // 정렬 메서드를 위한 하위 헬퍼 메서드
    private LocalDateTime getUnitPeriodEndDate(LocalDateTime orderDateTime) {

        // 단위기간의 경계가 되는 시간(당일 14시)을 비교 기준 객체로 생성.
        LocalDateTime periodBoundary = orderDateTime.toLocalDate().atTime(LocalTime.of(14, 0));

        // 주문 시간이 기준 시간(당일 14시)과 같거나 이후라면, 다음 날 14시가 단위기간의 종료 시점
        if (!orderDateTime.isBefore(periodBoundary)) {
            return periodBoundary.plusDays(1);
        }
        // 주문 시간이 기준 시간보다 이전이라면, 당일 14시가 단위기간의 종료 시점
        else {
            return periodBoundary;
        }
    }
}