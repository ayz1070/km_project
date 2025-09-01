package km.km_springboot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginForm {
    
    @NotBlank(message = "사용자명을 입력해주세요")
    private String username;
    
    @NotBlank(message = "패스워드를 입력해주세요")
    private String password;
}