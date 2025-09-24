package com.grids.domain.item.controller;

import com.grids.domain.item.entity.Item;
import com.grids.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@Controller
//@ResponseBody
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items")
    public List<Item> ShowItems() {

        return itemService.getItems();

    }
}
