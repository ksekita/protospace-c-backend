package in.techcamp.protospace.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

//affiliationsテーブルを操作するインターフェース
@Mapper
public interface AffiliationMapper {

  //保存
  @Insert("INSERT INTO affiliations (user_id, affiliation) VALUES (#{userId}, #{affiliation})")
  void insert(@Param("userId") Long userId, @Param("affiliation") String affiliation);

  //検索
  @Select("SELECT affiliation FROM affiliations WHERE user_id = #{userId}")
  String findByUserId(Long userId);
}