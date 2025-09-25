package com.grids.domain.orderItem.controller;

import com.grids.domain.orderItem.dto.CancelOrderResponse;
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

//    private final OrderItemRepository orderItemRepository;
//    // 테스트용 샘플 데이터 삽입 API : OrderItems의 필드변수들을 public으로 변경후 사용 가능
//    @PostMapping("/create-sample-orderItem")
//    @ResponseBody
//    public String sampleOrderItem() {
//
//        // 1. Order 샘플 엔티티 생성
//        OrderItem newOrder = new OrderItem();
//        //newOrder.order
//        //newOrder.item=("Seoul, Korea");
//        //newOrder.subTotalPrice=("12345");
//        newOrder.quantity=(1);
//
//        // 2. 리포지토리를 통해 저장
//        orderItemRepository.save(newOrder);
//
//        return "샘플 데이터가 DB/OrdersItem 테이블에 저장되었습니다";
//    }

    @DeleteMapping("/orderItems")
    @ResponseBody
    public CancelOrderResponse cancelOrderItems(
            @RequestParam("id") List<Long> orderItemIds
    ) {
        System.out.println("Cancelling orderItemIds: " + orderItemIds);
        orderItemService.removeByOrderItemIds(orderItemIds);

        CancelOrderResponse cancelOrderResponse = new CancelOrderResponse();
        cancelOrderResponse.setOrderIds(orderItemIds);

        return cancelOrderResponse;
    }
}
