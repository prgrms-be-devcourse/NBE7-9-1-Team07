package com.grids.domain.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemCreateRequestDto {

    private String name;
    private Long price;
    private String category;
    private String image;

    @Builder
    public ItemCreateRequestDto(String name, Long price, String category, String image) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
    }

}
