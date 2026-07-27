package in.techcamp.protospace.controller;

import java.util.List;
import in.techcamp.protospace.dto.PrototypeDetailResponseDto;
import in.techcamp.protospace.service.PrototypeService;
import in.techcamp.protospace.entity.PrototypeEntity;
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

  // プロトタイプ一覧取得機能
    @GetMapping("/")
    public ResponseEntity<List<PrototypeEntity>> getAllPrototypes() {
        List<PrototypeEntity> prototypes = prototypeService.getAllPrototypes();
        return ResponseEntity.ok(prototypes);
    }


// プロトタイプ詳細データの取得
  @GetMapping("/{id}")
  public ResponseEntity<PrototypeDetailResponseDto> getPrototypeDetail(
      @PathVariable("id") Long id) {
    PrototypeDetailResponseDto response = prototypeService.getPrototypeDetail(id);
    return ResponseEntity.ok(response);
  }

   // プロトタイプ投稿機能
    @PostMapping("/new")
    ResponseEntity<String> createPrototype(
        @ModelAttribute PrototypeForm form,
        Authentication authentication
    ){
    try {
      Long userId = Long.valueOf(authentication.getName());

      prototypeService.createPrototype(form, userId);

      return ResponseEntity.ok("プロトタイプの投稿に成功しました。");

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body("エラーが発生しました: " + e.getMessage());
    }
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<String> updatePrototype(
        @PathVariable Long id,
        @ModelAttribute PrototypeForm form,
        Authentication authentication
    ) {
        try{
            // IDの取得
            Long userId = Long.valueOf(authentication.getName());
            // サービス層に記述
            prototypeService.updatePrototype(id, form, userId);

            return ResponseEntity.ok("プロトタイプの更新に成功しました");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("更新に失敗しました：" + e.getMessage());
        }
    }
}
