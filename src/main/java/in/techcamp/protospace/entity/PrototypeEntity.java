package in.techcamp.protospace.entity;

import lombok.Data;

@Data
public class PrototypeEntity {
    private Long id;
    private String title;
    private String catchCopy;
    private String concept;
    private String image;
    private Long userId;
}
