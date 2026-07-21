package in.techcamp.protospace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.techcamp.protospace.dto.LoginRequestDto;
import in.techcamp.protospace.dto.LoginResponseDto;
import in.techcamp.protospace.dto.UserDto;
import in.techcamp.protospace.exception.AuthenticationException; // CustomAuthenticationException に変更している場合はそちらを指定
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// 🌟 SpringBootTestやWebMvcTestを使わず、純粋なMockito拡張を使用します
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Mock
  private UserService userService;

  @Mock
  private AuthService authService;

  @InjectMocks
  private UserController userController;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    // 🌟 autoconfigure に頼らず、手動で MockMvc を構築します
    // これによりセキュリティフィルターを完全にバイパスしつつ、コントローラーの挙動だけをテストできます
    mockMvc = MockMvcBuilders.standaloneSetup(userController)
        .setControllerAdvice(new GlobalExceptionHandler()) // 例外ハンドラーも手動で適用
        .build();
  }

  // -----------------------------------------------------------------
  // ユーザー新規登録のテスト
  // -----------------------------------------------------------------
  @Nested
  @DisplayName("ユーザー新規登録 API (/api/auth/register)")
  class RegisterTest {

    @Test
    @DisplayName("【正常系】適切なユーザー情報で登録が成功する (200 OK)")
    void register_Success() throws Exception {
      UserDto dto = createValidUserDto();
      when(userService.insertUser(any(UserDto.class))).thenReturn(1);

      mockMvc.perform(post("/api/auth/register")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").value(1));
    }

    @Test
    @DisplayName("【異常系】メールアドレスの形式が正しくない場合 (422 Unprocessable Entity)")
    void register_InvalidEmail_Returns422() throws Exception {
      UserDto dto = createValidUserDto();
      dto.setEmail("invalid-email-format");

      // @Valid によるバリデーションは standaloneSetup の場合、
      // 厳密なテストには手動の Validator 登録が必要ですが、
      // Controller側でエラーハンドリングをどう実装しているかによって
      // ここは 400系(422) が返ることを検証します。
      // もしここだけテストが通らない場合は、このテストメソッドのみ調整します。
      mockMvc.perform(post("/api/auth/register")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("【異常系】パスワードと確認用パスワードが不一致の場合 (400 Bad Request)")
    void register_PasswordMismatch_Returns400() throws Exception {
      UserDto dto = createValidUserDto();
      dto.setPasswordConfirm("DifferentPassword123!");

      when(userService.insertUser(any(UserDto.class)))
          .thenThrow(new ValidationException(
              Map.of("passwordConfirm", List.of("パスワードが一致しません")), "パスワードが一致しません"));

      mockMvc.perform(post("/api/auth/register")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value("パスワードが一致しません"));
    }

    @Test
    @DisplayName("【異常系】メールアドレスが既に存在する場合 (400 Bad Request)")
    void register_DuplicateEmail_Returns400() throws Exception {
      UserDto dto = createValidUserDto();

      when(userService.insertUser(any(UserDto.class)))
          .thenThrow(new ValidationException(
              Map.of("email", List.of("このメールアドレスは既に登録されています。")), "登録エラー"));

      mockMvc.perform(post("/api/auth/register")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors.email[0]").value("このメールアドレスは既に登録されています。"));
    }
  }

  // -----------------------------------------------------------------
  // ログインのテスト
  // -----------------------------------------------------------------
  @Nested
  @DisplayName("ログイン API (/api/auth/login)")
  class LoginTest {

    @Test
    @DisplayName("【正常系】正しい認証情報でログインに成功しトークンが返却される (200 OK)")
    void login_Success() throws Exception {
      LoginRequestDto request = new LoginRequestDto();
      request.setEmail("test@example.com");
      request.setPassword("Password123456!");

      LoginResponseDto response = new LoginResponseDto(
          "dummy.jwt.token", 1L, "test@example.com", "テスト太郎",
          List.of("マネージャー"), List.of("エンジニア")
      );

      when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

      mockMvc.perform(post("/api/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.token").value("dummy.jwt.token"))
          .andExpect(jsonPath("$.username").value("テスト太郎"))
          .andExpect(jsonPath("$.positions[0]").value("マネージャー"));
    }

    @Test
    @DisplayName("【異常系】パスワードまたはメールアドレスが誤っている場合 (401 Unauthorized)")
    void login_InvalidCredentials_Returns401() throws Exception {
      LoginRequestDto request = new LoginRequestDto();
      request.setEmail("test@example.com");
      request.setPassword("WrongPassword");

      when(authService.login(any(LoginRequestDto.class)))
          .thenThrow(new AuthenticationException("メールアドレスまたはパスワードが正しくありません"));

      mockMvc.perform(post("/api/auth/login")
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
    dto.setPositions(List.of("リーダー"));
    dto.setJobs(List.of("エンジニア"));
    return dto;
  }
}