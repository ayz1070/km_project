package km.km_springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "로그인 응답")
public class LoginResponse {
    
    @Schema(description = "성공 여부", example = "true")
    private boolean success;
    
    @Schema(description = "메시지", example = "로그인이 성공하였습니다.")
    private String message;
    
    @Schema(description = "사용자명", example = "admin")
    private String username;
    
    @Schema(description = "사용자 역할", example = "ADMIN")
    private String role;
    
    public static LoginResponse success(String username, String role) {
        return new LoginResponse(true, "로그인이 성공하였습니다.", username, role);
    }
    
    public static LoginResponse failure(String message) {
        return new LoginResponse(false, message, null, null);
    }
}