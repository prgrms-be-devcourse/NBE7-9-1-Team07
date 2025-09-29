package com.grids.domain.orderItem.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grids.domain.orderItem.entity.OrderItem;
import com.grids.domain.orderItem.repository.OrderItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderItemCancelIntegrationTest {

    @Autowired private MockMvc mvc;
    @Autowired private OrderItemRepository orderItemRepository;
    private final ObjectMapper om = new ObjectMapper();

    @Test
    @DisplayName("주문 취소(=OrderItem 삭제) - /orderItems?id=... 형태로 다건 취소하고 DB에서 사라짐")
    void cancelOrderItems_success_withSampleData() throws Exception {
        // given: 샘플 데이터 2건 저장 (엔티티 필수 필드에 맞게 값 세팅)
        OrderItem oi1 = orderItemRepository.saveAndFlush(new OrderItem(101L, 3));
        OrderItem oi2 = orderItemRepository.saveAndFlush(new OrderItem(202L, 1));
        Long id1 = oi1.getId();
        Long id2 = oi2.getId();

        assertThat(orderItemRepository.findById(id1)).isPresent();
        assertThat(orderItemRepository.findById(id2)).isPresent();

        // when: 컨트롤러 시그니처에 맞춰 DELETE /orderItems?id=...&id=...
        MvcResult result = mvc.perform(
                        delete("/orderItems")
                                .param("id", id1.toString(), id2.toString())
                )
                .andDo(print())
                .andExpect(status().isOk()) // @ResponseBody로 객체 리턴 → 200 OK
                .andReturn();

        // then: 응답 본문 검증 (CancelOrderItemResponse.orderItemIds 에 두 ID가 포함)
        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode root = om.readTree(body);
        assertThat(root.has("orderItemIds")).isTrue();
        JsonNode ids = root.get("orderItemIds");
        assertThat(ids.isArray()).isTrue();
        assertThat(ids.toString()).contains(id1.toString());
        assertThat(ids.toString()).contains(id2.toString());

        // 그리고 DB에서 실제 삭제되었는지 확인
        assertThat(orderItemRepository.findById(id1)).isNotPresent();
        assertThat(orderItemRepository.findById(id2)).isNotPresent();
    }
}
