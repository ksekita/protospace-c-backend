package in.techcamp.protospace.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import in.techcamp.protospace.dto.PrototypeDetailResponseDto;
import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.repository.PrototypeRepository;
import in.techcamp.protospace.repository.UserRepository;
import in.techcamp.protospace.exception.ResourceNotFoundException;
import in.techcamp.protospace.form.PrototypeForm;
import in.techcamp.protospace.mapper.PrototypeMapper;
import lombok.RequiredArgsConstructor;
import java.util.List;

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

     public void createPrototype(PrototypeForm form, Long userId) throws Exception {
        
     // 画像の保存処理
    MultipartFile imageFile = form.getImage();
    String savedFileName = null;

    if(imageFile != null && !imageFile.isEmpty()){
        
    String originalName = imageFile.getOriginalFilename();

    if(originalName != null && originalName.contains(".")){
    String extension = originalName.substring(originalName.lastIndexOf("."));
    savedFileName = UUID.randomUUID().toString() + extension;

    Path uploadPath = Paths.get("uploads/").toAbsolutePath().normalize();
    if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
        }

    Path filePath = uploadPath.resolve(savedFileName);
    imageFile.transferTo(filePath);
    // ここまで
        }
      } else{
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

    public List<PrototypeEntity> getAllPrototypes(){
        return prototypeMapper.findAll();
    }
}

