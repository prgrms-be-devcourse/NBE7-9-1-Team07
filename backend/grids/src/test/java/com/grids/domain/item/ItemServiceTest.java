package com.grids.domain.item;

import com.grids.domain.item.dto.ItemCreateRequestDto;
import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    // 테스트에 사용할 데이터를 제공하는 static 메서드
    private static Stream<Arguments> itemCreationProvider() {
        return Stream.of(
                Arguments.of("원두", 2000L, "과일", "apple.jpg"),
                Arguments.of("텀블러", 1500000L, "전자기기", "notebook.png"),
                Arguments.of("머그컵", 35000L, "스포츠용품", "soccer_ball.jpg")
        );
    }


    @ParameterizedTest
    @MethodSource("itemCreationProvider") // 위에서 만든 데이터 제공 메서드를 지정
    void 상품을성공적으로등록(String name, Long price, String category, String image) {
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