package com.example.graalvm.controller;

import com.example.graalvm.entity.User;
import com.example.graalvm.service.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/login")
    public String login(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return "redirect:/dashboard";
        }
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("isOAuth2", authentication.getPrincipal() instanceof OAuth2User);
        
        return "dashboard";
    }
    
    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("isOAuth2", authentication.getPrincipal() instanceof OAuth2User);
        
        return "profile";
    }
    
    private User getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof CustomOAuth2User) {
            return ((CustomOAuth2User) principal).getUser();
        }
        
        // Fallback - should not happen in normal flow
        return User.builder()
                .email(authentication.getName())
                .firstName("Unknown")
                .lastName("User")
                .build();
    }
}
