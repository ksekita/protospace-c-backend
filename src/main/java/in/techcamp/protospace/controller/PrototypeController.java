package in.techcamp.protospace.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.service.PrototypeService;
import in.techcamp.protospace.form.PrototypeForm;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/prototypes")
@RequiredArgsConstructor

public class PrototypeController {

    //データベース操作を行うmapperの依存関係    
    private final PrototypeService prototypeService;

    @GetMapping("/")
    public ResponseEntity <List<PrototypeEntity>> getAllPrototypes() {

        List<PrototypeEntity> prototypes = prototypeService.getAllPrototypes();

        return ResponseEntity.ok(prototypes);
    
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

    } catch (Exception e){
        // 処理のどこかで問題が発生した際に表示
        e.printStackTrace();
        return ResponseEntity.status(500).body("エラーが発生しました: " + e.getMessage());
    }
}
}
