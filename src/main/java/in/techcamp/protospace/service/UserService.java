package in.techcamp.protospace.service;

import java.util.List;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import in.techcamp.protospace.dto.UserDto;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.exception.ValidationException;
import in.techcamp.protospace.repository.JobRepository;
import in.techcamp.protospace.repository.PositionRepository;
import in.techcamp.protospace.repository.UserRepository;

@Service
public class UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final PositionRepository positionRepository;
  private final JobRepository jobRepository;

  public UserService(UserRepository userRepository, PositionRepository positionRepository, JobRepository jobRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.positionRepository = positionRepository;
    this.jobRepository = jobRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // ユーザー情報の取得
  public UserEntity selectById(Long id) {
    UserEntity user = userRepository.selectById(id);
    if (user != null) {
      user.setPositions(positionRepository.findByUserId(id));
      user.setJobs(jobRepository.findByUserId(id));
    }
    return user;
  }

  // ユーザー新規登録
  @Transactional 
  public int insertUser(UserDto userDto) {
    if (!userDto.getPassword().equals(userDto.getPasswordConfirm())) {
      throw new ValidationException(
          Map.of("passwordConfirm", List.of("パスワードが一致しません")), "パスワードが一致しません");
    }

    if (userRepository.existsByEmail(userDto.getEmail())) {
      throw new ValidationException(
          Map.of("email", List.of("このメールアドレスは既に登録されています。")), "登録エラー");
    }

    UserEntity user = new UserEntity();
    user.setUsername(userDto.getUsername());
    user.setEmail(userDto.getEmail());
    user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));

    // ユーザー本体の登録（自動採番されたIDが user.getId() にセットされる）
    int result = userRepository.insertUser(user);
    Long userId = user.getId();

    // 役職(positions)の登録
    if (userDto.getPositions() != null && !userDto.getPositions().isEmpty()) {
      for (String position : userDto.getPositions()) {
        if (position != null && !position.isBlank()) {
          positionRepository.insert(userId, position);
        }
      }
    }

    // 職業(jobs)の登録
    if (userDto.getJobs() != null && !userDto.getJobs().isEmpty()) {
      for (String job : userDto.getJobs()) {
        if (job != null && !job.isBlank()) {
          jobRepository.insert(userId, job);
        }
      }
    }

    return result;
  }
}