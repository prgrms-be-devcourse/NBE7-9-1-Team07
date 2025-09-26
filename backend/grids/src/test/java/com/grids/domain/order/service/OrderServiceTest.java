package com.grids.domain.order.service;

import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import com.grids.domain.order.dto.OrderItemDto;
import com.grids.domain.order.dto.OrderRequestDto;
import com.grids.domain.order.dto.OrderResponseDto;
import com.grids.domain.order.entity.Order;
import com.grids.domain.order.repository.OrderRepository;
import com.grids.domain.orderItem.dto.OrderItemRequestDto;
import com.grids.domain.orderItem.entity.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    private Item coffeeBean, mug, tumbler;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        coffeeBean = createItem("커피콩 원두", 5000L);
        mug = createItem("머그컵", 8000L);
        tumbler = createItem("텀블러", 12000L);
        itemRepository.saveAll(List.of(coffeeBean, mug, tumbler));
    }

    @Test
    @DisplayName("기존 주문이 없을 시, 새로운 주문을 생성")
    void createOrder_WhenNoExistingOrder_ShouldCreateNewOrder() {
        // given
        List<OrderItemRequestDto> items = List.of(
                new OrderItemRequestDto(coffeeBean.getId(), 2), // 10000
                new OrderItemRequestDto(mug.getId(), 1)          // 8000
        );
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .email("new.user@example.com")
                .userAddress("서울시 강남구")
                .userZipCode("12345")
                .items(items)
                .build();

        // when
        OrderResponseDto responseDto = orderService.createOrder(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getOrderId()).isNotNull();
        assertThat(responseDto.getTotalPrice()).isEqualTo(18000L);
        assertThat(responseDto.getStatus()).isEqualTo("ORDERED");

        List<Order> orders = orderRepository.findByUserEmailOrderByCreatedAtDesc(requestDto.getEmail());
        assertThat(orders).hasSize(1);
        Order savedOrder = orders.get(0);
        assertThat(savedOrder.getId()).isEqualTo(responseDto.getOrderId());
        assertThat(savedOrder.getTotalPrice()).isEqualTo(18000L);
        assertThat(savedOrder.getOrderItems()).hasSize(2);
    }

    @Test
    @DisplayName("단위 시간 내 기존 주문이 있을 시, 해당 주문에 상품을 병합")
    void createOrder_WhenExistingOrderFound_ShouldMergeIntoIt() {
        // given
        OrderItem order1Item1 = createOrderItem(coffeeBean, 1); // 5000
        Order existingOrder = createOrder("existing.user@example.com", "서울시 마포구", "54321", order1Item1);
        orderRepository.save(existingOrder);
        long priceBeforeMerge = existingOrder.getTotalPrice();
        int itemsBeforeMerge = existingOrder.getOrderItems().size();

        List<OrderItemRequestDto> newItems = List.of(
                new OrderItemRequestDto(mug.getId(), 1) // 8000
        );
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .email("existing.user@example.com")
                .userAddress("서울시 마포구")
                .userZipCode("54321")
                .items(newItems)
                .build();

        // when
        OrderResponseDto responseDto = orderService.createOrder(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getOrderId()).isEqualTo(existingOrder.getId());
        assertThat(responseDto.getTotalPrice()).isEqualTo(priceBeforeMerge + 8000L);

        Order mergedOrder = orderRepository.findById(existingOrder.getId()).orElseThrow();
        assertThat(mergedOrder.getOrderItems()).hasSize(itemsBeforeMerge + 1);
        assertThat(mergedOrder.getTotalPrice()).isEqualTo(priceBeforeMerge + 8000L);

        List<Order> orders = orderRepository.findByUserEmailOrderByCreatedAtDesc("existing.user@example.com");
        assertThat(orders).hasSize(1);
    }

    @Test
    @DisplayName("기존 주문이 단위 시간을 벗어났을 경우, 새로 주문을 생성")
    void createOrder_WhenExistingOrderIsOutsideTimeWindow_ShouldCreateNewOrder() throws ReflectiveOperationException {
        // given
        OrderItem oldOrderItem = createOrderItem(coffeeBean, 1);
        Order oldOrder = createOrder("old.order.user@example.com", "부산시 해운대구", "98765", oldOrderItem);
        setEntityField(oldOrder, "createdAt", LocalDateTime.now().minusDays(2));
        orderRepository.save(oldOrder);

        List<OrderItemRequestDto> newItems = List.of(
                new OrderItemRequestDto(tumbler.getId(), 1) // 12000
        );
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .email("old.order.user@example.com")
                .userAddress("부산시 해운대구")
                .userZipCode("98765")
                .items(newItems)
                .build();

        // when
        OrderResponseDto responseDto = orderService.createOrder(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getOrderId()).isNotEqualTo(oldOrder.getId());
        assertThat(responseDto.getTotalPrice()).isEqualTo(12000L);

        List<Order> orders = orderRepository.findByUserEmailOrderByCreatedAtDesc("old.order.user@example.com");
        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).getId()).isEqualTo(responseDto.getOrderId());
    }

    private static final String TEST_USER_EMAIL = "user@example.com";
    private static final String ANOTHER_USER_EMAIL = "another@example.com";
    private static final String NON_EXISTENT_EMAIL = "nonexistent@example.com";

    @Test
    @DisplayName("이메일로 주문 내역을 조회하면, 해당 주문 목록을 최신순 및 정렬 요구사항에 맞게 반환한다.")
    void findOrdersByEmail_Success() {
        // given
        OrderItem order1Item1 = createOrderItem(coffeeBean, 2);
        OrderItem order1Item2 = createOrderItem(tumbler, 1);
        Order order1 = createOrder(TEST_USER_EMAIL, "서울시 강남구", "12345", order1Item1, order1Item2);

        OrderItem order2Item1 = createOrderItem(coffeeBean, 3);
        OrderItem order2Item2 = createOrderItem(mug, 1);
        Order order2 = createOrder(TEST_USER_EMAIL, "서울시 마포구", "54321", order2Item1, order2Item2);
        orderRepository.saveAll(List.of(order1, order2));

        List<Order> expectedOrders = List.of(order2, order1);

        // when
        List<OrderResponseDto> actualOrders = orderService.findOrdersByEmail(TEST_USER_EMAIL);

        // then
        assertThat(actualOrders).hasSize(expectedOrders.size());

        for (int i = 0; i < expectedOrders.size(); i++) {
            Order expectedOrder = expectedOrders.get(i);
            OrderResponseDto actualOrderDto = actualOrders.get(i);

            assertThat(actualOrderDto.getOrderId()).isEqualTo(expectedOrder.getId());
            assertThat(actualOrderDto.getTotalPrice()).isEqualTo(expectedOrder.getTotalPrice());
            assertThat(actualOrderDto.getShippingDetails().getAddress()).isEqualTo(expectedOrder.getUserAddress());
            assertThat(actualOrderDto.getShippingDetails().getPostCode()).isEqualTo(expectedOrder.getUserZipCode());

            List<OrderItem> expectedOrderItems = expectedOrder.getOrderItems();
            List<OrderItemDto> actualOrderItemDtos = actualOrderDto.getOrderItems();
            assertThat(actualOrderItemDtos).hasSize(expectedOrderItems.size());

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