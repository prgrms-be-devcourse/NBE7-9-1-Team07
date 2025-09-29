package com.grids.domain.orderItem.controller;

import com.grids.domain.orderItem.dto.CancelOrderItemResponse;
import com.grids.domain.orderItem.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;


    @DeleteMapping("/orderItems")
    @ResponseBody
    public CancelOrderItemResponse cancelOrderItems(
            @RequestParam("id") List<Long> orderItemIds
    ) {
        System.out.println("Cancelling orderItemIds: " + orderItemIds);
        orderItemService.removeByOrderItemIds(orderItemIds);

        CancelOrderItemResponse cancelOrderResponse = new CancelOrderItemResponse();
        cancelOrderResponse.setOrderItemIds(orderItemIds);

        return cancelOrderResponse;
    }
}
