package km.km_springboot.service;

import km.km_springboot.config.TestProfile;
import km.km_springboot.entity.Role;
import km.km_springboot.entity.User;
import km.km_springboot.entity.UserStatus;
import km.km_springboot.repository.RoleRepository;
import km.km_springboot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestProfile
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        // 테스트용 역할 생성
        Role userRole = Role.builder()
                .name("USER")
                .description("일반 사용자")
                .build();
        roleRepository.save(userRole);

        // 테스트용 사용자 생성
        testUser = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .fullName("테스트 사용자")
                .status(UserStatus.ACTIVE)
                .role(userRole)
                .build();
        userRepository.save(testUser);
    }

    @Test
    void 인증되지_않은_상태에서_isAuthenticated_호출() {
        // when
        boolean isAuthenticated = authService.isAuthenticated();

        // then
        assertThat(isAuthenticated).isFalse();
    }

    @Test
    void 인증된_상태에서_isAuthenticated_호출() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser", "password123", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        boolean isAuthenticated = authService.isAuthenticated();

        // then
        assertThat(isAuthenticated).isTrue();
    }

    @Test
    void 인증되지_않은_상태에서_getCurrentUsername_호출() {
        // when
        String username = authService.getCurrentUsername();

        // then
        assertThat(username).isNull();
    }

    @Test
    void 인증된_상태에서_getCurrentUsername_호출() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser", "password123", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        String username = authService.getCurrentUsername();

        // then
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void 인증되지_않은_상태에서_getCurrentUser_호출() {
        // when
        User currentUser = authService.getCurrentUser();

        // then
        assertThat(currentUser).isNull();
    }

    @Test
    void 인증된_상태에서_getCurrentUser_호출() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser", "password123", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        User currentUser = authService.getCurrentUser();

        // then
        assertThat(currentUser).isNotNull();
        assertThat(currentUser.getUsername()).isEqualTo("testuser");
        assertThat(currentUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void 현재_인증된_사용자의_마지막_로그인_시간_업데이트() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser", "password123", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        LocalDateTime beforeUpdate = LocalDateTime.now();

        // when
        authService.updateLastLoginAt();

        // then
        User updatedUser = userRepository.findByUsername("testuser").get();
        assertThat(updatedUser.getLastLoginAt()).isNotNull();
        assertThat(updatedUser.getLastLoginAt()).isAfterOrEqualTo(beforeUpdate);
    }

    @Test
    void 인증되지_않은_상태에서_마지막_로그인_시간_업데이트() {
        // when & then (예외가 발생하지 않아야 함)
        authService.updateLastLoginAt();
    }

    @Test
    void 특정_사용자명으로_마지막_로그인_시간_업데이트() {
        // given
        LocalDateTime beforeUpdate = LocalDateTime.now();

        // when
        authService.updateLastLoginAt("testuser");

        // then
        User updatedUser = userRepository.findByUsername("testuser").get();
        assertThat(updatedUser.getLastLoginAt()).isNotNull();
        assertThat(updatedUser.getLastLoginAt()).isAfterOrEqualTo(beforeUpdate);
    }
}