package com.grids.domain.item.controller;

import com.grids.domain.item.dto.*;
import com.grids.domain.item.entity.Item;
import com.grids.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    @Transactional(readOnly = true)
    public List<ItemListDto> list() {
        List<ItemListDto> itemList = itemService.findAll().stream()
                .map(Item::toDto)
                .toList();

        return itemList;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemInfoUpdateResponseDto> updateItemInfo(@PathVariable("itemId") Long itemId, @RequestBody ItemInfoUpdateRequestDto itemInfoUpdateRequestDto) {
        ItemInfoUpdateResponseDto responseDto = itemService.updateItem(itemId, itemInfoUpdateRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<ItemCreateResponseDto> createItem(@RequestBody ItemCreateRequestDto request) {
        Long createdId = itemService.createItem(request);
        ItemCreateResponseDto result = new ItemCreateResponseDto(createdId);
        return ResponseEntity.ok(result);
    }
}
