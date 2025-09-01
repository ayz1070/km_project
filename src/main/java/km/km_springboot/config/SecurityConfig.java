package km.km_springboot.config;

import km.km_springboot.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Spring Security 설정 클래스
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CustomUserDetailsService userDetailsService;
    
    // 비밀번호 암호화를 위한 PasswordEncoder 빈 등록 (BCrypt 사용)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // AuthenticationManager 빈 등록 (API 로그인에서 사용)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    // HTTP 보안 설정을 구성하는 SecurityFilterChain 빈 등록
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 요청별 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll() // 정적 자원 및 H2 콘솔 허용
                .requestMatchers("/auth/login", "/auth/signup").permitAll() // 로그인, 회원가입 페이지 허용
                .requestMatchers("/api/auth/login").permitAll() // API 로그인 허용
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger UI 허용
                .requestMatchers("/admin/**").hasRole("ADMIN") // '/admin/**' 경로는 'ADMIN' 역할 필요
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            )
            // 폼 기반 로그인 설정
            .formLogin(form -> form
                .loginPage("/auth/login") // 커스텀 로그인 페이지
                .loginProcessingUrl("/auth/login") // 로그인 처리 URL
                .defaultSuccessUrl("/dashboard", true) // 로그인 성공 시 이동할 URL
                .failureUrl("/auth/login?error=true") // 로그인 실패 시 이동할 URL
                .permitAll()
            )
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/auth/logout") // 로그아웃 처리 URL
                .logoutSuccessUrl("/auth/login?logout=true") // 로그아웃 성공 시 이동할 URL
                .invalidateHttpSession(true) // 세션 무효화
                .deleteCookies("JSESSIONID") // 쿠키 삭제
            )
            .userDetailsService(userDetailsService) // 커스텀 UserDetailsService 사용
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**") // H2 콘솔 및 API CSRF 보호 예외
            )
            .headers(headers -> headers
                .frameOptions().sameOrigin() // H2 콘솔 frameOptions 설정
            );
            
        return http.build();
    }
}
