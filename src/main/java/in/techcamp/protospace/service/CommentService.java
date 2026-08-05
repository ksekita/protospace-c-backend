package in.techcamp.protospace.service;

import in.techcamp.protospace.dto.CommentRequestDto;
import in.techcamp.protospace.dto.CommentResponseDto;
import in.techcamp.protospace.entity.CommentEntity;
import in.techcamp.protospace.repository.CommentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;

  // 記事IDを使用してコメントを取得
  public List<CommentResponseDto> getCommentsByPrototypeId(Long prototypeId) {
    return commentRepository.findByPrototypeId(prototypeId);
  }

  // コメント作成
  @Transactional
  public void createComment(Long prototypeId, Long userId, CommentRequestDto request) {
    CommentEntity comment = new CommentEntity();
    comment.setContent(request.getContent());
    comment.setPrototypeId(prototypeId);
    comment.setUserId(userId);

    commentRepository.insert(comment);
  }

  // コメント削除機能
  public void deleteComment(Long userId, Long Id)  {

    // コメントを入手
    CommentEntity existingComment = commentRepository.selectByCommentId(Id);
    if(existingComment == null){
      throw new IllegalArgumentException("指定されたコメントが見つかりません");
    }
    if(!existingComment.getUserId().equals(userId)) {
      throw new SecurityException("他のユーザーのコメントを削除する権限がありません");
    }
    // 削除実行
  commentRepository.deleteByCommentId(Id);
  }

}
