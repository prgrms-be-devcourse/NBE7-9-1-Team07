package com.grids.domain.order.dto;

import com.grids.domain.orderItem.dto.OrderItemRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    private String email;
    private List<OrderItemRequestDto> orderItems;
    private String userAddress;
    private String userZipCode;


}
