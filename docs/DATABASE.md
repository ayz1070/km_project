# 데이터베이스 설계 문서

## 🗄️ 데이터베이스 개요

### 지원 데이터베이스
- **H2**: 개발/테스트 (인메모리)
- **MySQL**: 운영 환경 (8.0+)  
- **MariaDB**: 대안 운영 환경

### 설계 원칙
- 단순성 우선 (빠른 개발)
- JPA/Hibernate 표준 준수
- 향후 확장 가능한 구조

## 📊 핵심 테이블 구조

### ERD
```
User (사용자)     →     Role (권한)
├── id (PK)            ├── id (PK)
├── username (UK)      ├── name (ADMIN/USER)
├── password           └── description
├── email (UK)
├── full_name
├── status (ENUM)
├── role_id (FK)
└── BaseEntity (created_at, updated_at)
```

## 📋 핵심 테이블 설계

### USERS 테이블 (필수)
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,               -- BCrypt 암호화
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') DEFAULT 'ACTIVE',
    role_id BIGINT NOT NULL,                      -- FK to roles
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    
    FOREIGN KEY (role_id) REFERENCES roles(id),
    INDEX idx_username (username),
    INDEX idx_email (email)
);
```

### ROLES 테이블 (필수)
```sql
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,             -- ADMIN, USER
    description VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 기본 데이터
INSERT INTO roles (name, description) VALUES 
('ADMIN', '시스템 관리자'), ('USER', '일반 사용자');
```

## 🔗 JPA Entity 매핑

### User Entity
```java
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    // 패스워드 암호화 메서드
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
    
    // 로그인 시간 업데이트
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
```

### Role Entity
```java
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String name;
    
    @Column(length = 200)
    private String description;
    
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();
}
```

### UserSession Entity (선택사항)
```java
@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {
    
    @Id
    @Column(name = "session_id")
    private String sessionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
}
```

### BaseEntity (공통 엔티티)
```java
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

### Enum 클래스
```java
public enum UserStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    LOCKED("잠금");
    
    private final String description;
    
    UserStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
```

## ⚙️ 환경별 DB 설정

### H2 (개발환경)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
```

### MySQL (운영환경)  
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/kmdb
spring.datasource.username=${DB_USERNAME:admin}
spring.datasource.password=${DB_PASSWORD:password}
spring.jpa.hibernate.ddl-auto=validate
```

## 🔄 Repository 패턴

### UserRepository (핵심 메서드)
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.status = 'ACTIVE'")
    Optional<User> findActiveUserByUsername(@Param("username") String username);
}
```

### RoleRepository
```java
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
```

## 📝 초기 데이터 설정

### 기본 DDL
```sql
-- 관리자 계정 생성 (패스워드: admin123)
INSERT INTO roles (name, description) VALUES ('ADMIN', '관리자'), ('USER', '사용자');
INSERT INTO users (username, password, email, full_name, role_id) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIFi', 'admin@example.com', '관리자', 1);
```

간결하고 실용적인 데이터베이스 설계로 빠른 개발이 가능합니다.