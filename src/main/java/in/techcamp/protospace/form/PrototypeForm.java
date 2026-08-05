package in.techcamp.protospace.form;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
// @AllArgsConstructor
public class PrototypeForm {

  @NotBlank(message = "タイトルは必須です")
  @Size(max = 50, message = "タイトルは50文字以内で入力してください")
  private String title;

  @NotBlank(message = "メッセージは必須です")
  @Size(max = 50, message = "キャッチコピーは50文字以内で入力してください")
  private String catchCopy;

  @NotBlank(message = "コンセプトは必須です")
  @Size(max = 1000, message = "コンセプトは1000文字以内で入力してください")
  private String concept;
  
  private MultipartFile image;
}
