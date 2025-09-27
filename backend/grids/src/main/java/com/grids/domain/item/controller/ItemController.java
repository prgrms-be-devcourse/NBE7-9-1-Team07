package com.grids.domain.item.controller;

import com.grids.domain.item.dto.ItemListDto;
import com.grids.domain.item.entity.Item;
import com.grids.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items")
    @Transactional(readOnly = true)
    public List<ItemListDto> list() {
        List<ItemListDto> itemList =  itemService.findAll().stream()
                .map(Item::toDto)
                .toList();

        return itemList;
    }


}
