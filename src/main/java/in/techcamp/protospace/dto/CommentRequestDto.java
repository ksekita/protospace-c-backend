package in.techcamp.protospace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequestDto {
  @NotBlank(message = "コメントを入力してください")
  @Size(max = 255, message="コメントは255文字以内で入力してください")
  private String content;
}
