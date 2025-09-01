# 테스트 전략

이 문서는 KM Spring Boot 프로젝트의 테스트 전략을 정의합니다. 고품질의 소프트웨어를 유지하고 안정적인 기능을 보장하기 위해 체계적인 테스트는 필수적입니다.

## 1. 테스트 목표

- **정확성 검증**: 모든 기능이 요구사항에 맞게 정확하게 동작하는지 확인합니다.
- **회귀 방지**: 새로운 코드 변경이 기존 기능에 영향을 주지 않는다는 것을 보장합니다.
- **리팩토링 지원**: 안정적인 테스트 코드를 기반으로 안심하고 코드를 리팩토링할 수 있는 환경을 구축합니다.
- **문서화**: 테스트 코드를 통해 코드의 동작 방식을 명확하게 문서화합니다.

## 2. 테스트 계층

프로젝트는 계층형 아키텍처를 따르며, 각 계층에 맞는 테스트 전략을 사용합니다.

### 2.1. Controller (Web Layer) 테스트

- **목표**: API 엔드포인트의 요청/응답, 데이터 변환(DTO), 유효성 검사(Validation)를 테스트합니다.
- **방법**: `@ExtendWith(MockitoExtension.class)`를 사용하여 Mockito 기반의 단위 테스트를 수행하고, `MockMvcBuilders.standaloneSetup()`을 통해 `MockMvc`를 수동으로 설정합니다. 이를 통해 웹 계층을 독립적으로 테스트합니다.
- **도구**: `MockMvc`를 사용하여 HTTP 요청을 시뮬레이션하고, 응답을 검증합니다.
- **의존성 관리**: 서비스 계층의 의존성은 `@Mock`과 `@InjectMocks`를 사용하여 Mock 객체로 주입합니다.

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// Mockito 기반의 컨트롤러 단위 테스트
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock // Mock 객체 생성
    private AuthService authService;

    @InjectMocks // Mock 객체를 주입할 컨트롤러 인스턴스 생성
    private AuthController authController;

    @BeforeEach // 각 테스트 실행 전 MockMvc 설정
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

### 2.2. Service (Business Logic) 테스트

- **목표**: 핵심 비즈니스 로직의 정확성을 검증합니다.
- **방법**:
    - **단위 테스트**: 순수한 비즈니스 로직을 테스트할 경우, Mockito를 사용한 JUnit 테스트를 진행합니다.
    - **통합 테스트**: 데이터베이스와의 상호작용을 포함한 전체적인 서비스 흐름을 테스트할 경우, `@SpringBootTest`와 `@Transactional`을 사용합니다.
- **의존성 관리**: Repository 계층의 의존성은 Mock 객체로 대체하거나, 통합 테스트 시 실제 DB(H2)를 사용합니다.

```java
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 마지막_로그인_시간_업데이트() {
        // given
        User user = userRepository.findByUsername("admin").get();
        LocalDateTime initialLoginTime = user.getLastLoginAt();

        // when
        userService.updateLastLoginAt("admin");

        // then
        User updatedUser = userRepository.findByUsername("admin").get();
        assertThat(updatedUser.getLastLoginAt()).isAfter(initialLoginTime);
    }
}
```

### 2.3. Repository (Data Access) 테스트

- **목표**: JPA 쿼리 및 데이터베이스와의 상호작용을 검증합니다.
- **방법**: `@DataJpaTest`를 사용하여 데이터 접근 계층만 테스트합니다. 인메모리 데이터베이스(H2)를 사용하여 외부 환경의 영향을 받지 않습니다.
- **특징**: `@DataJpaTest`는 기본적으로 `@Transactional`을 포함하므로, 각 테스트 후 데이터는 롤백됩니다.

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

## 3. 테스트 도구 및 프레임워크

- **JUnit 5**: 테스트 작성을 위한 핵심 프레임워크입니다.
- **Spring Boot Test**: Spring 애플리케이션 컨텍스트를 로드하여 통합 테스트를 지원합니다.
- **Mockito**: 의존성을 가진 객체를 Mock(가짜) 객체로 만들어 테스트의 독립성을 보장합니다.
- **AssertJ**: `assertThat`을 사용하여 가독성 높고 풍부한 표현의 검증문을 작성합니다.
- **H2 Database**: 테스트 시 사용할 인메동작리 데이터베이스입니다.

## 4. 테스트 명명 규칙

- **클래스**: `[클래스명]Tests.java` (예: `UserServiceTests.java`)
- **메소드**: `[테스트할_메소드명]_[상황]_[예상결과]` 형식의 한글 메소드명을 사용하여 테스트의 의도를 명확하게 표현합니다. (예: `회원가입_성공()`, `잘못된_정보로_로그인시_예외발생()`)

## 5. 향후 계획

현재 프로젝트에는 기본적인 `contextLoads()` 테스트만 존재합니다. 위에서 정의한 전략에 따라 각 계층별 테스트 코드를 점진적으로 추가하여 코드 커버리지를 높이고, 애플리케이션의 안정성을 확보해 나갈 계획입니다.
