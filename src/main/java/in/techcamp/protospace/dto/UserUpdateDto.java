package in.techcamp.protospace.dto;

// import in.techcamp.protospace.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {
  @NotBlank(message = "ユーザー名は必須です")
  @Size(max = 10, message = "ユーザー名は10文字以内で入力してください")
  private String name;

  @NotBlank(message = "役職は必須です")
  @Size(max = 50, message = "役職は50文字以内で入力してください")
  private String position;

  @NotBlank(message = "所属は必須です")
  @Size(max = 50, message = "所属は50文字以内で入力してください")
  private String affiliation;

  @NotBlank(message = "プロフィールは必須です")
  @Size(max = 1000, message = "プロフィールは1000文字以内で入力してください")
  private String profile;


}
