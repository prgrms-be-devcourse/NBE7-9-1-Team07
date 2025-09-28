package com.grids.domain.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ItemInfoUpdateRequestDto {
    private String name;
    private Long price;
    private String category;
    private String image;
}
