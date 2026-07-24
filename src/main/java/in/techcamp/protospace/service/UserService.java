package in.techcamp.protospace.service;

import in.techcamp.protospace.dto.UserDetailResponseDto;
import in.techcamp.protospace.dto.UserDto;
import in.techcamp.protospace.dto.UserResponseDto;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.exception.ValidationException;
import in.techcamp.protospace.repository.AffiliationRepository;
import in.techcamp.protospace.repository.PositionRepository;
import in.techcamp.protospace.repository.UserRepository;
import in.techcamp.protospace.security.JwtTokenProvider;
import java.util.List;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final PositionRepository positionRepository;
  private final AffiliationRepository affiliationRepository;
  private final JwtTokenProvider jwtTokenProvider;

  public UserService(
      UserRepository userRepository,
      PositionRepository positionRepository,
      AffiliationRepository affiliationRepository,
      PasswordEncoder passwordEncoder,
      JwtTokenProvider jwtTokenProvider) {
    this.userRepository = userRepository;
    this.positionRepository = positionRepository;
    this.affiliationRepository = affiliationRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  // ユーザー情報の取得
  public UserEntity selectById(Long id) {
    return userRepository.selectById(id);
  }

  // ユーザー新規登録
  @Transactional
  public UserResponseDto insertUser(UserDto userDto) {
    if (!userDto.getPassword().equals(userDto.getPasswordConfirm())) {
      throw new ValidationException(
          Map.of("passwordConfirm", List.of("パスワードが一致しません")), "パスワードが一致しません");
    }

    if (userRepository.existsByEmail(userDto.getEmail())) {
      throw new ValidationException(Map.of("email", List.of("このメールアドレスは既に登録されています。")), "登録エラー");
    }

    UserEntity user = new UserEntity();
    user.setUsername(userDto.getUsername());
    user.setEmail(userDto.getEmail());
    user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));

    // ユーザー本体の登録（自動採番されたIDが user.getId() にセットされる）
    userRepository.insertUser(user);
    Long userId = user.getId();

    // 役職(positions)の登録
    if (userDto.getPosition() != null && !userDto.getPosition().isBlank()) {
      positionRepository.insert(userId, userDto.getPosition());
    }

    // 配属(affiliations)の登録
    if (userDto.getAffiliation() != null && !userDto.getAffiliation().isBlank()) {
      affiliationRepository.insert(userId, userDto.getAffiliation());
    }

    String token = jwtTokenProvider.generateToken(String.valueOf(userId));

    return new UserResponseDto(
        token,
        userId,
        user.getUsername(),
        user.getEmail(),
        userDto.getPosition(),
        userDto.getAffiliation());
  }
  public UserDetailResponseDto getUserDetail(Long userId) {
        // 1. ユーザー基本情報の取得
        UserEntity user = userRepository.selectById(userId);
        if (user == null) {
            throw new RuntimeException("ユーザーが見つかりません");
        }

        // 2. 役職と所属の取得
        String position = positionRepository.findByUserId(userId);
        String affiliation = affiliationRepository.findByUserId(userId);

        // 3. DTOに詰めて返す
        UserDetailResponseDto response = new UserDetailResponseDto();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPosition(position);
        response.setAffiliation(affiliation);

        return response;
    }
}
