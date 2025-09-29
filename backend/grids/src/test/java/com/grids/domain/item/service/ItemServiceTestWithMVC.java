package com.grids.domain.item.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItemServiceTestWithMVC {

    @Autowired private MockMvc mvc;
    @Autowired private ItemRepository itemRepository;

    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();

        // 실제 DB에 더미 데이터 저장 (정확히 이 값들이 응답으로 와야 함)
        itemRepository.saveAll(List.of(
                new Item("텀블러", 1500L, "주방용품", "tumbler.jpg"),
                new Item("머그컵", 3000L, "주방용품", "mug.jpg")
        ));
    }

    @Test
    @DisplayName("상품 조회 - 실제 저장된 값과 응답 값이 일치한다")
    void getItemList_ReturnsExactValues() throws Exception {
        // when
        MvcResult result = mvc.perform(get("/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode root = om.readTree(json);

        assertThat(root.isArray()).isTrue();
        assertThat(root.size()).isEqualTo(2);

        // 응답을 name -> (price, category, image) 맵으로 변환
        Map<String, ItemView> respMap = toMapByName(root);

        // 1) 존재 여부 및 개수
        assertThat(respMap.keySet())
                .containsExactlyInAnyOrder("텀블러", "머그컵");

        // 2) 각 항목의 필드 값 일치 검증
        assertThat(respMap.get("텀블러").price).isEqualTo(1500L);
        assertThat(respMap.get("텀블러").category).isEqualTo("주방용품");
        assertThat(respMap.get("텀블러").image).isEqualTo("tumbler.jpg");

        assertThat(respMap.get("머그컵").price).isEqualTo(3000L);
        assertThat(respMap.get("머그컵").category).isEqualTo("주방용품");
        assertThat(respMap.get("머그컵").image).isEqualTo("mug.jpg");

        // 3) 공통 필드 유효성 (id 존재/양수, createdAt/updatedAt 문자열 등)
        root.forEach(node -> {
            assertThat(node.hasNonNull("id")).isTrue();
            assertThat(node.get("id").isIntegralNumber()).isTrue();
            assertThat(node.get("id").asLong()).isPositive();

            if (node.has("createdAt")) {
                assertThat(node.get("createdAt").isTextual()).isTrue();
                assertThat(node.get("createdAt").asText()).isNotBlank();
            }
            if (node.has("updatedAt")) {
                assertThat(node.get("updatedAt").isTextual()).isTrue();
                assertThat(node.get("updatedAt").asText()).isNotBlank();
            }
        });
    }

    private record ItemView(Long price, String category, String image) {}

    private Map<String, ItemView> toMapByName(JsonNode arrayRoot) {
        return toList(arrayRoot).stream().collect(Collectors.toMap(
                n -> n.get("name").asText(),
                n -> new ItemView(
                        n.has("price") ? n.get("price").asLong() : null,
                        n.has("category") ? n.get("category").asText() : null,
                        n.has("image") ? n.get("image").asText() : null
                )
        ));
    }

    private List<JsonNode> toList(JsonNode arrayRoot) {
        return arrayRoot.findParents("name").stream()
                .map(p -> p) // 이미 배열 요소
                .toList();
    }
}
