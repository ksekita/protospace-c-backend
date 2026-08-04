package in.techcamp.protospace.repository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import in.techcamp.protospace.mapper.LikeMapper;
import in.techcamp.protospace.entity.LikeEntity;
import in.techcamp.protospace.entity.PrototypeEntity;

@Repository
@RequiredArgsConstructor
public class LikeRepository {
  private final LikeMapper likeMapper;

  // 保存
  public void insert(LikeEntity like) {
    likeMapper.insert(like);
  }

  public PrototypeEntity countLikes(Long prototypeId){
    return likeMapper.countLikes(prototypeId);
  }

  public boolean existsLike(Long prototypeId){
    return likeMapper.existsLike(prototypeId); 
  }
}
