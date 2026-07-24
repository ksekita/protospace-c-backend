package in.techcamp.protospace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.techcamp.protospace.dto.CommentRequestDto;
import in.techcamp.protospace.dto.CommentResponseDto;
import in.techcamp.protospace.security.JwtTokenProvider;
import in.techcamp.protospace.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 🌟 @Autowired を外し、直接インスタンス化することで Bean 欠絶エラーを回避
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CommentService commentService;

    @Nested
    @DisplayName("コメント一覧取得API (GET /api/prototypes/{prototypeId}/comments)")
    class GetCommentsTest {

        // CommentControllerTest.java 内の対象メソッド
@Test
@DisplayName("【正常系】コメント一覧をJSONで取得できること (トークン不要)")
void getComments_Success() throws Exception {
    CommentResponseDto mockComment = new CommentResponseDto(
            1L, "テストコメント", 10L, 1L, "テスト太郎"
    );
    when(commentService.getCommentsByPrototypeId(1L)).thenReturn(List.of(mockComment));

    mockMvc.perform(get("/api/prototypes/1/comments"))
            .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print()) // 🌟 これを追加して詳細ログを吐かせる
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].content").value("テストコメント"))
            .andExpect(jsonPath("$[0].userName").value("テスト太郎"));
}
    }

    @Nested
    @DisplayName("コメント投稿API (POST /api/prototypes/{prototypeId}/comments)")
    class CreateCommentTest {

        @Test
        @DisplayName("【正常系】正しいJWTトークンとデータでコメントが投稿できること")
        void createComment_Success() throws Exception {
            String token = jwtTokenProvider.generateToken("10");

            CommentRequestDto request = new CommentRequestDto();
            request.setContent("最高のプロトタイプですね！");

            mockMvc.perform(post("/api/prototypes/1/comments")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("コメントを投稿しました。"));

            verify(commentService).createComment(eq(1L), eq(10L), any(CommentRequestDto.class));
        }

        @Test
        @DisplayName("【異常系】バリデーションエラー：コメントが空の場合、422エラーになること")
        void createComment_ValidationFailed_Returns422() throws Exception {
            String token = jwtTokenProvider.generateToken("10");

            CommentRequestDto request = new CommentRequestDto();
            request.setContent("");

            mockMvc.perform(post("/api/prototypes/1/comments")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(422))
                    .andExpect(jsonPath("$.message").value("入力内容に不備があります"));
        }
    }
}