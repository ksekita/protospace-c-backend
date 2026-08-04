package in.techcamp.protospace.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import in.techcamp.protospace.entity.LikeEntity;
import in.techcamp.protospace.entity.PrototypeEntity;

@Mapper
public interface LikeMapper {
    
  // 保存
  @Insert(
      "INSERT INTO likes (user_id, prototype_id) VALUES (#{userId}, #{prototypeId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(LikeEntity like);

  //総いいね数のカウント
  @Select("SELECT COUNT(*) FROM likes WHERE prototype_id = :id")
  PrototypeEntity countLikes(Long prototypeId);

  //いいねがついているか
  @Select("SELECT EXISTS(SELECT 1 FROM likes WHERE user_id = #{userId} AND prototype_id = #{prototypeId}")
  boolean existsLike(Long prototypeId);
}
