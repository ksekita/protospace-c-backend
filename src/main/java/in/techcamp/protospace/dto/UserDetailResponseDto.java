package in.techcamp.protospace.dto;

import lombok.Data;

@Data
public class UserDetailResponseDto {
    private Long id;
    private String username;
    private String email;
    private String position;
    private String affiliation;
}