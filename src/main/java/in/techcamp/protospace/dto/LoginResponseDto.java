package in.techcamp.protospace.dto;

import java.util.List;
import lombok.Data;

@Data
//エンティティと似ているが、トークンを渡したり、パスワードを宣言していなかったりする点が異なる
//要は、安全にログインするために必要なファイル
public class LoginResponseDto {
  private String token;
  private Long id;
  private String email;
  private String username;
  private List<String> positions;
  private List<String> jobs;
}