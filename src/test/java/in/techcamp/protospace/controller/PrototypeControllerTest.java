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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import in.techcamp.protospace.dto.PrototypeDetailResponseDto;
import in.techcamp.protospace.dto.PrototypeLikeResponseDto;
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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
    token = jwtTokenProvider.generateToken("1"); // ID = 1 のトークン
  }

  @Test
  public void testCreatePrototype() throws Exception {
    MockMultipartFile imageFile =
        new MockMultipartFile(
            "image", "test-image.png", "image/png", "dummy image data".getBytes());

    mockMvc
        .perform(
            multipart("/api/prototypes/")
                .file(imageFile)
                .param("title", "テストタイトル")
                .param("catchCopy", "テストキャッチコピー")
                .param("concept", "テストコンセプト")
                .header("Authorization", "Bearer " + token)
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("プロトタイプの投稿に成功しました。"));

    verify(prototypeService).createPrototype(any(), eq(1L));
  }

  @Test
  public void testGetAllPrototypes() throws Exception {
    List<PrototypeListDto> mockList = PrototypeFactory.createDummyList(100);

    // トークン経由でログインしているため loggedInUserId = 1L が渡る
    when(prototypeService.getAllPrototypes(null, "latest", 1L)).thenReturn(mockList);

    mockMvc
        .perform(get("/api/prototypes/").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(100))
        .andExpect(jsonPath("$[0].title").value("テストタイトル1"))
        .andExpect(jsonPath("$[99].title").value("テストタイトル100"));

    verify(prototypeService).getAllPrototypes(null, "latest", 1L);
  }

  @Test
  public void testGetAllPrototypesWithKeyword() throws Exception {
    List<PrototypeListDto> mockList = PrototypeFactory.createDummyList(2);

    when(prototypeService.getAllPrototypes("テスト", "latest", 1L)).thenReturn(mockList);

    mockMvc
        .perform(get("/api/prototypes/")
            .param("keyword", "テスト")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));

    verify(prototypeService).getAllPrototypes("テスト", "latest", 1L);
  }

  @Nested
  @DisplayName("プロトタイプ削除API (DELETE /api/prototypes/{id})")
  class DeletePrototypeApiTest {

    @Test
    @DisplayName("【正常系】削除に成功した場合、200 OKと成功メッセージが返ること")
    @WithMockUser(username = "1")
    void deletePrototype_Success() throws Exception {
      doNothing().when(prototypeService).deletePrototype(1L, 1L);

      mockMvc
          .perform(delete("/api/prototypes/1").header("Authorization", "Bearer " + token))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("プロトタイプの削除に成功しました"));
    }

    @Test
    @DisplayName("【異常系】削除に失敗した場合、400 Bad Requestとエラーメッセージが返ること")
    @WithMockUser(username = "1")
    void deletePrototype_Fail() throws Exception {
      doThrow(new Exception("他のユーザーの投稿を削除する権限がありません"))
          .when(prototypeService)
          .deletePrototype(1L, 1L);

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
    void getPrototypesByUserId_Success() throws Exception {
      Long userId = 1L;

      in.techcamp.protospace.dto.UserPrototypeListDto dto =
          new in.techcamp.protospace.dto.UserPrototypeListDto();
      dto.setId(10L);
      dto.setName("テストユーザー");
      dto.setTitle("テストタイトル");
      dto.setCatchCopy("テストキャッチコピー");
      dto.setImage("test.png");

      when(prototypeService.getPrototypesByUserId(userId, "latest", 1L)).thenReturn(List.of(dto));

      mockMvc
          .perform(
              get("/api/prototypes/users/" + userId).header("Authorization", "Bearer " + token))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].id").value(10))
          .andExpect(jsonPath("$[0].name").value("テストユーザー"));

      verify(prototypeService).getPrototypesByUserId(userId, "latest", 1L);
    }
  }

  @Nested
  @DisplayName("プロトタイプ一覧・検索・並び替えAPI (GET /api/prototypes)")
  class GetAllPrototypesApiTest {

    @Test
    @DisplayName("【正常系】キーワードとソート条件（oldest）を指定して検索できること")
    void getAllPrototypes_WithKeywordAndSort() throws Exception {
      List<PrototypeListDto> mockList = PrototypeFactory.createDummyList(2);
      when(prototypeService.getAllPrototypes("Java", "oldest", 1L)).thenReturn(mockList);

      mockMvc
          .perform(
              get("/api/prototypes/")
                  .param("keyword", "Java")
                  .param("sort", "oldest")
                  .header("Authorization", "Bearer " + token))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2));

      verify(prototypeService).getAllPrototypes("Java", "oldest", 1L);
    }

    @Test
    @DisplayName("【正常系】ソート条件のみ（oldest）を指定した場合、古い順で取得できること")
    void getAllPrototypes_WithSortOnly() throws Exception {
      List<PrototypeListDto> mockList = PrototypeFactory.createDummyList(5);
      when(prototypeService.getAllPrototypes(null, "oldest", 1L)).thenReturn(mockList);

      mockMvc
          .perform(
              get("/api/prototypes/")
                  .param("sort", "oldest")
                  .header("Authorization", "Bearer " + token))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(5));

      verify(prototypeService).getAllPrototypes(null, "oldest", 1L);
    }
  }

  @Nested
  @DisplayName("プロトタイプ詳細API (GET /api/prototypes/{id})")
  class GetPrototypeDetailApiTest {

    @Test
    @DisplayName("【正常系】ログイン状態で詳細を取得した場合、いいね情報が含まれること")
    @WithMockUser(username = "1")
    void getPrototypeDetail_LoggedIn() throws Exception {
      Long prototypeId = 1L;
      Long loggedInUserId = 1L;

      PrototypeDetailResponseDto mockResponse = new PrototypeDetailResponseDto(
          prototypeId, "タイトル", "キャッチ", "コンセプト", "image.png", 2L, "投稿者", 10L, true
      );

      when(prototypeService.getPrototypeDetail(prototypeId, loggedInUserId)).thenReturn(mockResponse);

      mockMvc.perform(get("/api/prototypes/" + prototypeId).header("Authorization", "Bearer " + token))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.likeCount").value(10))
             .andExpect(jsonPath("$.isLiked").value(true));

      verify(prototypeService).getPrototypeDetail(prototypeId, loggedInUserId);
    }

    @Test
    @DisplayName("【正常系】未ログイン状態で詳細を取得した場合、未ログインID(0L)としてServiceが呼ばれること")
    void getPrototypeDetail_Anonymous() throws Exception {
      Long prototypeId = 1L;
      Long anonymousUserId = 0L;

      PrototypeDetailResponseDto mockResponse = new PrototypeDetailResponseDto(
          prototypeId, "タイトル", "キャッチ", "コンセプト", "image.png", 2L, "投稿者", 100L, false
      );

      when(prototypeService.getPrototypeDetail(prototypeId, anonymousUserId)).thenReturn(mockResponse);

      // Authorization ヘッダーなしで送信
      mockMvc.perform(get("/api/prototypes/" + prototypeId))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.likeCount").value(100))
             .andExpect(jsonPath("$.isLiked").value(false));

      verify(prototypeService).getPrototypeDetail(prototypeId, anonymousUserId);
    }
  }

  @Nested
  @DisplayName("いいねトグルAPI (POST /api/prototypes/{id}/like)")
  class ToggleLikeApiTest {

    @Test
    @DisplayName("【正常系】いいねのトグル処理に成功し、最新のいいね数と状態が返ること")
    @WithMockUser(username = "1")
    void toggleLike_Success() throws Exception {
      Long prototypeId = 1L;
      Long userId = 1L;
      PrototypeLikeResponseDto responseDto = new PrototypeLikeResponseDto(5L, true);

      when(prototypeService.toggleLike(prototypeId, userId)).thenReturn(responseDto);

      mockMvc
          .perform(
              post("/api/prototypes/" + prototypeId + "/like")
                  .header("Authorization", "Bearer " + token)
                  .with(csrf()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.likeCount").value(5))
          .andExpect(jsonPath("$.isLiked").value(true));

      verify(prototypeService).toggleLike(prototypeId, userId);
    }

    @Test
    @DisplayName("【異常系】Service層で例外が発生した場合、500 Internal Server Errorが返ること")
    @WithMockUser(username = "1")
    void toggleLike_ServerError() throws Exception {
      Long prototypeId = 1L;
      Long userId = 1L;

      when(prototypeService.toggleLike(prototypeId, userId))
          .thenThrow(new RuntimeException("DBエラー"));

      mockMvc
          .perform(
              post("/api/prototypes/" + prototypeId + "/like")
                  .header("Authorization", "Bearer " + token)
                  .with(csrf()))
          .andExpect(status().isInternalServerError());
    }
  }
}