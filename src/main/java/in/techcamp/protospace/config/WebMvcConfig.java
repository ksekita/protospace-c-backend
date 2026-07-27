package in.techcamp.protospace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // バックエンドのルート直下にある「uploads」フォルダの中身を返す設定
    registry.addResourceHandler("/images/**").addResourceLocations("file:uploads/");
  }
}
