package in.techcamp.protospace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
//ユーザー情報を返すためのファイル
public class UserResponseDto {
  private String token;
  private Long id;
  private String username;
  private String email;
  private String position;
  private String affiliation;
}