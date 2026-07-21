package in.techcamp.protospace.exception;

// 自作エラーを記載(元はRuntimeException)
public class AuthenticationException extends RuntimeException {
  public AuthenticationException(String message) {
    super(message);
  }
}