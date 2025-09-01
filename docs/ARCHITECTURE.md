# Spring Boot 프로젝트 아키텍처 설계

## 📁 핵심 패키지 구조
```
km.km_springboot/
├── config/              # 설정 (Security, Web, Database)
├── controller/
│   ├── api/            # REST API 컨트롤러
│   └── web/            # 웹 페이지 컨트롤러
├── service/            # 비즈니스 로직
├── repository/         # 데이터 액세스 (JPA)
├── entity/             # JPA 엔티티
├── dto/                # 데이터 전송 객체
├── security/           # Spring Security 관련
├── exception/          # 예외 처리
└── util/              # 유틸리티

resources/
├── templates/          # Thymeleaf 템플릿
├── static/            # CSS, JS, Images
└── application.properties
```

## 🔄 3계층 아키텍처

### 1. Presentation Layer
- **Web Controller**: Thymeleaf 템플릿 반환
- **REST Controller**: JSON API 응답

### 2. Business Layer
- **Service Interface**: 비즈니스 로직 정의
- **Service Impl**: 실제 구현체 (@Transactional)

### 3. Data Access Layer  
- **Repository**: Spring Data JPA 활용
- **Entity**: JPA 엔티티 (BaseEntity 패턴)

## 🔒 Spring Security 구조

### Filter Chain 플로우
```
HTTP Request → Security Filters → Controller
├── CSRF Filter
├── Authentication Filter  
├── Authorization Filter
└── Exception Handler
```

### 핵심 설정
- **로그인**: `/auth/login` (Form 기반)
- **로그아웃**: `/auth/logout` (세션 무효화)
- **권한**: ADMIN, USER 역할 기반
- **CSRF**: 토큰 기반 보호

## 📱 Frontend 구조 (Bootstrap + Thymeleaf)

### 템플릿 레이아웃
```html
layout/base.html          # 기본 레이아웃
├── fragments/header      # 네비게이션
├── fragments/footer      # 푸터
└── auth/login.html       # 로그인 페이지
```

### 핵심 컴포넌트
- **반응형 네비게이션**: Bootstrap Navbar
- **폼 검증**: Thymeleaf + Bean Validation
- **알림 메시지**: Bootstrap Alert

## 🗄️ 데이터베이스 구조

### Entity 관계
```
User (사용자)     →     Role (권한)
├── id (PK)            ├── id (PK)
├── username           ├── name (ADMIN/USER)  
├── password           └── description
├── email              
├── role_id (FK)       
└── BaseEntity         
```

### 환경별 DB 설정
- **Local**: H2 인메모리 DB
- **Production**: MySQL/MariaDB

## ⚡ 개발 최적화

### 성능 최적화
- **JPA**: N+1 문제 해결, 지연 로딩
- **캐싱**: @Cacheable 활용
- **세션**: 적절한 타임아웃 설정

### 보안 최적화  
- **패스워드**: BCrypt 암호화
- **CSRF**: 토큰 기반 보호
- **XSS**: Thymeleaf 자동 이스케이프

이 간결한 아키텍처를 기반으로 빠르고 안전한 Spring Boot 애플리케이션을 구축할 수 있습니다.