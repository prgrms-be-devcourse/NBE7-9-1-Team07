package com.grids.domain.item.controller;

import com.grids.domain.item.dto.ItemCreateRequestDto;
import com.grids.domain.item.dto.ItemCreateResponseDto;
import com.grids.domain.item.service.ItemService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items") // 이 컨트롤러의 모든 API는 /api/items 경로를 가집니다.
public class ItemController {

    private final ItemService itemService;

    @PostMapping // HTTP POST 요청을 처리하는 메서드
    public ResponseEntity<ItemCreateResponseDto> createItem(@RequestBody ItemCreateRequestDto requestDto) {
        // Service를 호출하여 상품을 생성하고, 생성된 ID를 받아옴
        Long itemId = itemService.createItem(requestDto);

        // 응답 객체를 생성합니다.
        ItemCreateResponseDto responseDto = new ItemCreateResponseDto(itemId);

        // 생성된 리소스의 URI를 Location 헤더에 포함하여 응답합니다.
        URI location = URI.create("/api/items/" + itemId);
        return ResponseEntity.created(location).body(responseDto);
    }
}
