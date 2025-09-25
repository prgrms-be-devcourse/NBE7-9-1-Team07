package com.grids.domain.order.service;

import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
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

import java.util.List;

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

    @BeforeEach
    void setUp() {
        Item item1 = createItem("커피콩 원두", 5000L);
        Item item2 = createItem("머그컵", 8000L);
        Item item3 = createItem("텀블러", 12000L);
        itemRepository.saveAll(List.of(item1, item2, item3));

        OrderItem order1Item1 = createOrderItem(item1, 2);
        OrderItem order1Item2 = createOrderItem(item3, 1);
        Order order1 = createOrder("user@example.com", "서울시 강남구", "12345", order1Item1, order1Item2);

        OrderItem order2Item1 = createOrderItem(item1, 3);
        OrderItem order2Item2 = createOrderItem(item2, 1);
        Order order2 = createOrder("user@example.com", "서울시 마포구", "54321", order2Item1, order2Item2);

        OrderItem order3Item1 = createOrderItem(item1, 1);
        Order order3 = createOrder("another@example.com", "부산시 해운대구", "98765", order3Item1);

        orderRepository.saveAll(List.of(order1, order2, order3));
    }

    @Test
    @DisplayName("이메일로 주문 내역을 조회하면, 해당 주문 목록을 최신순 및 정렬 요구사항에 맞게 반환한다.")
    void findOrdersByEmail_Success() {
        String email = "user@example.com";

        List<OrderResponseDto> orders = orderService.findOrdersByEmail(email);

        assertThat(orders).hasSize(2);

        OrderResponseDto latestOrder = orders.get(0);
        assertThat(latestOrder.getShippingDetails().getAddress()).isEqualTo("서울시 마포구");
        assertThat(latestOrder.getTotalPrice()).isEqualTo(23000L);
        assertThat(latestOrder.getOrderItems()).hasSize(2);
        assertThat(latestOrder.getOrderItems().get(0).getOrderItemName()).isEqualTo("머그컵");
        assertThat(latestOrder.getOrderItems().get(1).getOrderItemName()).isEqualTo("커피콩 원두");

        OrderResponseDto previousOrder = orders.get(1);
        assertThat(previousOrder.getShippingDetails().getAddress()).isEqualTo("서울시 강남구");
        assertThat(previousOrder.getTotalPrice()).isEqualTo(22000L);
        assertThat(previousOrder.getOrderItems()).hasSize(2);
        assertThat(previousOrder.getOrderItems().get(0).getOrderItemName()).isEqualTo("커피콩 원두");
        assertThat(previousOrder.getOrderItems().get(1).getOrderItemName()).isEqualTo("텀블러");
    }

    @Test
    @DisplayName("주문 내역이 없는 이메일로 조회하면, 빈 배열을 반환한다.")
    void findOrdersByEmail_Empty() {
        String email = "nonexistent@example.com";

        List<OrderResponseDto> orders = orderService.findOrdersByEmail(email);

        assertThat(orders).isNotNull();
        assertThat(orders).isEmpty();
    }
}