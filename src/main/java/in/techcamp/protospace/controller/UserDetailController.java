package in.techcamp.protospace.controller;

import in.techcamp.protospace.dto.UserDetailResponseDto;
import in.techcamp.protospace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}