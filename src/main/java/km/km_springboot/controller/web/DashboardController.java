package km.km_springboot.controller.web;

import km.km_springboot.entity.User;
import km.km_springboot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// 대시보드 관련 웹 요청을 처리하는 컨트롤러
@Controller
@RequiredArgsConstructor
public class DashboardController {
    
    private final AuthService authService;
    
    // 대시보드 페이지를 보여주는 메소드
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        // 현재 로그인된 사용자 정보를 가져와 모델에 추가
        User currentUser = authService.getCurrentUser();
        model.addAttribute("user", currentUser);
        model.addAttribute("title", "대시보드");
        
        // 마지막 로그인 시간 업데이트
        authService.updateLastLoginAt();
        
        return "dashboard/index";
    }
}
