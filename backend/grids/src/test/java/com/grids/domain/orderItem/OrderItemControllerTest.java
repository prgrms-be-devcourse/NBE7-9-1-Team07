package com.grids.domain.orderItem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // Bean에 등록한 것들 사용
@ActiveProfiles("test") // application-test.yml 활성화 -> db가 파일모드가 아닌 메모리 모드로 변경
@AutoConfigureMockMvc // 진짜 서버를 띄워서 네트워크를 이용한 통신이 아닌 내부적인 로직처리로 http요청/응답을 흉내냄
@Transactional // 테스트가 끝나면 자동 롤백
public class OrderItemControllerTest {

    @Autowired
    private MockMvc mvc; // 가짜 서버, 서버 시뮬레이션용

    @Test
        @DisplayName("주문 항목 취소")
        void t1() throws Exception {

            ResultActions resultActions = mvc
                    .perform(
                            delete("/orderItems?id=1")
                    )
                    .andDo(print());

            resultActions
                    .andExpect(status().isOk()); // 응답코드가 200 인지 확인
        }

}