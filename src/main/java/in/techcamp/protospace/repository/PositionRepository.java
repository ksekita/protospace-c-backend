package in.techcamp.protospace.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import in.techcamp.protospace.mapper.PositionMapper;

@Repository
public class PositionRepository {
  private final PositionMapper positionMapper;

  //コンストラクタ
  public PositionRepository(PositionMapper positionMapper) {
    this.positionMapper = positionMapper;
  }

  //保存
  public void insert(Long userId, String position) {
    positionMapper.insert(userId, position);
  }

  //検索
  public List<String> findByUserId(Long userId) {
    return positionMapper.findByUserId(userId);
  }
}