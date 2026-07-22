package in.techcamp.protospace.mapper;

import in.techcamp.protospace.entity.PrototypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PrototypeMapper {

  @Select(
      "SELECT id, title, catch_copy AS catchCopy, concept, image, user_id AS userId FROM"
          + " prototypes WHERE id = #{id}")
  PrototypeEntity findById(Long id);
}