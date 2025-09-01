package km.km_springboot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    /**
     * 사용자 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자명 (로그인 시 사용)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 비밀번호 (암호화하여 저장)
     */
    @Column(nullable = false)
    private String password;

    /**
     * 이메일 주소
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 전체 이름
     */
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    /**
     * 사용자 상태 (ACTIVE, INACTIVE, LOCKED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * 사용자의 역할
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /**
     * 마지막 로그인 일시
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * 비밀번호를 암호화합니다.
     * @param passwordEncoder Spring Security의 PasswordEncoder
     */
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    /**
     * 마지막 로그인 시간을 현재 시간으로 업데이트합니다.
     */
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}