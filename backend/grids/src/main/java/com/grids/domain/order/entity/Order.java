package com.grids.domain.order.entity;

import com.grids.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Order extends BaseEntity {

    private String userEmail;

    private String userAddress;

    private String userZipCode;

    private String status;

    private Long totalPrice;
}
