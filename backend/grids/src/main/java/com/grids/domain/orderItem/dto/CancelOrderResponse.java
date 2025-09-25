package com.grids.domain.orderItem.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CancelOrderResponse {
    private List<Long> orderItemIds;
    private String orderStatus = "CANCELLED";
    private String message = "주문이 정상적으로 취소되었습니다";
}
