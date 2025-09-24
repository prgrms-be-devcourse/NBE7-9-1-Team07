package com.grids.domain.order.entity;

import com.grids.domain.orderItem.entity.OrderItem;
import com.grids.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Order extends BaseEntity {

    private String userEmail;

    private String userAddress;

    private String userZipCode;

    private String status;

    private Long totalPrice;

    // 한 오더는 여러 개의 품목을 가질 수 있으니까
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
}
