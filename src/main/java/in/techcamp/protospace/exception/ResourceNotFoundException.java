package in.techcamp.protospace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  // エラーメッセージのみを返す
  public ResourceNotFoundException(String message) {
    super(message);
  }

  // エラーメッセージとその原因を返す
  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
