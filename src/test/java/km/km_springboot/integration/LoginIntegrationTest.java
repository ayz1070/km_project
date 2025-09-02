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
    void API_로그아웃_플로우() {
        // when
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/logout", null, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"success\":true");
        assertThat(response.getBody()).contains("로그아웃이 성공하였습니다");
    }

    @Test
    void 인증되지_않은_사용자의_대시보드_접근_시_로그인_페이지로_리다이렉트() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/dashboard", String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().toString()).contains("/auth/login");
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

    @Test
    void 폼_기반_로그인_성공_후_대시보드_접근() {
        // Step 1: 로그인 페이지에서 CSRF 토큰 추출
        ResponseEntity<String> loginPageResponse = restTemplate.getForEntity("/auth/login", String.class);
        String csrfToken = extractCsrfToken(loginPageResponse.getBody());
        String sessionCookie = extractSessionCookie(loginPageResponse);

        // Step 2: 로그인 폼 제출
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (sessionCookie != null) {
            headers.add("Cookie", sessionCookie);
        }

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", "admin");
        formData.add("password", "admin123");
        if (csrfToken != null) {
            formData.add("_csrf", csrfToken);
        }

        HttpEntity<MultiValueMap<String, String>> loginRequest = new HttpEntity<>(formData, headers);

        // when
        ResponseEntity<String> loginResponse = restTemplate.exchange(
                "/auth/login", HttpMethod.POST, loginRequest, String.class);

        // then
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(loginResponse.getHeaders().getLocation().toString()).contains("/dashboard");
    }

    private String extractCsrfToken(String html) {
        if (html == null) return null;
        Pattern pattern = Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(html);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractSessionCookie(ResponseEntity<String> response) {
        return response.getHeaders().getFirst("Set-Cookie");
    }
}