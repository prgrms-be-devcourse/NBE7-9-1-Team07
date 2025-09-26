//package com.grids.domain.order.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//
//@Controller
//@RequiredArgsConstructor
//public class OrderController {
//
//}
package com.grids.domain.order.controller;

import com.grids.domain.order.dto.OrderRequestDto;
import com.grids.domain.order.dto.OrderResponseDto;
import com.grids.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto requestDto) {

        OrderResponseDto responseDto = orderService.createOrder(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }



    // TODO: 주문 목록 조회 API, 주문 상세 조회 API, 주문 취소 API 등 추가
}