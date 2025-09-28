package com.grids.domain.item.service;

import com.grids.domain.item.dto.ItemInfoUpdateRequestDto;
import com.grids.domain.item.dto.ItemInfoUpdateResponseDto;
import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.grids.domain.item.helper.ItemTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Test
    @DisplayName("상품 정보 변경에 성공한다.")
    void updateItem_Success() throws ReflectiveOperationException {
        // given (테스트 데이터 준비)
        Long itemId = 1L;
        LocalDateTime creationTime = LocalDateTime.now().minusDays(1);

        // 원본 상품 데이터
        Item existingItem = createItem("기존 상품", 10000L, "기존 카테고리", "original.jpg");
        setEntityField(existingItem, "id", itemId);
        setEntityField(existingItem, "createdAt", creationTime);
        setEntityField(existingItem, "updatedAt", creationTime);

        // 업데이트할 상품 데이터 (변수로 선언)
        String updatedName = "업데이트된 상품";
        Long updatedPrice = 12000L;
        String updatedCategory = "업데이트된 카테고리";
        String updatedImage = "updated.jpg";
        ItemInfoUpdateRequestDto requestDto = createItemInfoUpdateRequestDto(updatedName, updatedPrice, updatedCategory, updatedImage);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // saveAndFlush 메서드가 어떤 Item 객체든 받으면, 그대로 반환
        when(itemRepository.saveAndFlush(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when (테스트할 메서드 호출)
        ItemInfoUpdateResponseDto responseDto = itemService.updateItem(itemId, requestDto);

        // then (결과 검증 - given 블록의 변수를 사용하여 동적으로 검증)
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(itemId);
        assertThat(responseDto.getName()).isEqualTo(updatedName);
        assertThat(responseDto.getPrice()).isEqualTo(updatedPrice);
        assertThat(responseDto.getCategory()).isEqualTo(updatedCategory);
        assertThat(responseDto.getImage()).isEqualTo(updatedImage);
        assertThat(responseDto.getCreatedAt()).isEqualTo(creationTime); // createdAt은 변경되지 않았는지 확인
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 변경을 시도하면 예외가 발생한다.")
    void updateItem_Fail_WhenItemNotFound() {
        // given
        Long nonExistentItemId = 999L;
        ItemInfoUpdateRequestDto requestDto = createItemInfoUpdateRequestDto("업데이트", 1000L, "카테고리", "이미지");

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then (예외 발생 검증)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            itemService.updateItem(nonExistentItemId, requestDto);
        });

        assertThat(exception.getMessage()).isEqualTo("해당 상품이 존재하지 않습니다. id=" + nonExistentItemId);
    }
}