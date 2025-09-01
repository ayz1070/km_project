package km.km_springboot.entity;

/**
 * 사용자 계정 상태를 나타내는 열거형
 */
public enum UserStatus {
    /**
     * 활성 상태
     */
    ACTIVE("활성"),
    /**
     * 비활성 상태
     */
    INACTIVE("비활성"),
    /**
     * 잠금 상태
     */
    LOCKED("잠금");
    
    private final String description;
    
    UserStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}