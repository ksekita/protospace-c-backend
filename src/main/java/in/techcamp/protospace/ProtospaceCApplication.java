package in.techcamp.protospace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class ProtospaceCApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProtospaceCApplication.class, args);
  }

  @Profile("local")
  @Bean
  public FlywayMigrationStrategy flywayMigrationStrategy() {
    return flyway -> {
      // flyway.repair();
      flyway.clean();

      // 2. 修復した状態で、通常通りマイグレーションを実行する
      flyway.migrate();
    };
  }
}
