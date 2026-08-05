package in.techcamp.protospace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// ログインしたいユーザーのメールアドレスとパスワードのJSONデータをJavaオブジェクトとして受け取る
@Data
public class LoginRequestDto {
  @NotBlank(message = "メールアドレスは必須です")
  @Size( max = 255, message = "255文字以内で入力してください" )
  private String email;

  @NotBlank(message = "パスワードは必須です")
  @Size( max = 255, message = "パスワードは255文字以内で入力してください" )
  private String password;
}
