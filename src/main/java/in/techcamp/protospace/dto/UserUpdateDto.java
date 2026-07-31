package in.techcamp.protospace.dto;

import in.techcamp.protospace.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateDto {
  @NotBlank(message = "ユーザー名は必須です")
  @Size(max = 10, message = "ユーザー名は10文字以内で入力してください")
  private String name;

  @NotBlank(message = "メールアドレスは必須です")
  @Email(message = "メールアドレスの形式が正しくありません")
  private String email;

  @NotBlank(message = "役職は必須です")
  @Size(max = 50, message = "役職は50文字以内で入力してください")
  private String position;

  @NotBlank(message = "所属は必須です")
  @Size(max = 50, message = "所属は50文字以内で入力してください")
  private String affiliation;

  @NotBlank(message = "プロフィールは必須です")
  @Size(max = 200, message = "プロフィールは200文字以内で入力してください")
  private String profile;

  @NotBlank(message = "現在のパスワードは必須です")
  private String currentPassword;

  @Size(min = 6 , max = 64 , message = "新しいパスワードは6文字以上64字以内で入力してください")
  @ValidPassword
  private String newPassword;

}
