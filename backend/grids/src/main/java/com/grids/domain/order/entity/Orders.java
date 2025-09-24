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
public class Orders extends BaseEntity {

    @OneToMany(mappedBy = "order", cascade = {jakarta.persistence.CascadeType.REMOVE, jakarta.persistence.CascadeType.PERSIST})
    private List<OrderItem> orderItems = new ArrayList<>();

    public String userEmail;

    public String userAddress;

    public String userZipCode;

    public String status;

    public Long totalPrice;
}