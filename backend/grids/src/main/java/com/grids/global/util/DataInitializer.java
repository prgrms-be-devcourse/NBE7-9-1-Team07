package com.grids.global.util;

import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import com.grids.domain.order.entity.Order;
import com.grids.domain.order.repository.OrderRepository;
import com.grids.domain.orderItem.entity.OrderItem;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 테스트용 Item 데이터 생성
        Item item1 = Item.builder().name("커피콩 원두").price(5000L).category("원두").image("image_url_1").build();
        Item item2 = Item.builder().name("머그컵").price(8000L).category("굿즈").image("image_url_2").build();
        Item item3 = Item.builder().name("텀블러").price(12000L).category("굿즈").image("image_url_3").build();
        itemRepository.saveAll(Arrays.asList(item1, item2, item3));

        Order order1 = com.grids.domain.order.entity.Order.builder()
                .userEmail("user@example.com")
                .userAddress("서울시 강남구")
                .userZipCode("12345")
                .totalPrice(22000L)
                .status("배송준비중")
                .build();
        order1.addOrderItem(createOrderItem(item1, 2));
        order1.addOrderItem(createOrderItem(item3, 1));
        orderRepository.save(order1);

        Order order2 = com.grids.domain.order.entity.Order.builder()
                .userEmail("user@example.com")
                .userAddress("서울시 마포구")
                .userZipCode("54321")
                .totalPrice(8000L)
                .status("배송준비중")
                .build();
        order2.addOrderItem(createOrderItem(item2, 1));
        orderRepository.save(order2);

        Order order3 = Order.builder()
                .userEmail("another@example.com")
                .userAddress("부산시 해운대구")
                .userZipCode("98765")
                .totalPrice(5000L)
                .status("배송완료")
                .build();
        order3.addOrderItem(createOrderItem(item1, 1));
        orderRepository.save(order3);
    }

    private OrderItem createOrderItem(Item item, int quantity) {
        return OrderItem.builder()
                .item(item)
                .quantity(quantity)
                .subTotalPrice(item.getPrice() * quantity)
                .build();
    }
}
