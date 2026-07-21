package in.techcamp.protospace.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

//jobsテーブルを操作するインターフェース
@Mapper
public interface JobMapper {

  //保存
  @Insert("INSERT INTO jobs (user_id, job) VALUES (#{userId}, #{job})")
  void insert(@Param("userId") Long userId, @Param("job") String job);

  //検索
  @Select("SELECT job FROM jobs WHERE user_id = #{userId}")
  List<String> findByUserId(Long userId);
}