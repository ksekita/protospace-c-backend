package in.techcamp.protospace.controller;

import in.techcamp.protospace.dto.PrototypeDetailResponseDto;
import in.techcamp.protospace.dto.PrototypeLikeResponseDto;
import in.techcamp.protospace.dto.PrototypeListDto;
import in.techcamp.protospace.dto.UserPrototypeListDto;
import in.techcamp.protospace.form.PrototypeForm;
import in.techcamp.protospace.service.PrototypeService;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/prototypes")
public class PrototypeController {

  private final PrototypeService prototypeService;

  public PrototypeController(PrototypeService prototypeService) {
    this.prototypeService = prototypeService;
  }

  // プロトタイプ一覧取得機能兼検索機能
  @GetMapping({"/",""})
  public ResponseEntity<List<PrototypeListDto>> getAllPrototypes(
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "sort", defaultValue = "latest") String sort,
      Authentication authentication) {
      Long loggedInUserId = (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser"))
        ? Long.valueOf(authentication.getName()) : 0L;
    // Serviceにkeywordを渡す
    List<PrototypeListDto> prototypes = prototypeService.getAllPrototypes(keyword,sort,loggedInUserId);
    return ResponseEntity.ok(prototypes);
  }

  // プロトタイプ詳細データの取得
  @GetMapping("/{id}")
  public ResponseEntity<PrototypeDetailResponseDto> getPrototypeDetail(
      @PathVariable("id") Long id,
      Authentication authentication) {
        Long loggedInUserId = (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser"))
        ? Long.valueOf(authentication.getName()) : 0L;
    PrototypeDetailResponseDto response = prototypeService.getPrototypeDetail(id,loggedInUserId);
    return ResponseEntity.ok(response);
  }

  // プロトタイプ投稿機能
  @PostMapping("/")
  ResponseEntity<Map<String, String>> createPrototype(
      @ModelAttribute PrototypeForm form, Authentication authentication) {
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
  public ResponseEntity<Map<String, String>> updatePrototype(
      @PathVariable("id") Long id,
      @ModelAttribute PrototypeForm form,
      Authentication authentication) {
    try {
      // IDの取得
      Long userId = Long.valueOf(authentication.getName());
      // サービス層に記述
      prototypeService.updatePrototype(id, form, userId);

      return ResponseEntity.ok(Map.of("message", "プロトタイプの更新に成功しました"));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Map.of("error", "更新に失敗しました：" + e.getMessage()));
    }
  }

  //プロトタイプ削除機能
  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, String>> deletePrototype(
      @PathVariable("id") Long id, Authentication authentication) {
    try {
      Long userId = Long.valueOf(authentication.getName());
      prototypeService.deletePrototype(id, userId);
      return ResponseEntity.ok(Map.of("message", "プロトタイプの削除に成功しました"));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Map.of("error", "削除に失敗しました：" + e.getMessage()));
    }
  }

  // ユーザーが作成したプロトタイプ一覧の取得
  @GetMapping("/users/{userId}")
  public ResponseEntity<List<UserPrototypeListDto>> getPrototypesByUserId(
      @PathVariable("userId") Long userId,
      @RequestParam(name = "sort", defaultValue = "latest") String sort,
      Authentication authentication) {
        Long loggedInUserId = (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser"))
        ? Long.valueOf(authentication.getName()) : 0L;
    List<UserPrototypeListDto> response = prototypeService.getPrototypesByUserId(userId,sort,loggedInUserId);
    return ResponseEntity.ok(response);
  }

  //お気に入り機能の切り替え
  @PostMapping("/{id}/like")
  public ResponseEntity<PrototypeLikeResponseDto> toggleLike(
    @PathVariable("id") Long id,
  Authentication authentication) {
    try{
      Long userId=Long.valueOf(authentication.getName());
      PrototypeLikeResponseDto response=prototypeService.toggleLike(id,userId);
      return ResponseEntity.ok(response);
    }
    catch(Exception e){
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
     
  }
}
