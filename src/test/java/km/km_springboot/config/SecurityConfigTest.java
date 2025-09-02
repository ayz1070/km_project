package km.km_springboot.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestProfile
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 인증되지_않은_사용자는_로그인_페이지로_리다이렉트된다() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }

    @Test
    void 로그인_페이지는_인증_없이_접근_가능하다() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk());
    }

    @Test
    void Swagger_UI는_인증_없이_접근_가능하다() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isOk());
    }

    @Test
    void API_문서는_인증_없이_접근_가능하다() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void API_로그인은_인증_없이_접근_가능하다() throws Exception {
        String loginJson = """
                {
                    "username": "testuser",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(loginJson))
                .andExpect(status().isUnauthorized()); // 인증은 실패하지만 접근은 가능
    }

    @Test
    @WithMockUser(roles = "USER")
    void 일반_사용자는_대시보드에_접근할_수_있다() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 관리자는_대시보드에_접근할_수_있다() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"));
    }

    @Test
    @WithMockUser
    void POST_요청시_CSRF_토큰이_필요하다() throws Exception {
        // CSRF 토큰 없이 POST 요청
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isForbidden());

        // CSRF 토큰과 함께 POST 요청
        mockMvc.perform(post("/auth/logout").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void API_요청은_CSRF_토큰이_필요하지_않다() throws Exception {
        String loginJson = """
                {
                    "username": "testuser",
                    "password": "password123"
                }
                """;

        // API 요청은 CSRF 토큰 없어도 403이 아닌 다른 상태코드 반환
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(loginJson))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized
    }

    @Test
    @WithMockUser
    void 인증된_사용자는_로그아웃_페이지에_접근할_수_있다() throws Exception {
        mockMvc.perform(get("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/logout"));
    }

    @Test
    void 인증되지_않은_사용자는_로그아웃_페이지에서_로그인으로_리다이렉트된다() throws Exception {
        mockMvc.perform(get("/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }
}