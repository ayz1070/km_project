# Claude Code 개발 지침서

## 📋 프로젝트 개요
- **목표**: Spring Boot + Bootstrap 조합으로 AI 활용한 빠른 구현
- **기간**: 단기 프로젝트 (빠른 개발 우선)
- **핵심 기능**: 로그인/로그아웃 시스템 + RDBMS 연동

## 🎯 개발 원칙

### 1. AI 개발 전략
- **속도 우선**: 완벽한 코드보다 빠른 구현 우선
- **AI 도구 최대 활용**: Claude Code로 자동화 가능한 모든 작업 활용
- **반복 작업 자동화**: 보일러플레이트 코드 자동 생성
- **검증 후 확장**: 기본 기능 완성 후 점진적 개선

### 2. 코딩 스타일 & 컨벤션

#### Java 코딩 스타일
```java
// ✅ 좋은 예시
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
```

#### 네이밍 규칙
- **클래스**: PascalCase (`UserService`, `AuthController`)
- **메서드/변수**: camelCase (`findUser`, `isAuthenticated`)
- **상수**: UPPER_SNAKE_CASE (`MAX_LOGIN_ATTEMPTS`)
- **패키지**: lowercase.dot.notation (`km.auth.service`)

#### 어노테이션 순서
```java
// 클래스 레벨
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User { }

// 메서드 레벨
@Override
@Transactional(readOnly = true)
@GetMapping("/users/{id}")
public User findById(@PathVariable Long id) { }
```

### 3. Spring Boot 아키텍처 패턴

#### 계층 구조
```
km.km_springboot
├── config/          # 설정 클래스
├── controller/      # REST API 컨트롤러
├── service/         # 비즈니스 로직
├── repository/      # 데이터 액세스
├── entity/          # JPA 엔티티
├── dto/             # 데이터 전송 객체
├── security/        # 보안 설정
└── web/             # 웹 컨트롤러 (Thymeleaf)
```

#### 의존성 주입 규칙
```java
// ✅ 생성자 주입 (Lombok 활용)
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}

// ❌ 필드 주입 금지
@Autowired
private UserRepository userRepository;
```

#### 예외 처리 패턴
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException e) {
        return ResponseEntity.status(UNAUTHORIZED)
            .body(ErrorResponse.of("AUTH_FAILED", e.getMessage()));
    }
}
```

### 4. Bootstrap + Thymeleaf 통합 규칙

#### 템플릿 구조
```
resources/templates/
├── layout/
│   ├── base.html        # 기본 레이아웃
│   └── components/      # 재사용 컴포넌트
├── auth/
│   ├── login.html       # 로그인 페이지
│   └── logout.html      # 로그아웃 페이지
└── fragments/           # HTML 조각들
```

#### Bootstrap 클래스 활용
```html
<!-- ✅ 표준 Bootstrap 클래스 사용 -->
<div class="container-fluid">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-4">
            <div class="card shadow">
                <div class="card-body">
                    <!-- 로그인 폼 -->
                </div>
            </div>
        </div>
    </div>
</div>
```

#### Thymeleaf 표현식 규칙
```html
<!-- ✅ 명확한 속성 사용 -->
<form th:action="@{/login}" th:object="${loginForm}" method="post">
    <input type="text" th:field="*{username}" 
           class="form-control" 
           th:classappend="${#fields.hasErrors('username')} ? 'is-invalid' : ''">
    
    <div th:if="${#fields.hasErrors('username')}" 
         class="invalid-feedback" th:errors="*{username}">
    </div>
</form>
```

## 🔒 Spring Security 개발 규칙

### 보안 설정 패턴
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
                .failureUrl("/login?error=true")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
            )
            .build();
    }
}
```

### 인증/인가 처리
- **UserDetailsService** 구현으로 사용자 정보 로딩
- **PasswordEncoder** 빈 설정 (BCrypt 권장)
- **CSRF 보호** 기본 활성화
- **세션 관리** 설정

## 🗄️ 데이터베이스 개발 규칙

### JPA Entity 패턴
```java
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String email;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

### Repository 패턴
```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
}
```

## 📁 파일 구조 규칙

### 정적 리소스
```
resources/static/
├── css/
│   ├── bootstrap.min.css
│   └── custom.css          # 커스텀 스타일
├── js/
│   ├── bootstrap.min.js
│   └── custom.js           # 커스텀 스크립트
└── images/
    └── logo.png
```

### 환경별 설정
```properties
# application.properties
spring.profiles.active=local

# application-local.properties (개발환경)
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true

# application-prod.properties (운영환경)
spring.datasource.url=jdbc:mysql://localhost:3306/kmdb
```

## ⚡ 빠른 개발을 위한 Claude Code 활용법

### 1. 자동 코드 생성 요청
```
"User 엔티티와 CRUD 컨트롤러, 서비스를 한번에 생성해줘"
"Bootstrap 로그인 폼과 Spring Security 설정을 통합해서 만들어줘"
```

### 2. 템플릿 기반 개발
- 표준 CRUD 패턴 템플릿 활용
- Bootstrap 컴포넌트 재사용
- Spring Security 보일러플레이트 자동 생성

### 3. 테스트 자동화
```
"현재 AuthService에 대한 단위 테스트를 자동으로 생성해줘"
"로그인 플로우 통합 테스트를 만들어줘"
```

## ✅ 체크리스트

### 개발 시 확인사항
- [ ] Lombok 어노테이션 적절히 사용
- [ ] Spring Security 설정 완료
- [ ] Bootstrap CSS/JS 정상 로딩
- [ ] CSRF 토큰 포함
- [ ] 에러 처리 및 검증 로직
- [ ] 반응형 디자인 적용

### Claude Code 요청 시 포함할 정보
1. **구체적인 기능 명세**
2. **사용할 엔티티/테이블 정보**
3. **Bootstrap 컴포넌트 요구사항**
4. **보안 요구사항**
5. **에러 처리 방식**

이 가이드를 따라 개발하면 일관성 있고 빠른 구현이 가능합니다.