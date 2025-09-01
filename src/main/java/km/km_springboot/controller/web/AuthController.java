package km.km_springboot.controller.web;

import km.km_springboot.dto.LoginForm;
import km.km_springboot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// 인증 관련 웹 요청을 처리하는 컨트롤러
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    // 로그인 페이지를 보여주는 메소드
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        // 이미 인증된 사용자는 대시보드로 리디렉션
        if (authService.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        
        // 뷰에 loginForm 객체와 에러/로그아웃 메시지 전달
        model.addAttribute("loginForm", new LoginForm());
        if (error != null) {
            model.addAttribute("error", "사용자명 또는 패스워드가 잘못되었습니다.");
        }
        if (logout != null) {
            model.addAttribute("message", "성공적으로 로그아웃되었습니다.");
        }
        
        return "auth/login";
    }
}
