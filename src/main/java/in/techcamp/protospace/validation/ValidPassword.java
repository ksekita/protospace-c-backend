package in.techcamp.protospace.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// アノテーション定義ファイル
// @ValidPasswordと書くだけで独自のパスワードチェックを呼び出せるようにするためのファイル
@Documented
// Constraintアノテーションで検証ファイルと結び付けている
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
  String message() default "不適切なパスワードです";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
