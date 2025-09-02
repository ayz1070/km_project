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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestProfile
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
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
    void 사용자명으로_사용자_찾기() {
        // when
        Optional<User> foundUser = userService.findByUsername("testuser");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void 존재하지_않는_사용자명으로_찾기() {
        // when
        Optional<User> foundUser = userService.findByUsername("nonexistent");

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void 마지막_로그인_시간_업데이트() {
        // given
        LocalDateTime beforeUpdate = LocalDateTime.now();

        // when
        userService.updateLastLoginAt("testuser");

        // then
        User updatedUser = userRepository.findByUsername("testuser").get();
        assertThat(updatedUser.getLastLoginAt()).isNotNull();
        assertThat(updatedUser.getLastLoginAt()).isAfterOrEqualTo(beforeUpdate);
    }

    @Test
    void 존재하지_않는_사용자의_로그인_시간_업데이트_시도() {
        // when & then (예외가 발생하지 않아야 함)
        userService.updateLastLoginAt("nonexistent");
    }

    @Test
    void 마지막_로그인_시간_업데이트_후_다시_업데이트() {
        // given
        userService.updateLastLoginAt("testuser");
        User firstUpdate = userRepository.findByUsername("testuser").get();
        LocalDateTime firstLoginTime = firstUpdate.getLastLoginAt();

        // 시간 차이를 보장하기 위한 대기
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // when
        userService.updateLastLoginAt("testuser");

        // then
        User secondUpdate = userRepository.findByUsername("testuser").get();
        assertThat(secondUpdate.getLastLoginAt()).isAfter(firstLoginTime);
    }
}
