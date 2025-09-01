package km.km_springboot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    /**
     * 역할 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 역할명 (e.g., "ADMIN", "USER")
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 역할 설명
     */
    @Column(length = 200)
    private String description;

    /**
     * 이 역할을 가진 사용자 목록
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();
}