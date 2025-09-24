package com.grids.domain.order.service;

import com.grids.domain.order.dto.OrderRequestDto;
import com.grids.domain.order.dto.OrderResponseDto;
import com.grids.domain.order.entity.Order;
import com.grids.domain.order.repository.OrderRepository;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;

import java.util.Optional;

import static com.grids.domain.order.helper.OrderTestHelper.createExistingOrderStub;
import static com.grids.domain.order.helper.OrderTestHelper.createMergedOrderStub;
import static com.grids.domain.order.helper.OrderTestHelper.createNewOrderStub;
import static com.grids.domain.order.helper.OrderTestHelper.createOrderRequestDto;
import static com.grids.domain.order.helper.OrderTestHelper.setEntityField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderCreator orderCreator;

    @Mock
    private OrderMerger orderMerger;

    @Test
    @DisplayName("기존 주문이 없을 시, 새로운 주문을 생성")
    void createOrder_WhenNoExistingOrder_ShouldCreateNewOrder()
            throws ReflectiveOperationException {
        // given: 주문 요청 데이터 생성
        OrderRequestDto requestDto = createOrderRequestDto();

        // given: ID가 부여된 신규 주문 객체 Stub 생성
        // Stub : 테스트에 필요한 만큼의 아주 단순한 동작만 하도록 미리 정해진 값을 반환
        Order newOrder = createNewOrderStub(requestDto);
        setEntityField(newOrder, "id", 1L); // 리플렉션으로 ID 필드에 값 설정

        // given: Repository는 주문을 찾지 못하는 상황을 가정
        when(orderRepository.findFirstByUserEmailAndCreatedAtBetweenOrderByCreatedAtDesc(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // given: OrderCreator는 위에서 만든 newOrder Stub을 반환하도록 설정
        when(orderCreator.createNewOrder(requestDto)).thenReturn(newOrder);

        // when: 주문 생성 서비스 실행
        OrderResponseDto responseDto = orderService.createOrder(requestDto);

        // then: 반환된 DTO의 값 검증
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getOrderId()).isEqualTo(1L);
        assertThat(responseDto.getTotalPrice()).isEqualTo(30000L);

        // then: 의존 객체들의 호출 여부 검증
        verify(orderCreator, times(1)).createNewOrder(requestDto);
        verify(orderMerger, never()).mergeIntoExistingOrder(any(), any());
    }

    @Test
    @DisplayName("기존 주문이 있을 시, 해당 주문에 상품을 병합")
    void createOrder_WhenExistingOrderFound_ShouldMergeIntoIt()
            throws ReflectiveOperationException {
        // given: 주문 요청 데이터 생성
        OrderRequestDto requestDto = createOrderRequestDto();

        // given: DB에 이미 존재하는 주문 객체 Stub 생성
        Order existingOrder = createExistingOrderStub();
        setEntityField(existingOrder, "id", 1L); // 기존 주문 ID

        // given: 병합 완료 후의 주문 객체 Stub 생성
        Order mergedOrder = createMergedOrderStub(existingOrder);
        setEntityField(mergedOrder, "id",99L);

        // given: Repository는 existingOrder를 반환하는 상황을 가정
        when(orderRepository.findFirstByUserEmailAndCreatedAtBetweenOrderByCreatedAtDesc(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(existingOrder));

        // given: OrderMerger는 mergedOrder Stub을 반환하도록 설정
        when(orderMerger.mergeIntoExistingOrder(existingOrder, requestDto)).thenReturn(mergedOrder);

        // when: 주문 생성 서비스 실행
        OrderResponseDto responseDto = orderService.createOrder(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getOrderId()).isEqualTo(99L);
        assertThat(responseDto.getTotalPrice()).isEqualTo(80000L);

        // then
        verify(orderMerger, times(1)).mergeIntoExistingOrder(existingOrder, requestDto);
        verify(orderCreator, never()).createNewOrder(any());
    }

    @Test
    @DisplayName("기존 주문이 단위 시간을 벗어났을 경우, 새로 주문을 생성")
    void createOrder_WhenExistingOrderIsOutsideTimeWindow_ShouldCreateNewOrder()
            throws ReflectiveOperationException {
        // given: 주문 요청 데이터 생성
        OrderRequestDto requestDto = createOrderRequestDto();

        // given: '이틀 전'에 생성된, 즉 단위 시간을 벗어난 기존 주문 객체 생성
        Order oldOrder = createExistingOrderStub();
        setEntityField(oldOrder, "id", 99L); // 기존 주문 ID

        // given: ID가 부여된 신규 주문 객체 Stub 생성
        Order newOrder = createNewOrderStub(requestDto);
        setEntityField(newOrder, "id", 100L); // 새 주문 ID

        // given: Repository는 '단위 시간 내'에 주문을 찾지 못하는 상황을 가정 (oldOrder는 너무 오래되어서 찾아지지 않음)
        when(orderRepository.findFirstByUserEmailAndCreatedAtBetweenOrderByCreatedAtDesc(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // given: 따라서 OrderCreator가 호출될 것이며, newOrder Stub을 반환하도록 설정
        when(orderCreator.createNewOrder(requestDto)).thenReturn(newOrder);

        // when: 주문 생성 서비스 실행
        OrderResponseDto responseDto = orderService.createOrder(requestDto);

        // then: 반환된 DTO는 '새로 생성된' 주문의 정보와 일치해야 함
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getOrderId()).isEqualTo(100L); // 새 주문 ID
        assertThat(responseDto.getOrderId()).isNotEqualTo(99L); // 기존 주문 ID가 아니어야 함

        // then: Merger가 아닌 Creator가 호출되었는지 검증
        verify(orderCreator, times(1)).createNewOrder(requestDto);
        verify(orderMerger, never()).mergeIntoExistingOrder(any(), any());
    }

}