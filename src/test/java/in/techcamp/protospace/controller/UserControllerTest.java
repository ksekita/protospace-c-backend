package in.techcamp.protospace.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.techcamp.protospace.dto.LoginRequestDto;
import in.techcamp.protospace.dto.LoginResponseDto;
import in.techcamp.protospace.dto.UserDto;
import in.techcamp.protospace.dto.UserResponseDto;
import in.techcamp.protospace.exception.AuthenticationException;
import in.techcamp.protospace.exception.GlobalExceptionHandler;
import in.techcamp.protospace.exception.ValidationException;
import in.techcamp.protospace.service.AuthService;
import in.techcamp.protospace.service.UserService;
import java.util.List;
import java.util.Map;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Mock private UserService userService;
  @Mock private AuthService authService;

  @InjectMocks private UserController userController;

  @Captor private ArgumentCaptor<UserDto> userDtoCaptor;
  @Captor private ArgumentCaptor<LoginRequestDto> loginRequestDtoCaptor;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();

    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();

    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator)
            .build();
  }

  // ユーザー新規登録のテスト
  @Nested
  @DisplayName("ユーザー新規登録 API (/api/auth/register)")
  class RegisterTest {

    @Test
    @DisplayName("【正常系】適切なユーザー情報で登録が成功する (200 OK または 201 Created)")
    void register_Success() throws Exception {
      UserDto dto = createValidUserDto();

      UserResponseDto mockResponse =
          new UserResponseDto(
              "mock-jwt-token",
              1L,
              "テスト太郎",
              "test@example.com",
              "リーダー",
              "エンジニア");

      when(userService.insertUser(any(UserDto.class))).thenReturn(mockResponse);

      mockMvc
          .perform(
              post("/api/auth/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
          // Controller側の実装が 201 Created の場合は .isCreated()、200 OK の場合は .isOk() にしてください
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.token").value("mock-jwt-token"))
          .andExpect(jsonPath("$.id").value(1))
          .andExpect(jsonPath("$.username").value("テスト太郎"))
          .andExpect(jsonPath("$.email").value("test@example.com"))
          .andExpect(jsonPath("$.position").value("リーダー"))
          .andExpect(jsonPath("$.affiliation").value("エンジニア"));

      // ArgumentCaptor で Service に渡されたリクエスト引数を検証
      verify(userService).insertUser(userDtoCaptor.capture());
      UserDto capturedDto = userDtoCaptor.getValue();
      assertThat(capturedDto.getEmail()).isEqualTo("test@example.com");
      assertThat(capturedDto.getPosition()).isEqualTo("リーダー");
      assertThat(capturedDto.getAffiliation()).isEqualTo("エンジニア");
    }

    @Test
    @DisplayName("【異常系】Bean Validationエラー時：メールアドレスの形式が不正 (422 Unprocessable Entity)")
    void register_InvalidEmail_Returns400() throws Exception {
      UserDto dto = createValidUserDto();
      dto.setEmail("invalid-email-format");

      mockMvc
          .perform(
              post("/api/auth/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
                
          .andExpect(status().is(422));
    }

    @Test
    @DisplayName("【異常系】Service層でバリデーション例外が発生した場合に 400 Bad Request とエラー詳細を返す")
    void register_ServiceValidationException_Returns400() throws Exception {

      UserDto dto = createValidUserDto();

      when(userService.insertUser(any(UserDto.class)))
          .thenThrow(
              new ValidationException(
                  Map.of("email", List.of("このメールアドレスは既に登録されています。")), "バリデーションエラー"));

      mockMvc
          .perform(
              post("/api/auth/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value("バリデーションエラー"))
          .andExpect(jsonPath("$.errors.email[0]").value("このメールアドレスは既に登録されています。"));
    }
  }

  // ログインのテスト
  @Nested
  @DisplayName("ログイン API (/api/auth/login)")
  class LoginTest {

    @Test
    @DisplayName("【正常系】正しい認証情報でログインに成功しトークンが返却される (200 OK)")
    void login_Success() throws Exception {
      LoginRequestDto request = new LoginRequestDto();
      request.setEmail("test@example.com");
      request.setPassword("Password123456!");

      LoginResponseDto response =
          new LoginResponseDto(
              "dummy.jwt.token",
              1L,
              "test@example.com",
              "テスト太郎",
              "マネージャー",
              "エンジニア");

      when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

      mockMvc
          .perform(
              post("/api/auth/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.token").value("dummy.jwt.token"))
          .andExpect(jsonPath("$.id").value(1L))
          .andExpect(jsonPath("$.username").value("テスト太郎"))
          .andExpect(jsonPath("$.position").value("マネージャー"))
          .andExpect(jsonPath("$.affiliation").value("エンジニア"));

      // ArgumentCaptor で AuthService に渡されたリクエスト DTO を検証
      verify(authService).login(loginRequestDtoCaptor.capture());
      LoginRequestDto capturedRequest = loginRequestDtoCaptor.getValue();
      assertThat(capturedRequest.getEmail()).isEqualTo("test@example.com");
      assertThat(capturedRequest.getPassword()).isEqualTo("Password123456!");
    }

    @Test
    @DisplayName("【異常系】認証失敗時：AuthenticationExceptionが発生した場合に 401 Unauthorized を返す")
    void login_InvalidCredentials_Returns401() throws Exception {
      LoginRequestDto request = new LoginRequestDto();
      request.setEmail("test@example.com");
      request.setPassword("WrongPassword");

      when(authService.login(any(LoginRequestDto.class)))
          .thenThrow(new AuthenticationException("メールアドレスまたはパスワードが正しくありません"));

      mockMvc
          .perform(
              post("/api/auth/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.message").value("メールアドレスまたはパスワードが正しくありません"));
    }
  }

  private UserDto createValidUserDto() {
    UserDto dto = new UserDto();
    dto.setUsername("テスト太郎");
    dto.setEmail("test@example.com");
    dto.setPassword("ValidPassword123!");
    dto.setPasswordConfirm("ValidPassword123!");
    dto.setPosition("リーダー");
    dto.setAffiliation("エンジニア");
    return dto;
  }
}