package in.techcamp.protospace.validation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.passay.DictionaryRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RepeatCharacterRegexRule;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.RuleResultDetail;
import org.passay.WhitespaceRule;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.WordLists;
import org.passay.dictionary.sort.ArraysSort;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

// ユーザーの作成したパスワードが安全の基準を満たしているかをチェックするクラス
@Slf4j
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

  private DictionaryRule dictionaryRule;

  private  PasswordValidator validator;

  // validatorの初期化処理
  @Override
  public void initialize(ValidPassword constraintAnnotation) {

    List<Rule> rules = new ArrayList<>();
    rules.add(new LengthRule(6, 64)); //6文字以上64文字以内
    rules.add(new WhitespaceRule());//スペース禁止
    rules.add(new RepeatCharacterRegexRule(3));//同じ文字の連続(3回以上)禁止


    try (
      // 危険なパスワード一覧を読み込む
      InputStream is = getClass().getClassLoader().getResourceAsStream("passwords.txt")
    ) {
      if (is == null) {
        throw new FileNotFoundException("passwords.txt が resources 直下に見つかりません。");
      }

      Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

      WordListDictionary wordListDictionary = new WordListDictionary(
          WordLists.createFromReader(
              new Reader[] { reader }, 
              false,
              new ArraysSort()
          ));

      this.dictionaryRule = new DictionaryRule(wordListDictionary);

    } catch (FileNotFoundException e) {
      log.warn("【警告】パスワードリストが存在しません。辞書チェックをスキップします: {}", e.getMessage());
      this.dictionaryRule = null;
    } catch (IOException e) {
      log.error("【エラー】パスワード自作リストの読み込み中に予期せぬエラーが発生しました。", e);
      this.dictionaryRule = null;
    }
    if (this.dictionaryRule != null) {
      rules.add(this.dictionaryRule);
    }
    this.validator=new PasswordValidator(rules);
  }

  // 検証処理
  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    if (password == null) return false;

    RuleResult result = this.validator.validate(new PasswordData(password));

    if (result.isValid()) return true;

    context.disableDefaultConstraintViolation();
    List<String> errorMessages = new ArrayList<>();

    for (RuleResultDetail detail : result.getDetails()) {
      switch (detail.getErrorCode()) {
        case "TOO_SHORT" -> errorMessages.add("パスワードは6文字以上で入力してください");
        case "ILLEGAL_WORD" -> errorMessages.add("簡単すぎる、またはよく使われるパスワードは指定できません");
        case "ILLEGAL_REPEATED_CHARS" -> errorMessages.add("同じ文字を連続して使用することはできません");
        case "ILLEGAL_WHITESPACE" -> errorMessages.add("パスワードに空白文字を含めることはできません");
        default -> errorMessages.add("セキュリティ基準を満たさないパスワードです");
      }
    }

    context.buildConstraintViolationWithTemplate(String.join("。 ", errorMessages)).addConstraintViolation();
    return false;
  }
}