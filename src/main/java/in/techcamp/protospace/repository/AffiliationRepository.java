package in.techcamp.protospace.repository;


import org.springframework.stereotype.Repository;
import in.techcamp.protospace.mapper.AffiliationMapper;

@Repository
public class AffiliationRepository {
  private final AffiliationMapper affiliationMapper;

  //コンストラクタ
  public AffiliationRepository(AffiliationMapper affiliationMapper) {
    this.affiliationMapper = affiliationMapper;
  }

  //保存
  public void insert(Long userId, String affiliation) {
    affiliationMapper.insert(userId, affiliation);
  }

  //検索
  public String findByUserId(Long userId) {
  return affiliationMapper.findByUserId(userId);
}
}