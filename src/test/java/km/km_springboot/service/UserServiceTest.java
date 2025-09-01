package km.km_springboot.service;

import km.km_springboot.entity.User;
import km.km_springboot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 마지막_로그인_시간을_업데이트한다() {
        // given
        User user = userRepository.findByUsername("admin").get();
        LocalDateTime initialLoginTime = user.getLastLoginAt();

        // when
        userService.updateLastLoginAt("admin");

        // then
        User updatedUser = userRepository.findByUsername("admin").get();
        if (initialLoginTime != null) {
            assertThat(updatedUser.getLastLoginAt()).isAfter(initialLoginTime);
        } else {
            assertThat(updatedUser.getLastLoginAt()).isNotNull();
        }
    }
}
