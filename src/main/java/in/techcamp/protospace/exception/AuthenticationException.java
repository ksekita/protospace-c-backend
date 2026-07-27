package in.techcamp.protospace.exception;

// 自作エラーを記載(RuntimeExceptionを拡張したもの)
public class AuthenticationException extends RuntimeException {
  public AuthenticationException(String message) {
    super(message);
  }
}
