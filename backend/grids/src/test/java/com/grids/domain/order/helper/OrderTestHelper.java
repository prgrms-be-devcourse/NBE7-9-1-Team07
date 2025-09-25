package com.grids.domain.order.helper;

import com.grids.domain.item.entity.Item;
import com.grids.domain.order.entity.Order;
import com.grids.domain.orderItem.entity.OrderItem;

import java.util.Arrays;

public final class OrderTestHelper {

    private OrderTestHelper() {
    }

    public static Item createItem(String name, Long price) {
        return Item.builder()
                .name(name)
                .price(price)
                .category("테스트 카테고리")
                .image("test_image.jpg")
                .build();
    }

    public static OrderItem createOrderItem(Item item, int quantity) {
        return OrderItem.builder()
                .item(item)
                .quantity(quantity)
                .subTotalPrice(item.getPrice() * quantity)
                .build();
    }

    public static Order createOrder(String userEmail, String userAddress, String userZipCode, OrderItem... orderItems) {
        long totalAmount = Arrays.stream(orderItems)
                .mapToLong(OrderItem::getSubTotalPrice)
                .sum();

        Order order = Order.builder()
                .userEmail(userEmail)
                .userAddress(userAddress)
                .userZipCode(userZipCode)
                .status("배송준비중")
                .totalPrice(totalAmount)
                .build();

        Arrays.stream(orderItems).forEach(orderItem -> {
            order.getOrderItems().add(orderItem);
            orderItem.setOrders(order);
        });

        return order;
    }
}
