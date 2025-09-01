package km.km_springboot.config;

import km.km_springboot.entity.Role;
import km.km_springboot.entity.User;
import km.km_springboot.entity.UserStatus;
import km.km_springboot.repository.RoleRepository;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // 애플리케이션 시작 시 실행되어 기본 데이터를 생성하거나 업데이트합니다.
    @Override
    public void run(String... args) throws Exception {
        
        // 1. 기본 역할 생성
        createRoleIfNotExists("ADMIN", "시스템 관리자");
        createRoleIfNotExists("USER", "일반 사용자");
        
        // 2. 기본 사용자 생성
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Role userRole = roleRepository.findByName("USER").orElseThrow();
        
        createUserIfNotExists("admin", "admin123", "admin@example.com", "관리자", adminRole);
        createUserIfNotExists("test", "test123", "test@example.com", "테스트 사용자", userRole);
        createUserIfNotExists("km", "km123", "km@koreamarkers.com", "KM 사용자", userRole);
    }
    
    private void createRoleIfNotExists(String name, String description) {
        if (!roleRepository.findByName(name).isPresent()) {
            Role role = Role.builder()
                    .name(name)
                    .description(description)
                    .build();
            roleRepository.save(role);
            System.out.println("역할 생성: " + name);
        }
    }
    
    private void createUserIfNotExists(String username, String password, String email, String fullName, Role role) {
        if (!userRepository.findByUsername(username).isPresent()) {
            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullName(fullName)
                    .status(UserStatus.ACTIVE)
                    .role(role)
                    .build();
            userRepository.save(user);
            System.out.println("사용자 생성: " + username);
        } else {
            // 기존 사용자의 패스워드가 암호화되지 않은 경우 암호화
            userRepository.findByUsername(username).ifPresent(user -> {
                if (user.getPassword().equals(password)) {
                    user.setPassword(passwordEncoder.encode(password));
                    userRepository.save(user);
                    System.out.println("사용자 패스워드 암호화: " + username);
                }
            });
        }
    }
}
