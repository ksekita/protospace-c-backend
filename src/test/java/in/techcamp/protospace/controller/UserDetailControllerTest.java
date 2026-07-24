package in.techcamp.protospace.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import in.techcamp.protospace.dto.UserDetailResponseDto;
import in.techcamp.protospace.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserDetailControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserService userService;

  @Nested
  @DisplayName("ユーザー詳細取得API (GET /api/users/{id})")
  class GetUserDetailTest {

    @Test
    @DisplayName("【正常系】ユーザー詳細情報をJSONで取得できること (トークン不要)")
    void getUserDetail_Success() throws Exception {
      // 準備
      UserDetailResponseDto mockResponse = new UserDetailResponseDto();
      mockResponse.setId(1L);
      mockResponse.setUsername("テスト太郎");
      mockResponse.setEmail("test@example.com");
      mockResponse.setPosition("リーダー");
      mockResponse.setAffiliation("株式会社テスト");

      when(userService.getUserDetail(1L)).thenReturn(mockResponse);

      // 実行・検証
      mockMvc
          .perform(get("/api/users/1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(1))
          .andExpect(jsonPath("$.username").value("テスト太郎"))
          .andExpect(jsonPath("$.email").value("test@example.com"))
          .andExpect(jsonPath("$.position").value("リーダー"))
          .andExpect(jsonPath("$.affiliation").value("株式会社テスト"));
    }
  }
}