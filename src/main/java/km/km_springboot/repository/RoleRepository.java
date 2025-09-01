package km.km_springboot.repository;

import km.km_springboot.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Role 엔티티에 대한 데이터 접근을 처리하는 JpaRepository
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // 이름으로 역할을 찾는 메소드
    Optional<Role> findByName(String name);
}