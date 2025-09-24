package com.grids.domain.orderItem.entity;

import com.grids.domain.item.entity.Item;
import com.grids.domain.order.entity.Orders;
import com.grids.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    private Long subTotalPrice;

    private int quantity;
}
