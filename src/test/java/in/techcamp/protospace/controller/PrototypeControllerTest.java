package in.techcamp.protospace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.factory.PrototypeFactory;
import in.techcamp.protospace.security.JwtTokenProvider;
import in.techcamp.protospace.service.PrototypeService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PrototypeControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private JwtTokenProvider jwtTokenProvider;

  @MockitoBean private PrototypeService prototypeService;

  private String token;

  @BeforeEach
  void setUp() {
    token = jwtTokenProvider.generateToken("1");
  }

  @Test
  public void testCreatePrototype() throws Exception {
    // ダミーデータの作成
    MockMultipartFile imageFile =
        new MockMultipartFile(
            "image", "test-image.png", "image/png", "dummy image data".getBytes());

    // MockMvcを使って、疑似的にPOSTリクエストを送信する
    mockMvc
        .perform(
            multipart("/api/prototypes/")
                .file(imageFile)
                .param("title", "テストタイトル")
                .param("catchCopy", "テストキャッチコピー")
                .param("concept", "テストコンセプト")
                .header("Authorization", "Bearer " + token)
                .with(csrf()))

        // 動作確認
        .andExpect(status().isOk())
        .andExpect(content().string("プロトタイプの投稿に成功しました。"));

    verify(prototypeService).createPrototype(any(), eq(1L));
  }

  @Test
  @WithMockUser
  public void testGetAllPrototypes() throws Exception {

    // ダミー100個
    List<PrototypeEntity> mockList = PrototypeFactory.createDummyList(100);

    // モック
    when(prototypeService.getAllPrototypes()).thenReturn(mockList);

    mockMvc
        .perform(get("/api/prototypes/").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(100))
        .andExpect(jsonPath("$[0].title").value("テストタイトル1"))
        .andExpect(jsonPath("$[99].title").value("テストタイトル100"));

    verify(prototypeService).getAllPrototypes();
  }
}
