package in.techcamp.protospace.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import in.techcamp.protospace.dto.UserDto;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.exception.ValidationException;
import in.techcamp.protospace.repository.JobRepository;
import in.techcamp.protospace.repository.PositionRepository;
import in.techcamp.protospace.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PositionRepository positionRepository;
  @Mock
  private JobRepository jobRepository;
  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private UserDto validUserDto;

  @BeforeEach
  void setUp() {
    validUserDto = new UserDto();
    validUserDto.setUsername("テストユーザー");
    validUserDto.setEmail("test@example.com");
    validUserDto.setPassword("Password123456!");
    validUserDto.setPasswordConfirm("Password123456!");
    validUserDto.setPositions(List.of("リーダー"));
    validUserDto.setJobs(List.of("エンジニア"));
  }

  @Test
  @DisplayName("【正常系】ユーザーおよび役職・職業が正しく登録されること")
  void insertUser_Success() {
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
    when(userRepository.insertUser(any(UserEntity.class))).thenAnswer(invocation -> {
      UserEntity entity = invocation.getArgument(0);
      entity.setId(10L); 
      return 1;
    });

    userService.insertUser(validUserDto);

    verify(userRepository).insertUser(any(UserEntity.class));
    verify(positionRepository).insert(10L, "リーダー");
    verify(jobRepository).insert(10L, "エンジニア");
  }

  @Test
  @DisplayName("【異常系】パスワードと確認パスワードが一致しない場合にValidationExceptionが発生すること")
  void insertUser_PasswordMismatch_ThrowsException() {
    validUserDto.setPasswordConfirm("MismatchPassword");

    assertThatThrownBy(() -> userService.insertUser(validUserDto))
        .isInstanceOf(ValidationException.class)
        .hasMessage("パスワードが一致しません");
  }

  @Test
  @DisplayName("【異常系】既にメールアドレスが存在する場合にValidationExceptionが発生すること")
  void insertUser_DuplicateEmail_ThrowsException() {
    when(userRepository.existsByEmail(validUserDto.getEmail())).thenReturn(true);

    assertThatThrownBy(() -> userService.insertUser(validUserDto))
        .isInstanceOf(ValidationException.class)
        .hasMessage("登録エラー");
  }
}