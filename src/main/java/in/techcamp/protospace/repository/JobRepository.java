package in.techcamp.protospace.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import in.techcamp.protospace.mapper.JobMapper;

@Repository
public class JobRepository {
  private final JobMapper jobMapper;

  //コンストラクタ
  public JobRepository(JobMapper jobMapper) {
    this.jobMapper = jobMapper;
  }

  //保存
  public void insert(Long userId, String job) {
    jobMapper.insert(userId, job);
  }

  //検索
  public List<String> findByUserId(Long userId) {
    return jobMapper.findByUserId(userId);
  }
}