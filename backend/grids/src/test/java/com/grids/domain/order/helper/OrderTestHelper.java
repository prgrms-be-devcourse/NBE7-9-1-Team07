package com.grids.domain.order.helper;

import com.grids.domain.order.dto.OrderRequestDto;
import com.grids.domain.order.entity.Order;
import com.grids.domain.orderItem.dto.OrderItemRequestDto;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Order 관련 테스트에서 공통으로 사용할 객체 생성 및 유틸리티 메서드를 제공하는 헬퍼 클래스입니다.
 * 모든 메서드는 static으로 선언되어 객체 생성 없이 바로 사용할 수 있습니다.
 */
public class OrderTestHelper {

    // 표준 테스트용 DTO를 생성합니다.
    public static OrderRequestDto createOrderRequestDto() {
        List<OrderItemRequestDto> items = List.of(
                new OrderItemRequestDto(1L, 2),
                new OrderItemRequestDto(2L, 1)
        );
        return OrderRequestDto.builder()
                .email("test@example.com")
                .userAddress("서울시 강남구")
                .userZipCode("12345")
                .orderItems(items)
                .build();
    }

    // 신규 주문 생성 시 반환될 Stub 객체를 생성합니다.
    public static Order createNewOrderStub(OrderRequestDto requestDto) {
        return Order.builder()
                .userEmail(requestDto.getEmail())
                .userAddress(requestDto.getUserAddress())
                .userZipCode(requestDto.getUserZipCode())
                .status("ORDERED")
                .totalPrice(30000L)
                .build();
    }

    // 기존 주문을 나타내는 Stub 객체를 생성합니다.
    public static Order createExistingOrderStub() {
        return Order.builder()
                .userEmail("test@example.com")
                .userAddress("서울시 강남구")
                .userZipCode("12345")
                .status("ORDERED")
                .totalPrice(50000L)
                .build();
    }

    // 병합된 주문을 나타내는 Stub 객체를 생성합니다.
    public static Order createMergedOrderStub(Order existingOrder) {
        long newItemsPrice = 30000L;
        return Order.builder()
                .userEmail(existingOrder.getUserEmail())
                .userAddress(existingOrder.getUserAddress())
                .userZipCode(existingOrder.getUserZipCode())
                .status("ORDERED")
                .totalPrice(existingOrder.getTotalPrice() + newItemsPrice)
                .build();
    }

    // 리플렉션을 사용해 엔티티의 필드 값을 설정하는 유틸리티 메서드입니다.
    public static void setEntityField(Object entity, String fieldName, Object value) throws ReflectiveOperationException {
        Class<?> clazz = entity.getClass();
        while (clazz != null && clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(entity, value);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Failed to find '" + fieldName + "' field in class hierarchy.");
    }
}