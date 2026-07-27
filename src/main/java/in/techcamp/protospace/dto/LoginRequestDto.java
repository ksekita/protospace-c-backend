package in.techcamp.protospace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// ログインしたいユーザーのメールアドレスとパスワードのJSONデータをJavaオブジェクトとして受け取る
@Data
public class LoginRequestDto {
  @NotBlank(message = "メールアドレスは必須です")
  private String email;

  @NotBlank(message = "パスワードは必須です")
  private String password;
}
