package in.techcamp.protospace.controller;

import in.techcamp.protospace.dto.PrototypeDetailResponseDto;
import in.techcamp.protospace.service.PrototypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import in.techcamp.protospace.form.PrototypeForm;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/prototypes")
public class PrototypeController {

  private final PrototypeService prototypeService;

  public PrototypeController(PrototypeService prototypeService) {
    this.prototypeService = prototypeService;
  }

// プロトタイプ詳細データの取得
  @GetMapping("/{id}")
  public ResponseEntity<PrototypeDetailResponseDto> getPrototypeDetail(
      @PathVariable("id") Long id) {
    PrototypeDetailResponseDto response = prototypeService.getPrototypeDetail(id);
    return ResponseEntity.ok(response);
  }

   // プロトタイプ投稿機能
    @PostMapping("/")
    ResponseEntity<String> createPrototype(
        @ModelAttribute PrototypeForm form,
        Authentication authentication
    ){
    try {
      Long userId = Long.valueOf(authentication.getName());

      prototypeService.createPrototype(form, userId);

      return ResponseEntity.ok("プロトタイプの投稿に成功しました。");

    } catch (Exception e) {
      // 処理のどこかで問題が発生した際に表示
      e.printStackTrace();
      return ResponseEntity.status(500).body("エラーが発生しました: " + e.getMessage());
    }
  }
}





