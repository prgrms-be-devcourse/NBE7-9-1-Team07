package com.grids.domain.order.service;

import com.grids.domain.order.dto.OrderItemDto;
import com.grids.domain.order.dto.OrderResponseDto;
import com.grids.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderResponseDto> findOrdersByEmail(String email) {

        // DTO로 변환
        List<OrderResponseDto> responseDtos = orderRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());

        // 각 주문 내의 상품 목록을 상품명 기준 오름차순 정렬
        responseDtos.forEach(dto -> dto.getOrderItems()
                .sort(Comparator.comparing(OrderItemDto::getItemName)));

        // 전체 주문 목록을 단위기간 기준 최신순 정렬
        responseDtos.sort(Comparator.comparing(
                        (OrderResponseDto dto) -> getUnitPeriodEndDate(LocalDateTime.parse(dto.getOrderName())))
                .reversed()); //내림차순

        return responseDtos;
    }

    // 헬퍼 메서드
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
