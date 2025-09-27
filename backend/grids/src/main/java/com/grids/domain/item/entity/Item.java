package com.grids.domain.item.entity;

import com.grids.domain.item.dto.ItemListDto;
import com.grids.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
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
}
