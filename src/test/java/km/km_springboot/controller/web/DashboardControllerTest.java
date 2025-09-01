package km.km_springboot.controller.web;

import km.km_springboot.entity.Role;
import km.km_springboot.entity.User;
import km.km_springboot.entity.UserStatus;
import km.km_springboot.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
    }

    @Test
    void 인증된_사용자는_대시보드_페이지를_볼_수_있다() throws Exception {
        // given
        User mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .fullName("테스트 사용자")
                .email("test@example.com")
                .status(UserStatus.ACTIVE)
                .role(new Role(1L, "USER", "일반 사용자", null))
                .build();
        given(authService.getCurrentUser()).willReturn(mockUser);

        // when & then
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", mockUser));
    }
}