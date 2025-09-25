package com.grids.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemCreateRequestDto {

    private String name;
    private Long price;
    private String category;
    private String image;
}
