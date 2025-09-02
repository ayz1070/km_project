package km.km_springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "로그인 요청")
public class LoginRequest {
    
    @NotBlank(message = "사용자명을 입력해주세요")
    @Schema(description = "사용자명", example = "admin", required = true)
    private String username;
    
    @NotBlank(message = "패스워드를 입력해주세요")
    @Schema(description = "패스워드", example = "admin123", required = true)
    private String password;
}