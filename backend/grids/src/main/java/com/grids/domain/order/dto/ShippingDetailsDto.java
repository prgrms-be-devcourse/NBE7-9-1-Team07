package com.grids.domain.order.dto;

import com.grids.domain.order.entity.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShippingDetailsDto {
    private String recipientEmail;
    private String address;
    private String postCode;

    public ShippingDetailsDto(Order order) {
        this.recipientEmail = order.getUserEmail();
        this.address = order.getUserAddress();
        this.postCode = order.getUserZipCode();
    }
}