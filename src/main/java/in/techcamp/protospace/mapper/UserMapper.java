package in.techcamp.protospace.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import in.techcamp.protospace.entity.UserEntity;

//Userテーブルを操作するインターフェース
@Mapper
public interface UserMapper {

  //検索
  @Select("SELECT * FROM users WHERE id = #{id}")
  UserEntity selectById(Long id);

  //メアドが既にあるか確認
  @Select("SELECT EXISTS(SELECT 1 FROM users WHERE email = #{email})")
  boolean existsByEmail(@Param("email") String email);

  //メアドを検索
  @Select("SELECT * FROM users WHERE email = #{email}")
  UserEntity selectByEmail(String email);

  //保存
  @Insert("""
      INSERT INTO users (
        name, email, password
      ) VALUES (
        #{name}, #{email}, #{password}
      )
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(UserEntity user);
}