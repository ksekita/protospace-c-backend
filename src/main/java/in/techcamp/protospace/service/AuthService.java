package in.techcamp.protospace.service;

import in.techcamp.protospace.dto.LoginRequestDto;
import in.techcamp.protospace.dto.LoginResponseDto;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.exception.AuthenticationException;
import in.techcamp.protospace.repository.AffiliationRepository;
import in.techcamp.protospace.repository.PositionRepository;
import in.techcamp.protospace.repository.UserRepository;
import in.techcamp.protospace.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final UserRepository userRepository;
  private final PositionRepository positionRepository;
  private final AffiliationRepository affiliationRepository;

  public AuthService(
      AuthenticationManager authenticationManager,
      JwtTokenProvider tokenProvider,
      UserRepository userRepository,
      PositionRepository positionRepository,
      AffiliationRepository affiliationRepository) {
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
    this.userRepository = userRepository;
    this.positionRepository = positionRepository;
    this.affiliationRepository = affiliationRepository;
  }

  // ログイン処理
  public LoginResponseDto login(LoginRequestDto request) {
    try {
      // パスワードの照合・認証の実行
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    } catch (org.springframework.security.core.AuthenticationException e) {
      // ユーザー側のエラー
      throw new AuthenticationException("メールアドレスまたはパスワードが正しくありません");
    }

    UserEntity user = userRepository.selectByEmail(request.getEmail());
    if (user == null) {
      throw new AuthenticationException("ユーザー情報が取得できません");
    }

    // ログインユーザーの役職と職業を取得
    String token = tokenProvider.generateToken(String.valueOf(user.getId()));
    String position = positionRepository.findByUserId(user.getId());
    String affiliation = affiliationRepository.findByUserId(user.getId());

    // トークンやユーザー基本情報などをまとめたDtoを作成し、返す。
    return new LoginResponseDto(
        token, user.getId(), user.getEmail(), user.getUsername(), position, affiliation);
  }
}
