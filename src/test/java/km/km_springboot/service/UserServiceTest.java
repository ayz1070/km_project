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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
    void 사용자명으로_사용자_찾기() {
        // given
        User user = createAndSaveUniqueUser("userTestUser");

        // when
        Optional<User> foundUser = userService.findByUsername(user.getUsername());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void 존재하지_않는_사용자명으로_찾기() {
        // when
        Optional<User> foundUser = userService.findByUsername("nonexistent" + UUID.randomUUID().toString());

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void 마지막_로그인_시간_업데이트() {
        // given
        User user = createAndSaveUniqueUser("userTestUser");
        LocalDateTime beforeUpdate = LocalDateTime.now();

        // when
        userService.updateLastLoginAt(user.getUsername());

        // then
        User updatedUser = userRepository.findByUsername(user.getUsername()).get();
        assertThat(updatedUser.getLastLoginAt()).isNotNull();
        assertThat(updatedUser.getLastLoginAt()).isAfterOrEqualTo(beforeUpdate);
    }

    @Test
    void 존재하지_않는_사용자의_로그인_시간_업데이트_시도() {
        // when & then (예외가 발생하지 않아야 함)
        userService.updateLastLoginAt("nonexistent" + UUID.randomUUID().toString());
    }

    @Test
    void 마지막_로그인_시간_업데이트_후_다시_업데이트() {
        // given
        User user = createAndSaveUniqueUser("userTestUser");
        userService.updateLastLoginAt(user.getUsername());
        User firstUpdate = userRepository.findByUsername(user.getUsername()).get();
        LocalDateTime firstLoginTime = firstUpdate.getLastLoginAt();

        // 시간 차이를 보장하기 위한 대기
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // when
        userService.updateLastLoginAt(user.getUsername());

        // then
        User secondUpdate = userRepository.findByUsername(user.getUsername()).get();
        assertThat(secondUpdate.getLastLoginAt()).isAfter(firstLoginTime);
    }
}