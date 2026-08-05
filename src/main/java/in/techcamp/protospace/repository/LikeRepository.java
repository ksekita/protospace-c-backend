package in.techcamp.protospace.repository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import in.techcamp.protospace.mapper.LikeMapper;
import in.techcamp.protospace.entity.LikeEntity;

@Repository
@RequiredArgsConstructor
public class LikeRepository {
  private final LikeMapper likeMapper;

  // 保存
  public void insert(LikeEntity like) {
    likeMapper.insert(like);
  }

  //削除
  public void delete(LikeEntity like){
    likeMapper.delete(like.getUserId(),like.getPrototypeId());
  }

  // いいねの数を取得
  public Long countByPrototypeId(Long prototypeId){
    return likeMapper.countByPrototypeId(prototypeId);
  }

  // いいねされているか確認
  public boolean existsLike(Long userId,Long prototypeId){
    return likeMapper.existsLike(userId,prototypeId);
  }
}
