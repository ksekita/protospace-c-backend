package in.techcamp.protospace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import in.techcamp.protospace.dto.UserDto;
import in.techcamp.protospace.dto.UserResponseDto;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.exception.ValidationException;
import in.techcamp.protospace.repository.AffiliationRepository;
import in.techcamp.protospace.repository.PositionRepository;
import in.techcamp.protospace.repository.UserRepository;
import in.techcamp.protospace.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PositionRepository positionRepository;
  @Mock private AffiliationRepository affiliationRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtTokenProvider jwtTokenProvider; // 🌟 JwtTokenProviderのモックを追加

  @InjectMocks private UserService userService;

  @Captor private ArgumentCaptor<UserEntity> userEntityCaptor;

  private UserDto validUserDto;

  @BeforeEach
  void setUp() {
    validUserDto = new UserDto();
    validUserDto.setUsername("テストユーザー");
    validUserDto.setEmail("test@example.com");
    validUserDto.setPassword("Password123456!");
    validUserDto.setPasswordConfirm("Password123456!");
    validUserDto.setPosition("リーダー");
    validUserDto.setAffiliation("エンジニア");
  }

  @Nested
  @DisplayName("ユーザー登録処理 (insertUser)")
  class InsertUserTest {

    @Test
    @DisplayName("【正常系】ユーザー基本情報およびハッシュ化されたパスワード、役職・配属が正しく登録され、UserResponseDtoが返却されること")
    void insertUser_Success() {
      when(userRepository.existsByEmail(anyString())).thenReturn(false);
      when(passwordEncoder.encode("Password123456!")).thenReturn("hashedPassword123");
      when(userRepository.insertUser(any(UserEntity.class)))
          .thenAnswer(
              invocation -> {
                UserEntity entity = invocation.getArgument(0);
                entity.setId(10L);
                return 1;
              });
      when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("mock-jwt-token"); // 🌟 トークン生成のモック

      // 処理の呼び出しと結果の検証
      UserResponseDto response = userService.insertUser(validUserDto);

      assertThat(response).isNotNull();
      assertThat(response.getToken()).isEqualTo("mock-jwt-token");
      assertThat(response.getId()).isEqualTo(10L);
      assertThat(response.getUsername()).isEqualTo("テストユーザー");
      assertThat(response.getEmail()).isEqualTo("test@example.com");
      assertThat(response.getPosition()).isEqualTo("リーダー");
      assertThat(response.getAffiliation()).isEqualTo("エンジニア");

      // ArgumentCaptor で Entity に詰め替えられた値とパスワードハッシュ化を正確に検証
      verify(userRepository).insertUser(userEntityCaptor.capture());
      UserEntity capturedEntity = userEntityCaptor.getValue();
      assertThat(capturedEntity.getUsername()).isEqualTo("テストユーザー");
      assertThat(capturedEntity.getEmail()).isEqualTo("test@example.com");
      assertThat(capturedEntity.getPasswordHash()).isEqualTo("hashedPassword123");

      // 役職・職業がそれぞれ1回ずつ登録されたか検証
      verify(positionRepository).insert(10L, "リーダー");
      verify(affiliationRepository).insert(10L, "エンジニア");
    }

    @Test
    @DisplayName("【異常系】パスワードと確認用パスワードが一致しない場合にValidationExceptionが発生し、DB登録が行われないこと")
    void insertUser_PasswordMismatch_ThrowsException() {
      validUserDto.setPasswordConfirm("MismatchPassword");

      // 例外メッセージだけでなく、フィールドごとのエラーメッセージ構造まで検証
      assertThatThrownBy(() -> userService.insertUser(validUserDto))
          .isInstanceOf(ValidationException.class)
          .satisfies(
              ex -> {
                ValidationException valEx = (ValidationException) ex;
                assertThat(valEx.getMessage()).isEqualTo("パスワードが一致しません");
                assertThat(valEx.getErrors()).containsKey("passwordConfirm");
                assertThat(valEx.getErrors().get("passwordConfirm"))
                    .contains("パスワードが一致しません");
              });

      // 異常系では DB へのアクセスが発生しないことを保証
      verify(userRepository, never()).insertUser(any());
    }

    @Test
    @DisplayName("【異常系】既にメールアドレスが存在する場合にValidationExceptionが発生し、DB登録が行われないこと")
    void insertUser_DuplicateEmail_ThrowsException() {
      when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

      assertThatThrownBy(() -> userService.insertUser(validUserDto))
          .isInstanceOf(ValidationException.class)
          .satisfies(
              ex -> {
                ValidationException valEx = (ValidationException) ex;
                assertThat(valEx.getMessage()).isEqualTo("登録エラー");
                assertThat(valEx.getErrors()).containsKey("email");
                assertThat(valEx.getErrors().get("email"))
                    .contains("このメールアドレスは既に登録されています。");
              });

      verify(userRepository, never()).insertUser(any());
    }
  }
}