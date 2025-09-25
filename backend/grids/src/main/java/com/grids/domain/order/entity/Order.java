package com.grids.domain.order.entity;

import com.grids.domain.orderItem.entity.OrderItem;
import com.grids.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @OneToMany(mappedBy = "order", cascade = {jakarta.persistence.CascadeType.REMOVE, jakarta.persistence.CascadeType.PERSIST})
    private List<OrderItem> orderItems = new ArrayList<>();

    private String userEmail;

    private String userAddress;

    private String userZipCode;

    private String status;

    private Long totalPrice;
}