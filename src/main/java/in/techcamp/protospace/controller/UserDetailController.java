package in.techcamp.protospace.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.techcamp.protospace.dto.UserDetailResponseDto;
import in.techcamp.protospace.dto.UserUpdateDto;
import in.techcamp.protospace.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserDetailController {

  private final UserService userService;

  // ユーザー詳細情報の取得 API
  @GetMapping("/{id}")
  public ResponseEntity<UserDetailResponseDto> getUserDetail(@PathVariable("id") Long id) {
    UserDetailResponseDto response = userService.getUserDetail(id);
    return ResponseEntity.ok(response);
  }

  // ユーザー編集機能
  @PutMapping("/{id}")
  public ResponseEntity<Map<String, String>> updateUser(
    @PathVariable("id") Long id,
    @Valid @RequestBody UserUpdateDto dto,
    Authentication authentication
  ) {
    // ログイン中のユーザー情報を取得
    Long currentUserId = Long.valueOf(authentication.getName());

    // 編集するユーザーかどうかの確認
    if(!currentUserId.equals(id)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
      .body(Map.of("error", "他人のプロフィールは編集できません"));
    }

    //　サービスに記述
    String newToken = userService.updateUser(id, dto);

    // レスポンス
    return ResponseEntity.ok(Map.of(
      "message", "登録情報を更新しました",
      "token", newToken
    ));
  }
}
