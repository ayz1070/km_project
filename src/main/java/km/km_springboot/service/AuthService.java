package km.km_springboot.service;

import km.km_springboot.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

// 인증 관련 편의 기능을 제공하는 서비스
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserService userService;
    
    // 현재 사용자가 인증되었는지 확인
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !(authentication instanceof AnonymousAuthenticationToken);
    }
    
    // 현재 로그인된 사용자의 이름을 반환
    public String getCurrentUsername() {
        if (isAuthenticated()) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return null;
    }
    
    // 현재 로그인된 사용자 엔티티를 반환
    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username != null) {
            return userService.findByUsername(username).orElse(null);
        }
        return null;
    }
    
    // 현재 로그인된 사용자의 마지막 로그인 시간을 업데이트
    public void updateLastLoginAt() {
        String username = getCurrentUsername();
        if (username != null) {
            userService.updateLastLoginAt(username);
        }
    }
}
