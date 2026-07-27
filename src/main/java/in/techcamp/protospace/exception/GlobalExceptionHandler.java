package in.techcamp.protospace.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // @Valid によるアノテーションバリデーションエラー (@NotBlank, @Email等)をリストに格納
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    Map<String, Object> body = new HashMap<>();
    body.put("message", "入力内容に不備があります");
    body.put("errors", errors);

    return ResponseEntity.status(HttpStatus.valueOf(422)).body(body);
  }

  // UserService 内でのビジネスロジックエラー (パスワード不一致・メール重複等)
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Map<String, Object>> handleCustomValidationException(
      ValidationException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", ex.getMessage());
    body.put("errors", ex.getErrors());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  // ログイン認証失敗エラー
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, String>> handleAuthenticationException(
      AuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
  }

  // サーバー側のエラー
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleUnexpectedException(Exception ex) {
    log.error("【サーバーエラー】予期せぬ例外が発生しました", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("message", "サーバー内部で予期せぬエラーが発生しました"));
  }
}
