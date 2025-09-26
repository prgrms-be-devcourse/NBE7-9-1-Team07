package com.grids.domain.item.dto;

import com.grids.domain.item.entity.Item;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ItemInfoUpdateResponseDto {
    private final Long id;
    private final String name;
    private final String category;
    private final Long price;
    private final String image;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private ItemInfoUpdateResponseDto(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.category = item.getCategory();
        this.price = item.getPrice();
        this.image = item.getImage();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
    }

    // 정적 팩토리 메서드
    public static ItemInfoUpdateResponseDto from(Item item) {
        return new ItemInfoUpdateResponseDto(item);
    }
}
