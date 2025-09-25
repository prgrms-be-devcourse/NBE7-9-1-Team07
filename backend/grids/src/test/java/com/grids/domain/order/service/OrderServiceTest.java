package com.grids.domain.order.service;

import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import com.grids.domain.order.dto.OrderItemDto;
import com.grids.domain.order.dto.OrderResponseDto;
import com.grids.domain.order.entity.Order;
import com.grids.domain.order.repository.OrderRepository;
import com.grids.domain.orderItem.entity.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.grids.domain.order.helper.OrderTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    // 테스트에서 공통으로 사용할 상수 데이터 정의
    private static final String TEST_USER_EMAIL = "user@example.com";
    private static final String ANOTHER_USER_EMAIL = "another@example.com";
    private static final String NON_EXISTENT_EMAIL = "nonexistent@example.com";

    // 테스트 메소드에서 참조할 수 있도록 엔티티를 필드로 선언
    private Order order1, order2, order3;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        // yml로 테스트용 db 경로를 하나 팔까 생각하다, 일단 팀원들과 상의해보고 수정하기로
        // 그 전 까지 임시
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        // Item 객체 생성 및 저장
        Item coffeeBean = createItem("커피콩 원두", 5000L);
        Item mug = createItem("머그컵", 8000L);
        Item tumbler = createItem("텀블러", 12000L);
        itemRepository.saveAll(List.of(coffeeBean, mug, tumbler));

        // Order 객체 생성 (DB 저장은 아래에서 한번에 처리)
        OrderItem order1Item1 = createOrderItem(coffeeBean, 2);
        OrderItem order1Item2 = createOrderItem(tumbler, 1);
        order1 = createOrder(TEST_USER_EMAIL, "서울시 강남구", "12345", order1Item1, order1Item2);

        OrderItem order2Item1 = createOrderItem(coffeeBean, 3);
        OrderItem order2Item2 = createOrderItem(mug, 1);
        order2 = createOrder(TEST_USER_EMAIL, "서울시 마포구", "54321", order2Item1, order2Item2);

        OrderItem order3Item1 = createOrderItem(coffeeBean, 1);
        order3 = createOrder(ANOTHER_USER_EMAIL, "부산시 해운대구", "98765", order3Item1);

        // 생성된 Order 객체들을 DB에 한번에 저장
        orderRepository.saveAll(List.of(order1, order2, order3));
    }

    @Test
    @DisplayName("이메일로 주문 내역을 조회하면, 해당 주문 목록을 최신순 및 정렬 요구사항에 맞게 반환한다.")
    void findOrdersByEmail_Success() {
        // given
        // setUp에서 생성한 TEST_USER_EMAIL의 주문 목록을 최신순으로 정렬 (ID 내림차순)
        List<Order> expectedOrders = List.of(order2, order1);

        // when
        List<OrderResponseDto> actualOrders = orderService.findOrdersByEmail(TEST_USER_EMAIL);

        // then
        // 1. 반환된 주문 목록의 개수가 예상과 일치하는지 확인
        assertThat(actualOrders).hasSize(expectedOrders.size());

        // 2. 각 주문의 내용이 정확하게 DTO로 변환되었는지 동적으로 확인
        for (int i = 0; i < expectedOrders.size(); i++) {
            Order expectedOrder = expectedOrders.get(i);
            OrderResponseDto actualOrderDto = actualOrders.get(i);

            // 2-1. 주문 기본 정보 및 배송 정보 검증
            assertThat(actualOrderDto.getOrderId()).isEqualTo(expectedOrder.getId());
            assertThat(actualOrderDto.getTotalPrice()).isEqualTo(expectedOrder.getTotalPrice());
            assertThat(actualOrderDto.getShippingDetails().getAddress()).isEqualTo(expectedOrder.getUserAddress());
            assertThat(actualOrderDto.getShippingDetails().getPostCode()).isEqualTo(expectedOrder.getUserZipCode());

            // 2-2. 주문 상품 목록의 개수 검증
            List<OrderItem> expectedOrderItems = expectedOrder.getOrderItems();
            List<OrderItemDto> actualOrderItemDtos = actualOrderDto.getOrderItems();
            assertThat(actualOrderItemDtos).hasSize(expectedOrderItems.size());

            // 2-3. 주문 상품 목록의 내용과 정렬 순서(이름 오름차순) 검증
            // 예상 상품 목록을 DTO의 정렬 기준(상품명 오름차순)과 동일하게 정렬
            List<OrderItem> sortedExpectedOrderItems = expectedOrderItems.stream()
                    .sorted(Comparator.comparing(oi -> oi.getItem().getName()))
                    .collect(Collectors.toList());

            for (int j = 0; j < sortedExpectedOrderItems.size(); j++) {
                OrderItem expectedItem = sortedExpectedOrderItems.get(j);
                OrderItemDto actualItemDto = actualOrderItemDtos.get(j);

                assertThat(actualItemDto.getOrderItemName()).isEqualTo(expectedItem.getItem().getName());
                assertThat(actualItemDto.getOrderQuantity()).isEqualTo(expectedItem.getQuantity());
                assertThat(actualItemDto.getOrderPrice()).isEqualTo(expectedItem.getSubTotalPrice());
                assertThat(actualItemDto.getItemId()).isEqualTo(expectedItem.getItem().getId());
            }
        }
    }

    @Test
    @DisplayName("주문 내역이 없는 이메일로 조회하면, 빈 배열을 반환한다.")
    void findOrdersByEmail_Empty() {
        // when
        List<OrderResponseDto> orders = orderService.findOrdersByEmail(NON_EXISTENT_EMAIL);

        // then
        assertThat(orders).isNotNull();
        assertThat(orders).isEmpty();
    }
}