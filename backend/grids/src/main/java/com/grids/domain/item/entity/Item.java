package com.grids.domain.item.entity;

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
}
