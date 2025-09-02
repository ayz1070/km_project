package km.km_springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "로그아웃 응답")
public class LogoutResponse {
    
    @Schema(description = "성공 여부", example = "true")
    private boolean success;
    
    @Schema(description = "메시지", example = "로그아웃이 성공하였습니다.")
    private String message;
    
    public static LogoutResponse success() {
        return new LogoutResponse(true, "로그아웃이 성공하였습니다.");
    }
    
    public static LogoutResponse failure(String message) {
        return new LogoutResponse(false, message);
    }
}