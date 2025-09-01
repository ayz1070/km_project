package km.km_springboot.security;

import km.km_springboot.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// Spring Security가 사용할 사용자 상세 정보를 담는 클래스 (UserDetails 구현)
@Getter
public class CustomUserDetails implements UserDetails {
    
    private final User user; // 실제 사용자 엔티티
    
    public CustomUserDetails(User user) {
        this.user = user;
    }
    
    // 사용자의 권한 목록을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())
        );
    }
    
    // 사용자의 비밀번호를 반환
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    // 사용자의 이름을 반환
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    // 계정 만료 여부를 반환 (항상 true)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    // 계정 잠금 여부를 반환
    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != km.km_springboot.entity.UserStatus.LOCKED;
    }
    
    // 자격 증명(비밀번호) 만료 여부를 반환 (항상 true)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    // 계정 활성 여부를 반환
    @Override
    public boolean isEnabled() {
        return user.getStatus() == km.km_springboot.entity.UserStatus.ACTIVE;
    }

    // 사용자의 전체 이름을 반환 (Thymeleaf 등에서 사용)
    public String getFullName() {
        return user.getFullName();
    }
}
