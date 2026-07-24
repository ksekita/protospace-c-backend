package in.techcamp.protospace.controller;

import in.techcamp.protospace.form.PrototypeForm;
import in.techcamp.protospace.service.PrototypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prototypes")
@RequiredArgsConstructor
public class PrototypeController {

  private final PrototypeService prototypeService;

  // プロトタイプの投稿
  @PostMapping("/")
  ResponseEntity<String> createPrototype(
      @ModelAttribute PrototypeForm form, Authentication authentication) {
    try {
      Long userId = Long.valueOf(authentication.getName());

      prototypeService.createPrototype(form, userId);

      return ResponseEntity.ok("プロトタイプの投稿に成功しました。");

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body("エラーが発生しました: " + e.getMessage());
    }
  }
}
