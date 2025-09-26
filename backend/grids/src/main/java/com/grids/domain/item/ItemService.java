package com.grids.domain.item;

import com.grids.domain.item.dto.ItemCreateRequestDto;
import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용으로 설정
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public Long createItem(ItemCreateRequestDto requestDto) {

        // DTO를 Entity로 변환합니다.
        Item item = new Item(
                requestDto.getName(),
                requestDto.getPrice(),
                requestDto.getCategory(),
                requestDto.getImage()
        );

        // Repository를 통해 데이터베이스에 저장합니다.
        Item savedItem = itemRepository.save(item);

        // 저장된 Item의 ID를 반환합니다.
        return savedItem.getId();
    }
}