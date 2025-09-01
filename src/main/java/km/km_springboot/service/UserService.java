package km.km_springboot.service;

import km.km_springboot.entity.User;
import km.km_springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// 사용자 관련 비즈니스 로직을 처리하는 서비스
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    
    // 사용자명으로 사용자를 찾는 메소드
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // 사용자의 마지막 로그인 시간을 업데이트하는 메소드
    @Transactional
    public void updateLastLoginAt(String username) {
        userRepository.findByUsername(username)
                .ifPresent(User::updateLastLoginAt);
    }
}
