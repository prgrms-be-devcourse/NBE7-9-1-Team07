package com.grids.domain.item.entity;

import com.grids.domain.item.dto.ItemListDto;
import com.grids.domain.item.dto.ItemInfoUpdateRequestDto;
import com.grids.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@Getter
@Builder
@Entity
public class Item extends BaseEntity {

    private String name;

    private Long price;

    private String category;

    private String image;

    public Item (String name, Long price, String category, String image) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
    }

    public ItemListDto toDto(){
        return new ItemListDto(
                this.getId(),
                this.getName(),
                this.getCategory(),
                this.getPrice(),
                this.getImage()
        );
    }
    public void update(ItemInfoUpdateRequestDto requestDto) {
        Optional.ofNullable(requestDto.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(requestDto.getPrice()).ifPresent(price -> this.price = price);
        Optional.ofNullable(requestDto.getCategory()).ifPresent(category -> this.category = category);
        Optional.ofNullable(requestDto.getImage()).ifPresent(image -> this.image = image);
    }

}