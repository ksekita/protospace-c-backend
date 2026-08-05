package in.techcamp.protospace.service;

import in.techcamp.protospace.dto.PrototypeDetailResponseDto;
import in.techcamp.protospace.dto.PrototypeLikeResponseDto;
import in.techcamp.protospace.dto.PrototypeListDto;
import in.techcamp.protospace.dto.UserPrototypeListDto;
import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.entity.LikeEntity;
import in.techcamp.protospace.exception.ResourceNotFoundException;
import in.techcamp.protospace.form.PrototypeForm;
import in.techcamp.protospace.mapper.LikeMapper;
import in.techcamp.protospace.mapper.PrototypeMapper;
import in.techcamp.protospace.repository.PrototypeRepository;
import in.techcamp.protospace.repository.UserRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PrototypeService {

  private final PrototypeMapper prototypeMapper;
  private final PrototypeRepository prototypeRepository;
  private final UserRepository userRepository;
  private final LikeMapper likeMapper;

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

  public List<PrototypeListDto> getAllPrototypes(String keyword, String sort) {
    String order;
    if(sort.equals("oldest")){
      order="ASC";
    } else {
      order="DESC";
    }

    if (keyword == null || keyword.trim().isEmpty()) {
      return prototypeMapper.findAll(order);
    } else {
      return prototypeMapper.findByKeyword(keyword.trim(), order);
    }
  }

  public void createPrototype(PrototypeForm form, Long userId) throws Exception {
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

    PrototypeEntity entity = new PrototypeEntity();
    entity.setTitle(form.getTitle());
    entity.setCatchCopy(form.getCatchCopy());
    entity.setConcept(form.getConcept());
    entity.setImage(savedFileName);
    entity.setUserId(userId);
    prototypeMapper.insert(entity);
  }

  public void updatePrototype(Long id, PrototypeForm form, Long userId) throws Exception {
    PrototypeEntity existingPrototype = prototypeMapper.findById(id);
    if (existingPrototype == null) {
      throw new IllegalArgumentException("指定されたプロトタイプが見つかりません");
    }
    if (!existingPrototype.getUserId().equals(userId)) {
      throw new Exception("他のユーザーの投稿を編集する権限がありません");
    }
    MultipartFile imageFile = form.getImage();
    String savedFileName = existingPrototype.getImage();
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
    existingPrototype.setTitle(form.getTitle());
    existingPrototype.setCatchCopy(form.getCatchCopy());
    existingPrototype.setConcept(form.getConcept());
    existingPrototype.setImage(savedFileName);
    prototypeMapper.update(existingPrototype);
  }

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

  public List<UserPrototypeListDto> getPrototypesByUserId(Long userId, String sort) {
    String order;
    if ("oldest".equals(sort)) {
      order = "ASC";
    } else {
      order = "DESC";
    }
    return prototypeMapper.findByUserId(userId, order);
  }

  // いいねの状態を取得する
  public PrototypeLikeResponseDto getLikeStatus(Long prototypeId, Long userId) {
    Long currentLikeCount = likeMapper.countByPrototypeId(prototypeId);
    boolean isLiked = (userId != null && userId != 0L) && likeMapper.existsLike(userId, prototypeId);
    return new PrototypeLikeResponseDto(currentLikeCount, isLiked);
  }

  @Transactional
  public PrototypeLikeResponseDto toggleLike(Long prototypeId, Long userId) {
    boolean isLiked = likeMapper.existsLike(userId, prototypeId);
    if (isLiked) {
      likeMapper.delete(userId, prototypeId);
      isLiked = false;
    } else {
      LikeEntity like = new LikeEntity();
      like.setUserId(userId);
      like.setPrototypeId(prototypeId);
      likeMapper.insert(like);
      isLiked = true;
    }
    Long currentLikeCount = likeMapper.countByPrototypeId(prototypeId);
    return new PrototypeLikeResponseDto(currentLikeCount, isLiked);
  }
}