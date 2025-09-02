package km.km_springboot.repository;

import km.km_springboot.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void 역할명으로_역할_찾기() {
        // given
        Role role = Role.builder()
                .name("MANAGER")
                .description("관리자")
                .build();
        
        entityManager.persist(role);
        entityManager.flush();

        // when
        Optional<Role> foundRole = roleRepository.findByName("MANAGER");

        // then
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo("MANAGER");
        assertThat(foundRole.get().getDescription()).isEqualTo("관리자");
    }

    @Test
    void 존재하지_않는_역할명으로_찾기() {
        // when
        Optional<Role> foundRole = roleRepository.findByName("NONEXISTENT");

        // then
        assertThat(foundRole).isEmpty();
    }

    @Test
    void 역할_저장_및_조회() {
        // given
        Role role = Role.builder()
                .name("GUEST")
                .description("게스트 사용자")
                .build();

        // when
        Role savedRole = roleRepository.save(role);

        // then
        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo("GUEST");
        
        Optional<Role> foundRole = roleRepository.findById(savedRole.getId());
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getDescription()).isEqualTo("게스트 사용자");
    }
}