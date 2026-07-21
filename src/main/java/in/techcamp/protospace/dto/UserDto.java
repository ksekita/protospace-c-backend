package in.techcamp.protospace.dto;

import java.util.List;
import in.techcamp.protospace.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

//ユーザーの新規登録の際にフロントエンドから送られてくるJSONデータを受け取る
@Data
public class UserDto {
  @NotBlank(message = "ユーザー名は必須です")
  @Size(max = 10, message = "ユーザー名は10文字以内で入力してください")
  private String username;

  @NotBlank(message = "メールアドレスは必須です")
  @Email(message = "メールアドレスの形式が正しくありません")
  private String email;

  @NotBlank(message = "パスワードは必須です")
  @ValidPassword
  private String password;

  @NotBlank(message = "パスワード(確認)は必須です")
  private String passwordConfirm;

  // フロントエンドから役職などのデータを配列形式で受け取るため追加
  // 格納するだけなのでアノテーションはない
  private List<String> positions;
  private List<String> jobs;
}