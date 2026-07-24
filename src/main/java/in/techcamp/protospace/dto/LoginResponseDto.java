package in.techcamp.protospace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// エンティティと似ているが、トークンを渡したり、パスワードを宣言していなかったりする点が異なる
// 要は、安全にログインするために必要なファイル
public class LoginResponseDto {
  private String token;
  private Long id;
  private String email;
  private String username;
  private String position;
  private String affiliation;
}
