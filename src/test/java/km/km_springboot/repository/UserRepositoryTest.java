package km.km_springboot.repository;

import km.km_springboot.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 활성_상태의_사용자를_이름으로_조회한다() {
        // when
        Optional<User> foundUser = userRepository.findActiveUserByUsername("admin");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("admin");
        assertThat(foundUser.get().getStatus()).isEqualTo(km.km_springboot.entity.UserStatus.ACTIVE);
        assertThat(foundUser.get().getRole()).isNotNull();
        assertThat(foundUser.get().getRole().getName()).isEqualTo("ADMIN");
    }
}
