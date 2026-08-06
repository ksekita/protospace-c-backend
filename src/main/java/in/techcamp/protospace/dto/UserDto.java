package in.techcamp.protospace.dto;

import in.techcamp.protospace.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// ユーザーの新規登録の際にフロントエンドから送られてくるJSONデータを受け取る
@Data
public class UserDto {
  @NotBlank(message = "ユーザー名は必須です")
  @Size( max = 50, message = "ユーザー名は50文字以内で入力してください")
  private String name;

  @NotBlank(message = "メールアドレスは必須です")
  @Email(message = "メールアドレスの形式が正しくありません")
  @Size( max = 255, message = "メールアドレスは255文字以内で入力してください")
  private String email;

  @NotBlank(message = "パスワードは必須です")
  @Size( max = 255, message = "パスワードは6文字以上、255文字以内で入力してください")
  @ValidPassword
  private String password;

  @NotBlank(message = "パスワード(確認)は必須です")
  private String passwordConfirm;

  @NotBlank(message = "役職は必須です")
  @Size( max = 50, message = "役職は50文字以内で入力してください")
  private String position;

  @NotBlank(message = "所属は必須です")
  @Size( max = 50, message = "所属は50文字以内で入力してください")
  private String affiliation;

  @NotBlank(message = "プロフィールは必須です")
  @Size( max = 1000, message = "プロフィールは1000文字以内で入力してください")
  private String profile;
  
}