package in.techcamp.protospace.mapper;

import in.techcamp.protospace.entity.PrototypeEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

// prototypeテーブルを操作するインターフェース
@Mapper
public interface PrototypeMapper {

  // 保存
  @Insert(
      "INSERT INTO prototypes (title, catch_copy, concept, image, user_id) VALUES (#{title}, #{catchCopy}, #{concept}, #{image}, #{userId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(PrototypeEntity prototype);
}
