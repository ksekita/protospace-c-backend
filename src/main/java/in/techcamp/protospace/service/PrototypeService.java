package in.techcamp.protospace.service;

import in.techcamp.protospace.dto.PrototypeDetailResponseDto;
import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.exception.ResourceNotFoundException;
import in.techcamp.protospace.repository.PrototypeRepository;
import in.techcamp.protospace.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PrototypeService {

  private final PrototypeRepository prototypeRepository;
  private final UserRepository userRepository;

  public PrototypeService(
      PrototypeRepository prototypeRepository, UserRepository userRepository) {
    this.prototypeRepository = prototypeRepository;
    this.userRepository = userRepository;
  }

  // 記事詳細を取得
  public PrototypeDetailResponseDto getPrototypeDetail(Long id) {
    PrototypeEntity prototype = prototypeRepository.findById(id);
    if (prototype == null) {
      throw new ResourceNotFoundException("プロトタイプが見つかりません");
    }

    UserEntity user = userRepository.selectById(prototype.getUserId());
    String userName = (user != null) ? user.getUsername() : null;

    return new PrototypeDetailResponseDto(
        prototype.getId(),
        prototype.getTitle(),
        prototype.getCatchCopy(),
        prototype.getConcept(),
        prototype.getImage(),
        prototype.getUserId(),
        userName);
  }
}