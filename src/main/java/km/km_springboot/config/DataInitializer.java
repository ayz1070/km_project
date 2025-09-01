package km.km_springboot.config;

import km.km_springboot.entity.User;
import km.km_springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// 애플리케이션 시작 시 데이터를 초기화하는 클래스
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 애플리케이션 시작 시 실행되어 'admin' 사용자의 비밀번호를 암호화합니다.
    @Override
    public void run(String... args) throws Exception {
        userRepository.findByUsername("admin").ifPresent(user -> {
            if (user.getPassword().equals("admin123")) {
                user.setPassword(passwordEncoder.encode("admin123"));
                userRepository.save(user);
            }
        });
    }
}
