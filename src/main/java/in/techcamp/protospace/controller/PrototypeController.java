package in.techcamp.protospace.controller;

import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;


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
    @PostMapping("/")
    ResponseEntity<Map<String,String>> createPrototype(
        @ModelAttribute PrototypeForm form,
        Authentication authentication
    ){
    try {
      Long userId = Long.valueOf(authentication.getName());

      prototypeService.createPrototype(form, userId);

      return ResponseEntity.ok(Map.of("message", "プロトタイプの投稿に成功しました。")); 

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("error", "エラーが発生しました: " + e.getMessage()));
    }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String,String>> updatePrototype(
        @PathVariable("id") Long id,
        @ModelAttribute PrototypeForm form,
        Authentication authentication
    ) {
        try{
            // IDの取得
            Long userId = Long.valueOf(authentication.getName());
            // サービス層に記述
            prototypeService.updatePrototype(id, form, userId);

            return ResponseEntity.ok(Map.of("message","プロトタイプの更新に成功しました"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "更新に失敗しました：" + e.getMessage()));
        }
    }
  

  @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePrototype(
        @PathVariable("id") Long id,
        Authentication authentication
    ) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            prototypeService.deletePrototype(id, userId);
            return ResponseEntity.ok(Map.of("message", "プロトタイプの削除に成功しました"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "削除に失敗しました：" + e.getMessage()));
        }
    }
}
