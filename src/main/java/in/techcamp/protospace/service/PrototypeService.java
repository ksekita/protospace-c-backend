package in.techcamp.protospace.service;

import in.techcamp.protospace.dto.PrototypeDetailResponseDto;
import in.techcamp.protospace.dto.PrototypeListDto;
import in.techcamp.protospace.dto.UserPrototypeListDto;
import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.exception.ResourceNotFoundException;
import in.techcamp.protospace.form.PrototypeForm;
import in.techcamp.protospace.mapper.PrototypeMapper;
import in.techcamp.protospace.repository.PrototypeRepository;
import in.techcamp.protospace.repository.UserRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PrototypeService {

  private final PrototypeMapper prototypeMapper;
  private final PrototypeRepository prototypeRepository;
  private final UserRepository userRepository;

  // 記事詳細を取得
  public PrototypeDetailResponseDto getPrototypeDetail(Long id) {
    PrototypeEntity prototype = prototypeRepository.findById(id);
    if (prototype == null) {
      throw new ResourceNotFoundException("プロトタイプが見つかりません");
    }

    UserEntity user = userRepository.selectById(prototype.getUserId());
    String name = (user != null) ? user.getName() : null;

    return new PrototypeDetailResponseDto(
        prototype.getId(),
        prototype.getTitle(),
        prototype.getCatchCopy(),
        prototype.getConcept(),
        prototype.getImage(),
        prototype.getUserId(),
        name);
  }

  // 記事一覧を取得
  public List<PrototypeListDto> getAllPrototypes() {
  
    List<PrototypeListDto> dto = prototypeMapper.findAll();

    return dto;
  }

  // 記事新規作成
  public void createPrototype(PrototypeForm form, Long userId) throws Exception {

    // 画像の保存処理
    MultipartFile imageFile = form.getImage();
    String savedFileName = null;

    if (imageFile != null && !imageFile.isEmpty()) {

      String originalName = imageFile.getOriginalFilename();

      if (originalName != null && originalName.contains(".")) {
        String extension = originalName.substring(originalName.lastIndexOf("."));
        savedFileName = UUID.randomUUID().toString() + extension;

        Path uploadPath = Paths.get("uploads/").toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
          Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(savedFileName);
        imageFile.transferTo(filePath);
      }
    } else {
      throw new IllegalArgumentException("画像ファイルが選択されていません");
    }

    // DB保存
    PrototypeEntity entity = new PrototypeEntity();
    entity.setTitle(form.getTitle());
    entity.setCatchCopy(form.getCatchCopy());
    entity.setConcept(form.getConcept());
    entity.setImage(savedFileName);
    entity.setUserId(userId);

    prototypeMapper.insert(entity);
  }

  // 記事の更新
  public void updatePrototype(Long id, PrototypeForm form, Long userId) throws Exception {

    // 編集に必要なプロトタイプをエンティティから引っ張ってくる
    PrototypeEntity existingPrototype = prototypeMapper.findById(id);

    // 例外処理
    if (existingPrototype == null) {
      throw new IllegalArgumentException("指定されたプロトタイプが見つかりません");
    }

    if (!existingPrototype.getUserId().equals(userId)) {
      throw new Exception("他のユーザーの投稿を編集する権限がありません");
    }

    MultipartFile imageFile = form.getImage();
    // 古い画像をキープする
    String savedFileName = existingPrototype.getImage();

    // 新しい画像が送られてきた場合だけ上書きする
    if (imageFile != null && !imageFile.isEmpty()) {
      String originalName = imageFile.getOriginalFilename();
      if (originalName != null && originalName.contains(".")) {
        String extension = originalName.substring(originalName.lastIndexOf("."));
        savedFileName = UUID.randomUUID().toString() + extension;

        Path uploadPath = Paths.get("uploads/").toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
          Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(savedFileName);
        imageFile.transferTo(filePath);
      }
    }

    // 新しい内容をEntityに詰める
    existingPrototype.setTitle(form.getTitle());
    existingPrototype.setCatchCopy(form.getCatchCopy());
    existingPrototype.setConcept(form.getConcept());
    existingPrototype.setImage(savedFileName);

    // Mapperでデータベース上書き
    prototypeMapper.update(existingPrototype);
  }

  // 記事の削除
  public void deletePrototype(Long id, Long userId) throws Exception {
    PrototypeEntity existingPrototype = prototypeMapper.findById(id);
    if (existingPrototype == null) {
      throw new IllegalArgumentException("指定されたプロトタイプが見つかりません");
    }
    if (!existingPrototype.getUserId().equals(userId)) {
      throw new SecurityException("他のユーザーの投稿を削除する権限がありません");
    }
    String savedFileName = existingPrototype.getImage();
    if (savedFileName != null && !savedFileName.isEmpty()) {
      Path filePath = Paths.get("uploads/").resolve(savedFileName).toAbsolutePath().normalize();
      Files.deleteIfExists(filePath);
    }
    prototypeMapper.delete(id);
  }

  public List<UserPrototypeListDto> getPrototypesByUserId(Long userId) {
    // Mapperを使ってデータベースから取得
    List<PrototypeEntity> entities = prototypeMapper.findByUserId(userId);

    // EntityのリストをDTOのリストに変換（詰め替え）
    return entities.stream()
        .map(
            entity -> {
              UserPrototypeListDto dto = new UserPrototypeListDto();
              dto.setId(entity.getId());
              dto.setTitle(entity.getTitle());
              dto.setCatchCopy(entity.getCatchCopy());
              dto.setImage(entity.getImage());
              return dto;
            })
        .collect(Collectors.toList());
  }
}
