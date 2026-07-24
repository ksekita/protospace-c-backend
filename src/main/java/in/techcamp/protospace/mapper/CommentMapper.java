package in.techcamp.protospace.mapper;

import in.techcamp.protospace.dto.CommentResponseDto;
import in.techcamp.protospace.entity.CommentEntity;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

// commentテーブルを操作するインターフェース
@Mapper
public interface CommentMapper {

  // 保存
  @Insert(
      "INSERT INTO comments (content, user_id, prototype_id) VALUES (#{content}, #{userId}, #{prototypeId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(CommentEntity comment);

  // コメント一覧の取得
  @Select(
      """
        SELECT
            c.id,
            c.content,
            c.user_id AS userId,
            c.prototype_id AS prototypeId,
            u.username AS userName
        FROM comments c
        INNER JOIN users u ON c.user_id = u.id
        WHERE c.prototype_id = #{prototypeId}
        ORDER BY c.id ASC
    """)
  List<CommentResponseDto> findByPrototypeId(Long prototypeId);
}
