package in.techcamp.protospace.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import in.techcamp.protospace.entity.LikeEntity;

@Mapper
public interface LikeMapper {
    
  // 保存
  @Insert(
      "INSERT INTO likes (user_id, prototype_id) VALUES (#{userId}, #{prototypeId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(LikeEntity like);

  //削除
  @Delete("DELETE FROM likes WHERE user_id = #{userId} AND prototype_id = #{prototypeId}")
  void delete(@Param("userId") Long userId, @Param("prototypeId") Long prototypeId);

  //総いいね数のカウント
  @Select("SELECT COUNT(*) FROM likes WHERE prototype_id = #{prototypeId}")
  Long countByPrototypeId(Long prototypeId);

  //いいねがついているか
  @Select("SELECT EXISTS(SELECT 1 FROM likes WHERE user_id = #{userId} AND prototype_id = #{prototypeId})")
  boolean existsLike(@Param("userId") Long userId, @Param("prototypeId") Long prototypeId);
}
