package km.km_springboot.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import km.km_springboot.dto.LoginRequest;
import km.km_springboot.dto.LoginResponse;
import km.km_springboot.dto.LogoutResponse;
import km.km_springboot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "로그인/로그아웃 관련 API")
public class AuthApiController {
    
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자명과 패스워드로 로그인을 수행합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        try {
            // 인증 시도
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 마지막 로그인 시간 업데이트
            authService.updateLastLoginAt(loginRequest.getUsername());
            
            // 사용자 정보 가져오기
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            
            return ResponseEntity.ok(LoginResponse.success(loginRequest.getUsername(), role));
            
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                .body(LoginResponse.failure("사용자명 또는 패스워드가 잘못되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(LoginResponse.failure("로그인 중 오류가 발생했습니다."));
        }
    }
    
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public ResponseEntity<LogoutResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
                return ResponseEntity.ok(LogoutResponse.success());
            } else {
                return ResponseEntity.status(401)
                    .body(LogoutResponse.failure("로그인된 사용자가 없습니다."));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(LogoutResponse.failure("로그아웃 중 오류가 발생했습니다."));
        }
    }
}