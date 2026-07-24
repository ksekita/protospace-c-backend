package in.techcamp.protospace.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import in.techcamp.protospace.entity.PrototypeEntity;

@Mapper
public interface PrototypeMapper {
    //新規投稿によるデータベースの追加
    @Insert("INSERT INTO prototypes (title, catch_copy, concept, image, user_id) VALUES (#{title}, #{catchCopy}, #{concept}, #{image}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PrototypeEntity prototype);

        // 編集機能
    @Select("SELECT * FROM prototypes WHERE id = #{id}")
    PrototypeEntity findById(Long id);

    @Update("UPDATE prototypes SET title = #{title}, catch_copy = #{catchCopy}, concept = #{concept}, image = #{image} WHERE id = #{id}")
    void update(PrototypeEntity prototype);
}
