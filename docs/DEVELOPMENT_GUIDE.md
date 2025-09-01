# 개발 환경 및 워크플로우 가이드

## 🚀 빠른 시작

### 필수 도구
- **Java 17+**, **Gradle**, **Claude Code**
- **H2** (개발용), **MySQL** (운영용)

### 프로젝트 설정
```bash
git clone <repository-url> && cd km_springboot
./gradlew clean build
./gradlew bootRun
# H2 콘솔: http://localhost:8080/h2-console
```

### IDE 플러그인 (IntelliJ)
- Lombok, Spring Boot, Thymeleaf Plugin

## 🔄 Claude Code 개발 워크플로우

### 개발 프로세스
```
요구사항 → Claude 활용 설계 → Entity/Service 구현 → Controller → Template → 테스트
```

### Claude에게 효과적인 요청 방법
```bash
# 좋은 요청 예시
"User 엔티티와 CRUD 기능을 Bootstrap UI로 구현해줘"
"Spring Security 로그인/로그아웃 기능을 Thymeleaf로 만들어줘"
```

## 🧪 테스트 실행
```bash
# 전체 테스트
./gradlew test

# 애플리케이션 실행
./gradlew bootRun
```

## 🏗️ 빌드 및 배포

### 로컬 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 운영 배포
```bash
./gradlew clean build
java -jar build/libs/km-springboot.jar --spring.profiles.active=prod
```

## 📋 Git 워크플로우

### 브랜치 전략
```bash
# 기능 개발
git checkout -b feature/새기능명
git commit -m "feat: 새 기능 구현"
git checkout main && git merge feature/새기능명
```

### 커밋 규칙
- **feat**: 새 기능
- **fix**: 버그 수정  
- **docs**: 문서 수정

## 🚨 주요 트러블슈팅

### H2 콘솔 접속 불가
```properties
spring.h2.console.enabled=true  
# URL: http://localhost:8080/h2-console
```

### CSRF 토큰 오류
```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
```

### Bootstrap 로딩 실패
```java
// SecurityConfig에서 정적 리소스 허용
.requestMatchers("/css/**", "/js/**").permitAll()
```

간결하고 실용적인 개발 가이드로 빠른 개발이 가능합니다.