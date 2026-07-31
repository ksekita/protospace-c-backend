package in.techcamp.protospace.controller;

import in.techcamp.protospace.dto.LoginRequestDto;
import in.techcamp.protospace.dto.LoginResponseDto;
import in.techcamp.protospace.dto.UserDto;
import in.techcamp.protospace.dto.UserInfoDto;
import in.techcamp.protospace.dto.UserResponseDto;
import in.techcamp.protospace.service.AuthService;
import in.techcamp.protospace.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/auth")
public class UserController {

  private final UserService userService;
  private final AuthService authService;

  public UserController(UserService userService, AuthService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  // 新規登録
  @PostMapping("/register")
  public ResponseEntity<UserResponseDto> register(@RequestBody @Valid UserDto userDto) {
    UserResponseDto response = userService.insertUser(userDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // ログイン
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
    LoginResponseDto response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/user")
  public ResponseEntity<UserInfoDto> getUser(Authentication authentication) {
    UserInfoDto dto = new UserInfoDto();
    if (authentication == null) {
      dto.setId(null);
      dto.setName("テストユーザー");
    }else{
      Long userId = Long.valueOf(authentication.getName());
      dto = userService.getUserInfo(userId);
    }

    return ResponseEntity.ok(dto);
  }

}
