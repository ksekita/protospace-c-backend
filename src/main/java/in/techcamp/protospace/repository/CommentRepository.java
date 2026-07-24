package in.techcamp.protospace.repository;

import in.techcamp.protospace.dto.CommentResponseDto;
import in.techcamp.protospace.entity.CommentEntity;
import in.techcamp.protospace.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
    private final CommentMapper commentMapper;

    public void insert(CommentEntity comment) {
        commentMapper.insert(comment);
    }

    public List<CommentResponseDto> findByPrototypeId(Long prototypeId) {
        return commentMapper.findByPrototypeId(prototypeId);
    }
}