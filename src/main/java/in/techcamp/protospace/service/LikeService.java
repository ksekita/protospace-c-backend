package in.techcamp.protospace.service;
import in.techcamp.protospace.entity.LikeEntity;
import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.repository.LikeRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

// 1. 送信された `prototype_id` と ログイン中の `user_id` を取得。
// 2. `likes` テーブルにレコードが存在するか確認。
//     - **存在しない場合 :** レコードを挿入（いいね登録）。
//     - **存在する場合 :** レコードを削除（いいね解除）。
// 3. 処理後の該当記事の **「総いいね数（`likeCount`）」** と **「ログインユーザーが現在いいね中か（`isLiked`）」** を取得して返す。

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeEntity like;

     // いいねの数を取得
  public PrototypeEntity countLikes(Long prototypeId){
    return likeRepository.countLikes(prototypeId);
  }

    //いいねがあるか確認
   public void existsLike(Long prototypeId){
    boolean isCheck= likeRepository.existsLike(prototypeId); 
    if(!isCheck){
        likeRepository.insert(like);
    }
    else{
        likeRepository.delete(like);
    }
  }


    


}
