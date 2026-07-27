package in.techcamp.protospace.repository;

import in.techcamp.protospace.dto.CommentResponseDto;
import in.techcamp.protospace.entity.CommentEntity;
import in.techcamp.protospace.mapper.CommentMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
  private final CommentMapper commentMapper;

  // 保存
  public void insert(CommentEntity comment) {
    commentMapper.insert(comment);
  }

  // 記事idを使用して検索
  public List<CommentResponseDto> findByPrototypeId(Long prototypeId) {
    return commentMapper.findByPrototypeId(prototypeId);
  }
}
