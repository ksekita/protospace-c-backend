package in.techcamp.protospace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import in.techcamp.protospace.dto.PrototypeListDto;
import in.techcamp.protospace.factory.PrototypeFactory;
import in.techcamp.protospace.security.JwtTokenProvider;
import in.techcamp.protospace.service.PrototypeService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
        .andExpect(jsonPath("$.message").value("プロトタイプの投稿に成功しました。"));

    verify(prototypeService).createPrototype(any(), eq(1L));
  }

 @Test
  @WithMockUser
  public void testGetAllPrototypes() throws Exception {

    // ダミー100個
    List<PrototypeListDto> mockList = PrototypeFactory.createDummyList(100);

    // モック（引数が null の場合の挙動を定義）
    when(prototypeService.getAllPrototypes(null)).thenReturn(mockList);

    mockMvc
        .perform(get("/api/prototypes/").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(100))
        .andExpect(jsonPath("$[0].title").value("テストタイトル1"))
        .andExpect(jsonPath("$[99].title").value("テストタイトル100"));

    // 引数 null でサービスが呼ばれたか検証
    verify(prototypeService).getAllPrototypes(null);
  }

  // キーワードあり（検索）の場合のテスト
  @Test
  @WithMockUser
  public void testGetAllPrototypesWithKeyword() throws Exception {

    // 検索結果としてダミーを2個返すように設定
    List<PrototypeListDto> mockList = PrototypeFactory.createDummyList(2);

    // モック（引数に "テスト" が渡された場合の挙動を定義）
    when(prototypeService.getAllPrototypes("テスト")).thenReturn(mockList);

    // param("keyword", "テスト") でクエリパラメータを付与してリクエスト
    mockMvc
        .perform(get("/api/prototypes/")
            .param("keyword", "テスト")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));

    // 引数 "テスト" でサービスが呼ばれたか検証
    verify(prototypeService).getAllPrototypes("テスト");
  }

  @Nested
  @DisplayName("プロトタイプ削除API (DELETE /api/prototypes/{id})")
  class DeletePrototypeApiTest {

    @Test
    @DisplayName("【正常系】削除に成功した場合、200 OKと成功メッセージが返ること")
    @WithMockUser(username = "1") // ユーザーID=1としてモックログイン
    void deletePrototype_Success() throws Exception {
      // 準備 (Service層の処理は何もしないようにモックする)
      doNothing().when(prototypeService).deletePrototype(1L, 1L);

      // 実行・検証
      mockMvc
          .perform(delete("/api/prototypes/1").header("Authorization", "Bearer " + token))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("プロトタイプの削除に成功しました"));
    }

    @Test
    @DisplayName("【異常系】削除に失敗した場合、400 Bad Requestとエラーメッセージが返ること")
    @WithMockUser(username = "1")
    void deletePrototype_Fail() throws Exception {
      // 準備 (Service層で例外が発生するようにモックする)
      doThrow(new Exception("他のユーザーの投稿を削除する権限がありません"))
          .when(prototypeService)
          .deletePrototype(1L, 1L);

      // 実行・検証
      mockMvc
          .perform(delete("/api/prototypes/1").header("Authorization", "Bearer " + token))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error").value("削除に失敗しました：他のユーザーの投稿を削除する権限がありません"));
    }
  }

  @Nested
  @DisplayName("特定ユーザーのプロトタイプ一覧取得API (GET /api/prototypes/users/{userId})")
  class GetPrototypesByUserIdApiTest {

    @Test
    @DisplayName("【正常系】存在するユーザーIDを指定した場合、プロトタイプ一覧がJSONで返ること")
    @WithMockUser
    void getPrototypesByUserId_Success() throws Exception {
      // 準備
      Long userId = 1L;

      in.techcamp.protospace.dto.UserPrototypeListDto dto =
          new in.techcamp.protospace.dto.UserPrototypeListDto();
      dto.setId(10L);
      dto.setTitle("テストタイトル");
      dto.setCatchCopy("テストキャッチコピー");
      dto.setImage("test.png");

      when(prototypeService.getPrototypesByUserId(userId)).thenReturn(List.of(dto));

      // 実行・検証
      mockMvc
          .perform(
              get("/api/prototypes/users/" + userId).header("Authorization", "Bearer " + token))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].id").value(10))
          .andExpect(jsonPath("$[0].title").value("テストタイトル"))
          .andExpect(jsonPath("$[0].catchCopy").value("テストキャッチコピー"))
          .andExpect(jsonPath("$[0].image").value("test.png"));

      // サービスが正しく呼び出されたか検証
      verify(prototypeService).getPrototypesByUserId(userId);
    }
  }
}
