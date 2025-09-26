package com.grids.domain.order.service;

import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import com.grids.domain.order.dto.OrderItemDto;
import com.grids.domain.order.dto.OrderRequestDto;
import com.grids.domain.order.dto.OrderResponseDto;
import com.grids.domain.order.entity.Order;
import com.grids.domain.order.repository.OrderRepository;
import com.grids.domain.orderItem.entity.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.grids.domain.order.helper.OrderTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Spy
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository; // ItemRepository Mock 추가

    private static final String TEST_USER_EMAIL = "user@example.com";
    private static final String NON_EXISTENT_EMAIL = "nonexistent@example.com";

    @Test
    @DisplayName("이메일로 주문 내역을 조회하면, 해당 주문 목록을 최신순 및 정렬 요구사항에 맞게 반환한다.")
    void findOrdersByEmail_Success() throws ReflectiveOperationException {
        // given
        Item coffeeBean = createItem("커피콩 원두", 5000L);
        Item mug = createItem("머그컵", 8000L);
        Item tumbler = createItem("텀블러", 12000L);
        setEntityField(coffeeBean, "id", 1L);
        setEntityField(mug, "id", 2L);
        setEntityField(tumbler, "id", 3L);

        OrderItem order1Item1 = createOrderItem(coffeeBean, 2);
        OrderItem order1Item2 = createOrderItem(tumbler, 1);
        Order order1 = createOrder(TEST_USER_EMAIL, "서울시 강남구", "12345", order1Item1, order1Item2);
        setEntityField(order1, "id", 1L);
        setEntityField(order1, "createdAt", LocalDateTime.now().minusHours(1));


        OrderItem order2Item1 = createOrderItem(coffeeBean, 3);
        OrderItem order2Item2 = createOrderItem(mug, 1);
        Order order2 = createOrder(TEST_USER_EMAIL, "서울시 마포구", "54321", order2Item1, order2Item2);
        setEntityField(order2, "id", 2L);
        setEntityField(order2, "createdAt", LocalDateTime.now());

        List<Order> expectedOrders = List.of(order2, order1);
        when(orderRepository.findByUserEmailOrderByCreatedAtDesc(TEST_USER_EMAIL)).thenReturn(expectedOrders);

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
        // given
        when(orderRepository.findByUserEmailOrderByCreatedAtDesc(NON_EXISTENT_EMAIL)).thenReturn(Collections.emptyList());

        // when
        List<OrderResponseDto> orders = orderService.findOrdersByEmail(NON_EXISTENT_EMAIL);

        // then
        assertThat(orders).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("기존 주문이 없을 시, 새로운 주문을 생성")
    void createOrder_WhenNoExistingOrder_ShouldCreateNewOrder() throws ReflectiveOperationException {
        // given
        OrderRequestDto requestDto = createOrderRequestDto();
        Order newOrder = createNewOrderStub(requestDto);
        setEntityField(newOrder, "id", 1L);

        when(orderRepository.findFirstByUserEmailAndCreatedAtBetweenOrderByCreatedAtDesc(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        // Spy 객체의 메서드를 Stubbing 할 때는 doReturn().when() 사용
        doReturn(newOrder).when(orderService).createNewOrder(any(OrderRequestDto.class));

        // when
        OrderResponseDto responseDto = orderService.createOrder(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getOrderId()).isEqualTo(1L);
        assertThat(responseDto.getTotalPrice()).isEqualTo(30000L);

        verify(orderService, times(1)).createNewOrder(requestDto);
        verify(orderService, never()).mergeIntoExistingOrder(any(), any());
    }

    @Test
    @DisplayName("기존 주문이 있을 시, 해당 주문에 상품을 병합")
    void createOrder_WhenExistingOrderFound_ShouldMergeIntoIt() throws ReflectiveOperationException {
        // given
        OrderRequestDto requestDto = createOrderRequestDto();
        Order existingOrder = createExistingOrderStub();
        setEntityField(existingOrder, "id", 1L);
        Order mergedOrder = createMergedOrderStub(existingOrder);
        setEntityField(mergedOrder, "id", 1L); // 병합 후에도 ID는 동일해야 함

        when(orderRepository.findFirstByUserEmailAndCreatedAtBetweenOrderByCreatedAtDesc(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(existingOrder));
        doReturn(mergedOrder).when(orderService).mergeIntoExistingOrder(any(Order.class), any(OrderRequestDto.class));

        // when
        OrderResponseDto responseDto = orderService.createOrder(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getOrderId()).isEqualTo(1L); // 병합된 주문 ID 검증
        assertThat(responseDto.getTotalPrice()).isEqualTo(80000L);

        verify(orderService, times(1)).mergeIntoExistingOrder(existingOrder, requestDto);
        verify(orderService, never()).createNewOrder(any());
    }

    @Test
    @DisplayName("기존 주문이 단위 시간을 벗어났을 경우, 새로 주문을 생성")
    void createOrder_WhenExistingOrderIsOutsideTimeWindow_ShouldCreateNewOrder() throws ReflectiveOperationException {
        // given
        OrderRequestDto requestDto = createOrderRequestDto();
        Order newOrder = createNewOrderStub(requestDto);
        setEntityField(newOrder, "id", 100L);

        when(orderRepository.findFirstByUserEmailAndCreatedAtBetweenOrderByCreatedAtDesc(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        doReturn(newOrder).when(orderService).createNewOrder(any(OrderRequestDto.class));

        // when
        OrderResponseDto responseDto = orderService.createOrder(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getOrderId()).isEqualTo(100L);

        verify(orderService, times(1)).createNewOrder(requestDto);
        verify(orderService, never()).mergeIntoExistingOrder(any(), any());
    }
}