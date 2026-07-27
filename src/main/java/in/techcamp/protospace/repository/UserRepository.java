package in.techcamp.protospace.repository;

import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.mapper.UserMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
  private final UserMapper userMapper;

  // コンストラクタ
  public UserRepository(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  // 検索
  public UserEntity selectById(Long id) {
    return userMapper.selectById(id);
  }

  // 既存のメアドがあるか検索
  public boolean existsByEmail(String email) {
    return userMapper.existsByEmail(email);
  }

  // メアド検索
  public UserEntity selectByEmail(String email) {
    return userMapper.selectByEmail(email);
  }

  // 保存
  public int insertUser(UserEntity user) {
    return userMapper.insert(user);
  }
}
