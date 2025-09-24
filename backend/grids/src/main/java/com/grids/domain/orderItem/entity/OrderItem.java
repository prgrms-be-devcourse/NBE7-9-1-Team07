package com.grids.domain.orderItem.entity;

import com.grids.domain.item.entity.Item;
import com.grids.domain.order.entity.Order;
import com.grids.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") // db에 만들 fk 칼럼 이름
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id") // ''
    private Item item;

    private Long subTotalPrice;

    private int quantity;
}
