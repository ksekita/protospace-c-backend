package in.techcamp.protospace.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

import in.techcamp.protospace.entity.PrototypeEntity;

@Mapper
public interface PrototypeMapper {
    //新規投稿によるデータベースの追加
    @Insert("INSERT INTO prototypes (title, catch_copy, image, user_id) VALUES (#{title}, #{catch_copy}, #{concept}, #{image}, #{user.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PrototypeEntity prototype);

}