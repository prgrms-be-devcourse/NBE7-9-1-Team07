package com.grids.domain.item.entity;

import com.grids.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Builder;
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

    // 테스트 데이터 생성을 위해 빌더 패턴 추가
    @Builder
    public Item(String name, Long price, String category, String image) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
    }
}
