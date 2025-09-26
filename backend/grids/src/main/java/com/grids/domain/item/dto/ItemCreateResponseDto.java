package com.grids.domain.item.dto;

import lombok.Getter;

@Getter
public class ItemCreateResponseDto {

    private Long id;

    public ItemCreateResponseDto(Long id) {
        this.id = id;
    }
}
