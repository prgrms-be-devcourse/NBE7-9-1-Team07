package com.grids.domain.orderItem.entity;

import com.grids.domain.item.entity.Item;
import com.grids.domain.order.entity.Order;
import com.grids.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
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

    // 테스트 데이터 생성을 위해 빌더 패턴 추가
    @Builder
    public OrderItem(Item item, Long subTotalPrice, int quantity) {
        this.item = item;
        this.subTotalPrice = subTotalPrice;
        this.quantity = quantity;
    }


    // 테스트 데이터 생성을 위한 연관관계 편의 메서드
    public void setOrders(Order order) {
        this.order = order;
    }
}
