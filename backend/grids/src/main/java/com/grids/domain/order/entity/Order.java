package com.grids.domain.order.entity;

import com.grids.domain.orderItem.entity.OrderItem;
import com.grids.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    private String userEmail;
    private String userAddress;
    private String userZipCode;
    private String status;
    private Long totalPrice;

    // 한 오더는 여러 개의 품목을 가질 수 있으니까
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 테스트 데이터 생성을 위해 빌더 패턴 추가
    @Builder
    public Order(String userEmail, String userAddress, String userZipCode, String status, Long totalPrice) {
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        this.userZipCode = userZipCode;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    // 테스트 데이터 생성을 위한 연관관계 편의 메서드
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrders(this);
    }
}
