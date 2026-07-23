package in.techcamp.protospace.form;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class PrototypeForm {
    private String title;
    private String catchCopy;
    private String concept;
    private MultipartFile image;
}