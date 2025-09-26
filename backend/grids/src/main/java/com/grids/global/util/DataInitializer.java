package com.grids.global.util;

import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import com.grids.domain.order.entity.Order;
import com.grids.domain.order.repository.OrderRepository;
import com.grids.domain.orderItem.entity.OrderItem;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("!test") // test 프로필에서는 이 컴포넌트 로드하지 않도록
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 데이터가 이미 존재하는 경우, 다시 생성하지 않도록 방지
        if (itemRepository.count() > 0) {
            return;
        }

        // 상품 데이터 생성
        List<Item> items = createSampleItems();
        itemRepository.saveAll(items);

        // 주문 데이터 생성
        createSampleOrders(items);
    }

    private List<Item> createSampleItems() {
        List<Item> items = new ArrayList<>();
        items.add(Item.builder().name("에티오피아 어쩌구원두").price(15000L).category("원두").image("image_url_1").build());
        items.add(Item.builder().name("콜롬비아 아무튼 원두").price(13000L).category("원두").image("image_url_2").build());
        items.add(Item.builder().name("머그컵").price(8000L).category("굿즈").image("image_url_3").build());
        items.add(Item.builder().name("스테인리스 텀블러").price(12000L).category("굿즈").image("image_url_4").build());
        items.add(Item.builder().name("핸드드립 세트").price(25000L).category("도구").image("image_url_5").build());
        return items;
    }

    private void createSampleOrders(List<Item> items) {
        Item item1 = items.get(0); // 에티오피아
        Item item2 = items.get(1); // 콜롬비아
        Item item3 = items.get(2); // 머그컵
        Item item4 = items.get(3); // 텀블러

        // 주문 1
        Order order1 = Order.builder()
                .userEmail("user@example.com")
                .userAddress("서울시 강남구")
                .userZipCode("12345")
                .status("배송준비중")
                .orderItems(new ArrayList<>())
                .build();

        List<OrderItem> order1Items = List.of(
                createOrderItem(item1, 1), // 15000
                createOrderItem(item3, 2)  // 16000
        );
        order1Items.forEach(order1::addOrderItem);
        long order1TotalPrice = order1Items.stream().mapToLong(OrderItem::getSubTotalPrice).sum();
        order1.updateTotal(order1TotalPrice); // totalPrice 설정
        orderRepository.save(order1);

        // 주문 2
        Order order2 = Order.builder()
                .userEmail("user@example.com")
                .userAddress("서울시 마포구")
                .userZipCode("54321")
                .status("배송완료")
                .orderItems(new ArrayList<>())
                .build();

        List<OrderItem> order2Items = List.of(
                createOrderItem(item2, 2), // 26000
                createOrderItem(item4, 1)  // 12000
        );
        order2Items.forEach(order2::addOrderItem);
        long order2TotalPrice = order2Items.stream().mapToLong(OrderItem::getSubTotalPrice).sum();
        order2.updateTotal(order2TotalPrice); // totalPrice 설정
        orderRepository.save(order2);

        // 주문 3
        Order order3 = Order.builder()
                .userEmail("another@example.com")
                .userAddress("부산시 해운대구")
                .userZipCode("98765")
                .status("배송완료")
                .orderItems(new ArrayList<>())
                .build();

        List<OrderItem> order3Items = List.of(
                createOrderItem(item1, 1) // 15000
        );
        order3Items.forEach(order3::addOrderItem);
        long order3TotalPrice = order3Items.stream().mapToLong(OrderItem::getSubTotalPrice).sum();
        order3.updateTotal(order3TotalPrice); // totalPrice 설정
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