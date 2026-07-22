package in.techcamp.protospace.controller;

import in.techcamp.protospace.dto.PrototypeDetailResponseDto;
import in.techcamp.protospace.service.PrototypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/prototypes")
public class PrototypeController {

  private final PrototypeService prototypeService;

  public PrototypeController(PrototypeService prototypeService) {
    this.prototypeService = prototypeService;
  }

// 記事詳細ページに遷移
  @GetMapping("/{id}")
  public ResponseEntity<PrototypeDetailResponseDto> getPrototypeDetail(
      @PathVariable("id") Long id) {
    PrototypeDetailResponseDto response = prototypeService.getPrototypeDetail(id);
    return ResponseEntity.ok(response);
  }
}