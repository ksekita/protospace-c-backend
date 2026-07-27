package in.techcamp.protospace.dto;

import lombok.Data;

@Data
public class UserDetailResponseDto {
    private Long id;
    private String name;
    private String email;
    private String position;
    private String affiliation;
}