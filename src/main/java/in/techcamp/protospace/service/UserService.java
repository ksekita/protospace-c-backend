package in.techcamp.protospace.service;

import in.techcamp.protospace.dto.UserDetailResponseDto;
import in.techcamp.protospace.dto.UserDto;
import in.techcamp.protospace.dto.UserInfoDto;
import in.techcamp.protospace.dto.UserResponseDto;
import in.techcamp.protospace.dto.UserUpdateDto;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.exception.ValidationException;
import in.techcamp.protospace.repository.AffiliationRepository;
import in.techcamp.protospace.repository.PositionRepository;
import in.techcamp.protospace.repository.UserRepository;
import in.techcamp.protospace.security.JwtTokenProvider;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    user.setName(userDto.getName());
    user.setEmail(userDto.getEmail());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    user.setProfile(userDto.getProfile());

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
        user.getName(),
        user.getEmail(),
        userDto.getPosition(),
        userDto.getAffiliation(),
        userDto.getProfile());
  }

  public UserDetailResponseDto getUserDetail(Long userId) {
    // ユーザー基本情報の取得
    UserEntity user = userRepository.selectById(userId);
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません");
    }

    // 役職と所属の取得
    String position = positionRepository.findByUserId(userId);
    String affiliation = affiliationRepository.findByUserId(userId);

    // DTOに詰めて返す
    UserDetailResponseDto response = new UserDetailResponseDto();
    response.setId(user.getId());
    response.setName(user.getName());
    response.setProfile(user.getProfile());
    response.setPosition(position);
    response.setAffiliation(affiliation);

    return response;
  }

  public UserInfoDto getUserInfo(Long userId) {
    UserEntity user = userRepository.selectById(userId);

    UserInfoDto dto = new UserInfoDto();
    dto.setId(userId);
    dto.setName(user.getName());

    return dto;
  }

  public String updateUser(Long userId, UserUpdateDto dto) {

    // ユーザーの確認
    UserEntity user = userRepository.selectById(userId);
    if(user == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません");
    }

    // 本人確認
    // if(!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
    //   throw new ValidationException(
    //     Map.of("currentPassword", List.of("現在のパスワードが間違っています。")), "パスワードエラー");
    // }

  // usersテーブルの更新
    user.setName(dto.getName());
    user.setProfile(dto.getProfile());

    // 新しいパスワードの入力がある場合のみ
    // if(dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
    //   user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    // }

    userRepository.updateUser(user);

    // positionとaffiliationの更新は別途記述
    
    if(dto.getPosition() != null) {
      positionRepository.update(userId, dto.getPosition());
    }

    if(dto.getAffiliation() != null) {
      affiliationRepository.update(userId, dto.getAffiliation());
    }

    return jwtTokenProvider.generateToken(String.valueOf(userId));



  }
}
