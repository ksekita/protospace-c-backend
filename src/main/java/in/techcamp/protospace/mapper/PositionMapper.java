package in.techcamp.protospace.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

// positionテーブルを操作するインターフェース
@Mapper
public interface PositionMapper {

  // 保存
  @Insert("INSERT INTO positions (user_id, position) VALUES (#{userId}, #{position})")
  void insert(@Param("userId") Long userId, @Param("position") String position);

  // 検索
  @Select("SELECT position FROM positions WHERE user_id = #{userId}")
  String findByUserId(Long userId);

  // 編集
  @Update("UPDATE positions SET position = #{position} WHERE user_id = #{userId}")
  int update(@Param("userId") Long userId, @Param("position") String position);
}
