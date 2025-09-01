package km.km_springboot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KM Spring Boot API")
                        .version("1.0.0")
                        .description("Korea Markers Spring Boot 애플리케이션의 REST API 문서")
                        .contact(new Contact()
                                .name("Korea Markers")
                                .email("admin@koreamarkers.com")
                                .url("https://www.koreamarkers.com"))
                        .license(new License()
                                .name("Korea Markers License")
                                .url("https://www.koreamarkers.com")));
    }
}