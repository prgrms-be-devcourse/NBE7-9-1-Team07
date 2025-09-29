package com.grids.domain.item.service;

import com.grids.domain.item.dto.ItemCreateRequestDto;
import com.grids.domain.item.dto.ItemInfoUpdateRequestDto;
import com.grids.domain.item.dto.ItemInfoUpdateResponseDto;
import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemInfoUpdateResponseDto updateItem(Long itemId, ItemInfoUpdateRequestDto requestDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + itemId));

        item.update(requestDto);

        // 변경 사항을 db에 동기화, 업데이트된 entity를 반환받음
        Item updatedItem = itemRepository.saveAndFlush(item);

        // db에 반영된 최신 엔티티로 응답 Dto 생성
        return ItemInfoUpdateResponseDto.from(updatedItem);
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

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
