package in.techcamp.protospace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import in.techcamp.protospace.dto.CommentRequestDto;
import in.techcamp.protospace.dto.CommentResponseDto;
import in.techcamp.protospace.entity.CommentEntity;
import in.techcamp.protospace.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Captor
    private ArgumentCaptor<CommentEntity> commentEntityCaptor;

    @Nested
    @DisplayName("コメント取得処理 (getCommentsByPrototypeId)")
    class GetCommentsTest {

        @Test
        @DisplayName("【正常系】記事IDに紐づくコメント一覧が取得できること")
        void getComments_Success() {
            // モックデータの準備
            CommentResponseDto mockComment = new CommentResponseDto(
                    1L, "素晴らしいプロトタイプですね！", 10L, 1L, "テスト太郎"
            );
            when(commentRepository.findByPrototypeId(1L)).thenReturn(List.of(mockComment));

            // テスト実行
            List<CommentResponseDto> result = commentService.getCommentsByPrototypeId(1L);

            // 検証
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getContent()).isEqualTo("素晴らしいプロトタイプですね！");
            assertThat(result.get(0).getUserName()).isEqualTo("テスト太郎");
        }
    }

    @Nested
    @DisplayName("コメント投稿処理 (createComment)")
    class CreateCommentTest {

        @Test
        @DisplayName("【正常系】コメント情報が正しくEntityに詰め替えられ、保存されること")
        void createComment_Success() {
            // 準備
            CommentRequestDto request = new CommentRequestDto();
            request.setContent("参考になります！");

            // テスト実行 (記事ID: 1, ユーザーID: 10)
            commentService.createComment(1L, 10L, request);

            // 検証（Repositoryに渡されたEntityの値をキャプチャして確認）
            verify(commentRepository).insert(commentEntityCaptor.capture());
            CommentEntity capturedEntity = commentEntityCaptor.getValue();

            assertThat(capturedEntity.getContent()).isEqualTo("参考になります！");
            assertThat(capturedEntity.getPrototypeId()).isEqualTo(1L);
            assertThat(capturedEntity.getUserId()).isEqualTo(10L);
        }
    }
}