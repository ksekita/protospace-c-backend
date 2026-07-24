package in.techcamp.protospace.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

import in.techcamp.protospace.entity.PrototypeEntity;

@Mapper
public interface PrototypeMapper {
    //新規投稿によるデータベースの追加
  @Insert("INSERT INTO prototypes (title, catch_copy, concept, image, user_id) VALUES (#{title}, #{catchCopy}, #{concept}, #{image}, #{userId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PrototypeEntity prototype);

  @Select("SELECT * FROM prototypes")
    List<PrototypeEntity> findAll();
  
  // idを使用して検索
  @Select(
      "SELECT id, title, catch_copy AS catchCopy, concept, image, user_id AS userId FROM"
          + " prototypes WHERE id = #{id}")
  PrototypeEntity findById(Long id);
}
