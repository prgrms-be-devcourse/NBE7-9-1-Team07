package com.grids.domain.item.controller;

import com.grids.domain.item.dto.ItemInfoUpdateRequestDto;
import com.grids.domain.item.dto.ItemInfoUpdateResponseDto;
import com.grids.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemContoller {

    private final ItemService itemService;

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemInfoUpdateResponseDto> updateItemInfo(@PathVariable("itemId") Long itemId, @RequestBody ItemInfoUpdateRequestDto itemInfoUpdateRequestDto) {
        ItemInfoUpdateResponseDto responseDto = itemService.updateItem(itemId, itemInfoUpdateRequestDto);
        return ResponseEntity.ok(responseDto);
    }
}
