package com.grids.domain.order.controller;

import com.grids.domain.order.entity.Orders;
import com.grids.domain.order.repository.OrderRepository;
import com.grids.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    // 테스트용 샘플 데이터 삽입 API
    @GetMapping("/create-sample-order")
    @ResponseBody
    public String sampleOrder() {

        // 1. Order 샘플 엔티티 생성
        Orders newOrder = new Orders();
        newOrder.userEmail ="testuser@example.com";
        newOrder.userAddress=("Seoul, Korea");
        newOrder.userZipCode=("12345");
        newOrder.status=("PENDING");
        newOrder.totalPrice=(80000L);

        // 2. 리포지토리를 통해 저장
        orderRepository.save(newOrder);

        return "샘플 데이터가 DB/Orders 테이블에 저장되었습니다";
    }

    @DeleteMapping("/orders")
    @ResponseBody
    public String cancelOrder(
            @RequestParam("id") List<Integer> orderNumbers
    ) {
        System.out.println("Cancelling orders: " + orderNumbers);
        orderService.remove(orderNumbers);
        return orderNumbers + " 번 주문이 취소되었습니다. ";
    }

}
