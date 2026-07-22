package in.techcamp.protospace.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import in.techcamp.protospace.mapper.PrototypeMapper;
import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.form.PrototypeForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/prototypes")
@RequiredArgsConstructor

public class PrototypeController {

    //データベース操作を行うmapperの依存関係    
    private final PrototypeMapper prototypeMapper;

    // jwtのパスワード
    @Value("${app.jwt.secret}")
    private String jwtsecret;
        

    @PostMapping("/")
    ResponseEntity<String> createPrototype(
        @ModelAttribute PrototypeForm form,
        @RequestHeader("Authorization") String token
    ){
    try {
        // 誰が投稿したかを特定する 
        String jwt = token.replace("Bearer ", "");

        var key = Keys.hmacShaKeyFor(jwtsecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(jwt)
        .getPayload();

        Long userId = Long.valueOf(claims.getSubject());
    // ここまで

    // 画像ファイルの保存（名前を変えてフォルダに入れる）
        MultipartFile imageFile = form.getImage();

        String originalName = imageFile.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String savedFileName = UUID.randomUUID().toString() + extension;

        Path uploadPath = Paths.get("src/main/resources/static/uploads/");
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(savedFileName);
        imageFile.transferTo(filePath);

        PrototypeEntity entity = new PrototypeEntity();
            entity.setTitle(form.getTitle());
            entity.setCatchCopy(form.getCatchCopy());
            entity.setConcept(form.getConcept());
            entity.setImage(savedFileName); 
            entity.setUserId(userId);
            
            prototypeMapper.insert(entity);

            return ResponseEntity.ok("プロトタイプの投稿に成功しました！");

    } catch (Exception e){
        e.printStackTrace();
        return ResponseEntity.status(500).body("エラーが発生しました: " + e.getMessage());
    }
}
}
