package com.grids.domain.order.controller;

import com.grids.domain.order.dto.OrderResponseDto;
import com.grids.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByEmail(@RequestParam("email") String email) {
        List<OrderResponseDto> orders = orderService.findOrdersByEmail(email);

        return ResponseEntity.ok(orders);
    }
}
