package in.techcamp.protospace.mapper;

import in.techcamp.protospace.entity.PrototypeEntity;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

// prototypeテーブルを操作するインターフェース
@Mapper
public interface PrototypeMapper {

  // 新規投稿による記事の追加
  @Insert(
      "INSERT INTO prototypes (title, catch_copy, concept, image, user_id) VALUES (#{title}, #{catchCopy}, #{concept}, #{image}, #{userId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(PrototypeEntity prototype);

  // 全件一覧の取得
  @Select("SELECT * FROM prototypes")
  List<PrototypeEntity> findAll();

  // idを使用して検索
  @Select(
      "SELECT id, title, catch_copy AS catchCopy, concept, image, user_id AS userId FROM"
          + " prototypes WHERE id = #{id}")
  PrototypeEntity findById(Long id);
}
