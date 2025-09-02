package km.km_springboot.service;

import km.km_springboot.config.TestProfile;
import km.km_springboot.entity.Role;
import km.km_springboot.entity.User;
import km.km_springboot.entity.UserStatus;
import km.km_springboot.repository.RoleRepository;
import km.km_springboot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // No @BeforeEach or @AfterEach for data setup/teardown

    private User createAndSaveUniqueUser(String usernamePrefix) {
        String uniqueUsername = usernamePrefix + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = uniqueUsername + "@example.com";

        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
            Role newRole = Role.builder()
                    .name("USER")
                    .description("일반 사용자")
                    .build();
            return roleRepository.save(newRole);
        });

        User user = User.builder()
                .username(uniqueUsername)
                .password("password123")
                .email(uniqueEmail)
                .fullName("테스트 사용자")
                .status(UserStatus.ACTIVE)
                .role(userRole)
                .build();
        return userRepository.save(user);
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
        User user = createAndSaveUniqueUser("authTestUser");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), "password123", 
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
        User user = createAndSaveUniqueUser("authTestUser");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), "password123", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        String username = authService.getCurrentUsername();

        // then
        assertThat(username).isEqualTo(user.getUsername());
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
        User user = createAndSaveUniqueUser("authTestUser");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), "password123", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        User currentUser = authService.getCurrentUser();

        // then
        assertThat(currentUser).isNotNull();
        assertThat(currentUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(currentUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void 현재_인증된_사용자의_마지막_로그인_시간_업데이트() {
        // given
        User user = createAndSaveUniqueUser("authTestUser");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), "password123", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        LocalDateTime beforeUpdate = LocalDateTime.now();

        // when
        authService.updateLastLoginAt();

        // then
        User updatedUser = userRepository.findByUsername(user.getUsername()).get();
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
        User user = createAndSaveUniqueUser("authTestUser");
        LocalDateTime beforeUpdate = LocalDateTime.now();

        // when
        authService.updateLastLoginAt(user.getUsername());

        // then
        User updatedUser = userRepository.findByUsername(user.getUsername()).get();
        assertThat(updatedUser.getLastLoginAt()).isNotNull();
        assertThat(updatedUser.getLastLoginAt()).isAfterOrEqualTo(beforeUpdate);
    }
}