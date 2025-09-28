package com.grids.domain.item.dto;

import lombok.Getter;

@Getter
public class ItemListDto {
    private Long id;
    private String name;
    private String category;
    private Long price;
    private String image;

    public ItemListDto(Long id, String name, String category, Long price, String image) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.image = image;
    }
}
