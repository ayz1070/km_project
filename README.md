# KM SpringBoot 애플리케이션

Spring Boot를 기반으로 한 웹 애플리케이션입니다.

## 📝 깃허브

https://github.com/ayz1070/km_project

## 🚀 주요 기능

- **사용자 인증 및 권한 관리**: Spring Security 기반의 로그인/로그아웃 시스템
- **역할 기반 접근 제어**: ADMIN, USER 역할 분리
- **RESTful API**: 인증 관련 REST API 제공
- **웹 인터페이스**: Thymeleaf 기반 웹 UI
- **API 문서화**: Swagger/OpenAPI 3.0 지원

## 🛠 기술 스택

- **Framework**: Spring Boot 3.5.5
- **Language**: Java 17
- **Security**: Spring Security 6
- **Database**: H2
- **ORM**: Spring Data JPA
- **Template Engine**: Thymeleaf
- **API Documentation**: SpringDoc OpenAPI
- **Build Tool**: Gradle

## 📊 데이터베이스 구조

- **Users**: 사용자 정보 관리 (username, email, password, status)
- **Roles**: 역할 관리 (ADMIN, USER)

자세한 ERD는 [제공된 이미지] 를 참고하세요.


## 🌐 접속 정보

- **애플리케이션**: http://localhost:8080
- **API 문서**: http://localhost:8080/swagger-ui.html

## 👤 기본 계정

개발 환경에서 사용할 수 있는 기본 계정:

| 사용자명 | 비밀번호 | 역할 | 설명 |
|---------|---------|------|------|
| admin | admin123 | ADMIN | 관리자 계정 |
| test | test123 | USER | 테스트 사용자 |
| km | km123 | USER | KM 사용자 |

## 📁 프로젝트 구조

```
src/main/java/km/km_springboot/
├── config/          # 설정 클래스
├── controller/      # 컨트롤러
├── dto/            # 데이터 전송 객체
├── entity/         # JPA 엔티티
├── repository/     # 데이터 접근 계층
├── security/       # 보안 관련 클래스
└── service/        # 비즈니스 로직
```

## 🔒 보안 기능

- BCrypt 패스워드 암호화
- 세션 기반 인증
- CSRF 보호
- 역할 기반 페이지 접근 제어

## 📝 API 엔드포인트

- `POST /api/auth/login` - 로그인
- `POST /api/auth/logout` - 로그아웃
- `GET /dashboard` - 대시보드 (인증 필요)

자세한 API 명세는 Swagger UI에서 확인할 수 있습니다.
