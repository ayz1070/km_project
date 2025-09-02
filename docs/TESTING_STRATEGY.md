# KM Spring Boot 테스트 전략

Spring Security 기반 인증/인가 시스템과 Bootstrap UI를 포함한 웹 애플리케이션의 품질을 보장하는 테스트 전략입니다.

## 📋 테스트 목표

- **보안 검증**: Spring Security 인증/인가 로직의 정확한 동작 확인
- **기능 검증**: 로그인/로그아웃, 사용자 관리 등 핵심 기능의 정확성 검증
- **회귀 방지**: 새로운 코드 변경이 기존 기능에 영향을 주지 않음을 보장
- **리팩토링 지원**: 안정적인 테스트 기반으로 코드 개선 환경 구축
- **UI 검증**: Thymeleaf 템플릿과 Bootstrap 컴포넌트의 렌더링 확인

## 🏗️ 테스트 계층 구조

### 1. Repository 테스트 (`@DataJpaTest`)
```java
@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void 사용자명으로_사용자_찾기() {
        // when
        Optional<User> foundUser = userRepository.findByUsername("admin");
        
        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("admin");
    }
}
```

### 2. Service 테스트 (`@SpringBootTest` + `@Transactional`)
```java
@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;
    
    @Test
    void 마지막_로그인_시간_업데이트() {
        // given-when-then 패턴으로 비즈니스 로직 검증
        User user = userRepository.findByUsername("admin").get();
        LocalDateTime initialTime = user.getLastLoginAt();
        
        userService.updateLastLoginAt("admin");
        
        User updatedUser = userRepository.findByUsername("admin").get();
        assertThat(updatedUser.getLastLoginAt()).isAfter(initialTime);
    }
}
```

### 3. Controller 테스트

#### 단위 테스트 (`@ExtendWith(MockitoExtension.class)`)
```java
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    private MockMvc mockMvc;
    
    @Mock private AuthService authService;
    @InjectMocks private AuthController authController;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }
    
    @Test
    void 인증되지_않은_사용자는_로그인_페이지를_볼_수_있다() throws Exception {
        // given
        given(authService.isAuthenticated()).willReturn(false);
        
        // when & then
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }
}
```

### 4. Security 테스트 (`@WithMockUser`, `@WithUserDetails`)
```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void 인증되지_않은_사용자는_로그인_페이지로_리다이렉트된다() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void 관리자는_대시보드에_접근할_수_있다() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"));
    }
    
    @Test
    @WithMockUser
    void POST_요청시_CSRF_토큰_필요() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isForbidden());
                
        mockMvc.perform(post("/auth/logout").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
```

### 5. 통합 테스트 (End-to-End)
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class LoginIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void 전체_로그인_플로우_테스트() {
        // given: 로그인 페이지 접근
        ResponseEntity<String> loginPage = restTemplate.getForEntity("/auth/login", String.class);
        assertThat(loginPage.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // when: 올바른 자격증명으로 로그인
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("username", "admin");
        form.add("password", "admin123");
        
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/auth/login", form, String.class);
        
        // then: 대시보드로 리다이렉트
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }
}
```

## 🛠️ 핵심 도구

### 테스트 프레임워크
- **JUnit 5**: 테스트 프레임워크의 기반
- **Spring Boot Test**: 통합 테스트를 위한 Spring 컨텍스트 지원
- **Mockito**: Mock 객체 생성 및 검증 (`@Mock`, `@InjectMocks`)
- **AssertJ**: 유창한 API 검증 라이브러리 (`assertThat`)

### 웹 & 보안 테스트
- **MockMvc**: HTTP 요청/응답 시뮬레이션
- **TestRestTemplate**: REST API 통합 테스트
- **Spring Security Test**: `@WithMockUser`, `@WithUserDetails`
- **H2 Database**: 인메모리 테스트 데이터베이스

## ⚙️ 테스트 설정

### application-test.properties
```properties
# 테스트 전용 설정
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.sql.init.mode=never
logging.level.org.springframework.security=DEBUG
```

### 커스텀 테스트 프로파일
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public @interface TestProfile {
}
```

## 📊 품질 기준

### 커버리지 목표
- **라인 커버리지**: 80% 이상
- **브랜치 커버리지**: 70% 이상
- **보안 관련 코드**: 90% 이상

### 우선순위
1. **High**: Security, Controller, Service 핵심 로직
2. **Medium**: Repository, Configuration
3. **Low**: DTO, Entity, Utility

## 📝 명명 규칙

### 클래스 명명
- **단위 테스트**: `[클래스명]Test.java` (예: `UserServiceTest.java`)
- **통합 테스트**: `[기능명]IntegrationTest.java` (예: `LoginIntegrationTest.java`)
- **보안 테스트**: `[기능명]SecurityTest.java` (예: `AuthSecurityTest.java`)

### 메서드 명명 (BDD 스타일)
- **패턴**: `[상황]_[행동]_[결과]` (한글 권장)
- **예시**: `관리자가_로그인하면_대시보드에_접근할_수_있다()`
- **예외 테스트**: `잘못된_비밀번호로_로그인하면_예외가_발생한다()`

## 🚀 실행 계획

### Phase 1: 기본 테스트 인프라
- [x] 기본 contextLoads() 테스트
- [ ] TestProfile 및 설정
- [ ] Repository 계층 테스트

### Phase 2: 핵심 비즈니스 로직
- [ ] UserService 테스트
- [ ] AuthService 테스트
- [ ] DataInitializer 테스트

### Phase 3: 웹 계층 및 보안
- [ ] AuthController 테스트
- [ ] DashboardController 테스트
- [ ] Spring Security 통합 테스트

### Phase 4: 통합 테스트
- [ ] 로그인 플로우 End-to-End 테스트
- [ ] 권한 기반 접근 제어 테스트
- [ ] UI 렌더링 테스트

## 💡 핵심 포인트

- **Security 중심**: 인증/인가 테스트가 최우선
- **계층별 분리**: 각 계층에 맞는 테스트 도구 사용
- **통합 테스트**: 실제 사용자 시나리오 검증
- **BDD 스타일**: Given-When-Then 패턴으로 명확한 의도 표현
- **자동화**: Gradle + Jacoco로 커버리지 측정

이 전략을 바탕으로 실제 테스트 코드를 단계적으로 구현할 수 있습니다.
