package com.grids.domain.item.service;

import com.grids.domain.item.dto.ItemCreateRequestDto;
import com.grids.domain.item.dto.ItemInfoUpdateRequestDto;
import com.grids.domain.item.dto.ItemInfoUpdateResponseDto;
import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import static org.mockito.Mockito.verify;
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
        ItemInfoUpdateRequestDto requestDto = createItemInfoUpdateRequestDto(updatedName,
                updatedPrice, updatedCategory, updatedImage);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // saveAndFlush 메서드가 어떤 Item 객체든 받으면, 그대로 반환
        when(itemRepository.saveAndFlush(any(Item.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

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
        ItemInfoUpdateRequestDto requestDto = createItemInfoUpdateRequestDto("업데이트", 1000L, "카테고리",
                "이미지");

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then (예외 발생 검증)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            itemService.updateItem(nonExistentItemId, requestDto);
        });

        assertThat(exception.getMessage()).isEqualTo("해당 상품이 존재하지 않습니다. id=" + nonExistentItemId);
    }


    //상품등록Test
    private static Stream<Arguments> itemCreationProvider() {
        return Stream.of(
                Arguments.of("원두", 2000L, "과일", "apple.jpg"),
                Arguments.of("텀블러", 1500000L, "전자기기", "notebook.png"),
                Arguments.of("머그컵", 35000L, "스포츠용품", "soccer_ball.jpg")
        );
    }
    @ParameterizedTest
    @DisplayName("다양한 상품을 성공적으로 등록")
    @MethodSource("itemCreationProvider") // 위에서 만든 데이터 제공 메서드를 지정
    void createMultipleItems_Success(String name, Long price, String category, String image) {
        // given (주어진 상황)
        ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder()
                .name(name)
                .price(price)
                .category(category)
                .image(image)
                .build();

        // 고유한 ID를 부여하기 위해 AtomicLong 사용
        AtomicLong idCounter = new AtomicLong(1L);

        // itemRepository.save()가 호출되면, ID가 부여된 객체를 반환하도록 설정
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            // 실제 DB 처럼 고유한 ID를 부여하는 흉내
            long generatedId = idCounter.getAndIncrement();

            return new Item(item.getName(), item.getPrice(), item.getCategory(), item.getImage()) {
                @Override
                public Long getId() {
                    return generatedId;
                }
            };
        });

        // when (무엇을 할 때)
        Long newItemId = itemService.createItem(requestDto);

        // then (결과 검증)
        // 1. 반환된 ID가 null이 아닌지 확인 (동적 ID이므로 값 비교는 어려움)
        assertThat(newItemId).isNotNull();
        assertThat(newItemId).isGreaterThan(0L);

        // 2. itemRepository의 save 메서드가 정확히 1번 호출되었는지 확인
        // ParameterizedTest는 각 케이스마다 독립적으로 실행되므로 항상 1번
        verify(itemRepository).save(any(Item.class));
    }


}
