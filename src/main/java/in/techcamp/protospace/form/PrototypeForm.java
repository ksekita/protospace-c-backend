package in.techcamp.protospace.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PrototypeForm {
  private String title;
  private String catchCopy;
  private String concept;
  private MultipartFile image;
}
