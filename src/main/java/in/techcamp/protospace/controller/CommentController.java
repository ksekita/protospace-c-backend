package in.techcamp.protospace.controller;

import in.techcamp.protospace.dto.CommentRequestDto;
import in.techcamp.protospace.dto.CommentResponseDto;
import in.techcamp.protospace.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/prototypes/{prototypeId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // コメント一覧の取得（誰でも閲覧可能を想定）
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable("prototypeId") Long prototypeId) {
        List<CommentResponseDto> comments = commentService.getCommentsByPrototypeId(prototypeId);
        return ResponseEntity.ok(comments);
    }

    // 新規コメントの投稿（ログイン必須を想定）
    @PostMapping
    public ResponseEntity<String> createComment(
            @PathVariable("prototypeId") Long prototypeId,
            @RequestBody @Valid CommentRequestDto request,
            Authentication authentication) {
        
        // 解決策Aにより、トークンから確実にユーザーID（数値文字列）が取得できる
        Long userId = Long.valueOf(authentication.getName());

        commentService.createComment(prototypeId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body("コメントを投稿しました。");
    }
}