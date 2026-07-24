package in.techcamp.protospace.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.form.PrototypeForm;
import in.techcamp.protospace.mapper.PrototypeMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrototypeService {
    private final PrototypeMapper prototypeMapper;

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

    public void updatePrototype(Long id, PrototypeForm form, Long userId) throws Exception {

        // 編集に必要なプロトタイプをエンティティから引っ張ってくる
        PrototypeEntity existingPrototype = prototypeMapper.findById(id);

        // 例外処理
        if(existingPrototype == null){
            throw new IllegalArgumentException("指定されたプロトタイプが見つかりません");
        }

        if(!existingPrototype.getUserId().equals(userId)) {
            throw new Exception("他のユーザーの投稿を編集する権限がありません");
        }

        MultipartFile imageFile = form.getImage();
        // 古い画像をキープする
        String savedFileName = existingPrototype.getImage();

        // 新しい画像が送られてきた場合だけ上書きする
        if(imageFile != null && !imageFile.isEmpty()){
            String originalName = imageFile.getOriginalFilename();
                if(originalName != null && originalName.contains(".")) {
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

            // 新しい内容をEntitynに詰める 
            existingPrototype.setTitle(form.getTitle());
            existingPrototype.setCatchCopy(form.getCatchCopy());
            existingPrototype.setConcept(form.getConcept());
            existingPrototype.setImage(savedFileName);

            // Mapperでデータベース上書き
            prototypeMapper.update(existingPrototype);
        }
    }


