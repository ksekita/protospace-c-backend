package in.techcamp.protospace.mapper;

import in.techcamp.protospace.entity.PrototypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

// prototypeテーブルを操作するインターフェース
@Mapper
public interface PrototypeMapper {
  // idを使用して検索
  @Select(
      "SELECT id, title, catch_copy AS catchCopy, concept, image, user_id AS userId FROM"
          + " prototypes WHERE id = #{id}")
  PrototypeEntity findById(Long id);

//   新規投稿による記事の追加
  @Insert("INSERT INTO prototypes (title, catch_copy, concept, image, user_id) VALUES (#{title}, #{catchCopy}, #{concept}, #{image}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PrototypeEntity prototype);

}
