package in.techcamp.protospace.mapper;

import in.techcamp.protospace.dto.PrototypeListDto;
import in.techcamp.protospace.entity.PrototypeEntity;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

// prototypeテーブルを操作するインターフェース
@Mapper
public interface PrototypeMapper {

  // 編集機能
  @Update(
      "UPDATE prototypes SET title = #{title}, catch_copy = #{catchCopy}, concept = #{concept}, image = #{image} WHERE id = #{id}")
  void update(PrototypeEntity prototype);

  // 新規投稿による記事の追加
  @Insert(
      "INSERT INTO prototypes (title, catch_copy, concept, image, user_id) VALUES (#{title}, #{catchCopy}, #{concept}, #{image}, #{userId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(PrototypeEntity prototype);

  // 全件一覧の取得（新しい順に変更）
  @Select("SELECT p.id, p.title, p.catch_copy, p.image, p.user_id, u.name FROM prototypes p LEFT JOIN users u ON p.user_id = u.id ORDER BY p.id DESC")
  List<PrototypeListDto> findAll();

  // キーワード検索による一覧取得（新しい順）
  @Select("SELECT p.id, p.title, p.catch_copy, p.image, p.user_id, u.name FROM prototypes p LEFT JOIN users u ON p.user_id = u.id WHERE p.title LIKE CONCAT('%', #{keyword}, '%') ORDER BY p.id DESC")
  List<PrototypeListDto> findByKeyword(String keyword);

  // idを使用して検索
  @Select(
      "SELECT id, title, catch_copy, concept, image, user_id FROM" + " prototypes WHERE id = #{id}")
  PrototypeEntity findById(Long id);

  @Delete("DELETE FROM prototypes WHERE id = #{id}")
  void delete(Long id);

  @Select(
      "SELECT id, title, catch_copy, concept, image, user_id FROM prototypes WHERE user_id = #{userId}")
  List<PrototypeEntity> findByUserId(Long userId);
}
