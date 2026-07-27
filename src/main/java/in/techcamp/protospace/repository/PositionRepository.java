package in.techcamp.protospace.repository;

import in.techcamp.protospace.mapper.PositionMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PositionRepository {
  private final PositionMapper positionMapper;

  // コンストラクタ
  public PositionRepository(PositionMapper positionMapper) {
    this.positionMapper = positionMapper;
  }

  // 保存
  public void insert(Long userId, String position) {
    positionMapper.insert(userId, position);
  }

  // 検索
  public String findByUserId(Long userId) {
    return positionMapper.findByUserId(userId);
  }
}
