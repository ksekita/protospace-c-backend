package in.techcamp.protospace.service;

import in.techcamp.protospace.dto.CommentRequestDto;
import in.techcamp.protospace.dto.CommentResponseDto;
import in.techcamp.protospace.entity.CommentEntity;
import in.techcamp.protospace.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
}