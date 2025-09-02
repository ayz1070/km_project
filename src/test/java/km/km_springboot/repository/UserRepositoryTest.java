package km.km_springboot.repository;

import km.km_springboot.entity.Role;
import km.km_springboot.entity.User;
import km.km_springboot.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role userRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        // 기본 역할 생성
        adminRole = Role.builder()
                .name("ADMIN")
                .description("관리자")
                .build();
        userRole = Role.builder()
                .name("USER")
                .description("일반 사용자")
                .build();
        
        entityManager.persist(adminRole);
        entityManager.persist(userRole);
        entityManager.flush();
    }

    @Test
    void 사용자명으로_사용자_찾기() {
        // given
        User user = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .fullName("테스트 사용자")
                .status(UserStatus.ACTIVE)
                .role(userRole)
                .build();
        
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getRole().getName()).isEqualTo("USER");
    }

    @Test
    void 존재하지_않는_사용자명으로_찾기() {
        // when
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void 이메일로_사용자_찾기() {
        // given
        User user = User.builder()
                .username("emailtest")
                .password("password123")
                .email("email@example.com")
                .fullName("이메일 테스트")
                .status(UserStatus.ACTIVE)
                .role(userRole)
                .build();
        
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> foundUser = userRepository.findByEmail("email@example.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("emailtest");
    }

    @Test
    void 사용자명_존재_여부_확인() {
        // given
        User user = User.builder()
                .username("existstest")
                .password("password123")
                .email("exists@example.com")
                .fullName("존재 테스트")
                .status(UserStatus.ACTIVE)
                .role(userRole)
                .build();
        
        entityManager.persist(user);
        entityManager.flush();

        // when & then
        assertThat(userRepository.existsByUsername("existstest")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistent")).isFalse();
    }

    @Test
    void 활성_사용자를_사용자명으로_찾기() {
        // given
        User activeUser = User.builder()
                .username("activeuser")
                .password("password123")
                .email("active@example.com")
                .fullName("활성 사용자")
                .status(UserStatus.ACTIVE)
                .role(adminRole)
                .build();
        
        User inactiveUser = User.builder()
                .username("inactiveuser")
                .password("password123")
                .email("inactive@example.com")
                .fullName("비활성 사용자")
                .status(UserStatus.INACTIVE)
                .role(userRole)
                .build();
        
        entityManager.persist(activeUser);
        entityManager.persist(inactiveUser);
        entityManager.flush();

        // when
        Optional<User> foundActive = userRepository.findActiveUserByUsername("activeuser");
        Optional<User> foundInactive = userRepository.findActiveUserByUsername("inactiveuser");

        // then
        assertThat(foundActive).isPresent();
        assertThat(foundActive.get().getRole().getName()).isEqualTo("ADMIN");
        assertThat(foundInactive).isEmpty();
    }
}
