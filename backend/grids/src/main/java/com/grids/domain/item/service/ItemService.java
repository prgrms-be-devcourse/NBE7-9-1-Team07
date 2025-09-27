package com.grids.domain.item.service;

import com.grids.domain.item.dto.ItemInfoUpdateRequestDto;
import com.grids.domain.item.dto.ItemInfoUpdateResponseDto;
import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
