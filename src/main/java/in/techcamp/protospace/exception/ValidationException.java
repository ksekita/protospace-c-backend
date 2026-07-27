package in.techcamp.protospace.exception;

import java.util.List;
import java.util.Map;

// 自作例外処理
public class ValidationException extends RuntimeException {
  private final Map<String, List<String>> errors;

  public ValidationException(Map<String, List<String>> errors, String message) {
    super(message);
    this.errors = errors;
  }

  public Map<String, List<String>> getErrors() {
    return errors;
  }
}
