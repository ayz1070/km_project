package km.km_springboot.repository;

import km.km_springboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// User 엔티티에 대한 데이터 접근을 처리하는 JpaRepository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 사용자명으로 사용자를 찾는 메소드
    Optional<User> findByUsername(String username);
    // 이메일로 사용자를 찾는 메소드
    Optional<User> findByEmail(String email);
    // 사용자명 존재 여부를 확인하는 메소드
    boolean existsByUsername(String username);
    
    // 활성 상태의 사용자를 사용자명으로 찾고, 역할(Role) 정보를 함께 가져오는 메소드 (LazyInitializationException 방지)
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.username = :username AND u.status = 'ACTIVE'")
    Optional<User> findActiveUserByUsername(@Param("username") String username);
}