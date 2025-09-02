package km.km_springboot.integration;

import km.km_springboot.config.TestProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestProfile
class LoginIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void 로그인_페이지_접근_가능() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/auth/login", String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("로그인");
    }

    @Test
    void API_로그인_성공_플로우() {
        // given
        String loginJson = """
                {
                    "username": "admin",
                    "password": "admin123"
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(loginJson, headers);

        // when
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", request, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"success\":true");
        assertThat(response.getBody()).contains("\"username\":\"admin\"");
        assertThat(response.getBody()).contains("\"role\":\"ROLE_ADMIN\"");
    }

    @Test
    void API_로그인_실패_플로우() {
        // given
        String loginJson = """
                {
                
                    "username": "wronguser",
                    "password": "wrongpassword"
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(loginJson, headers);

        // when
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", request, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).contains("\"success\":false");
    }



    @Test
    void Swagger_UI_접근_가능() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/swagger-ui.html", String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void OpenAPI_문서_접근_가능() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("openapi");
    }

}