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

  // 記事詳細を取得
  public PrototypeDetailResponseDto getPrototypeDetail(Long id,Long loggedInUserId) {
    PrototypeEntity prototype = prototypeRepository.findById(id);
    if (prototype == null) {
      throw new ResourceNotFoundException("プロトタイプが見つかりません");
    }

    UserEntity user = userRepository.selectById(prototype.getUserId());
    String name = (user != null) ? user.getName() : null;

    Long likeCount = likeMapper.countByPrototypeId(id);
    boolean isLiked = (loggedInUserId != null && loggedInUserId != 0L) && likeMapper.existsLike(loggedInUserId, id);

    return new PrototypeDetailResponseDto(
        prototype.getId(),
        prototype.getTitle(),
        prototype.getCatchCopy(),
        prototype.getConcept(),
        prototype.getImage(),
        prototype.getUserId(),
        name,
        likeCount,
        isLiked);
  }

  // 記事一覧を取得し、検索に対応
 public List<PrototypeListDto> getAllPrototypes(String keyword,String sort,Long loggedInUserId) {
  String order;
  if(sort.equals("oldest")){
    order="ASC";
  }
  else{
    order="DESC";
  }

    // キーワードが空の場合は全件取得、ある場合は検索メソッドを呼ぶ
    if (keyword == null || keyword.trim().isEmpty()) {
      return prototypeMapper.findAll(order,loggedInUserId);
    } else {
      return prototypeMapper.findByKeyword(keyword.trim(),order,loggedInUserId);
    }
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

  public List<UserPrototypeListDto> getPrototypesByUserId(Long userId, String sort,Long loggedInUserId) {
    
    // 1. sort パラメータに応じて並び順（ORDER）を決定する
    String order;
    if ("oldest".equals(sort)) {
      order = "ASC";
    } else {
      order = "DESC";
    }

    // 2. Mapperを呼び出して、取得したDTOのリストをそのまま返す
    return prototypeMapper.findByUserId(userId, order,loggedInUserId);
  }

  @Transactional
  public PrototypeLikeResponseDto toggleLike(Long prototypeId,Long userId){
    boolean isLiked=likeMapper.existsLike(userId,prototypeId );
    if(isLiked){
      likeMapper.delete(userId,prototypeId);
      isLiked=false;
    }
    else{
      //お気に入りの保存
      LikeEntity like=new LikeEntity();
      like.setUserId(userId);
      like.setPrototypeId(prototypeId);
      likeMapper.insert(like);
      isLiked=true;
    }

    Long currentLikeCount=likeMapper.countByPrototypeId(prototypeId);

    return new PrototypeLikeResponseDto(currentLikeCount,isLiked);

  }
}
