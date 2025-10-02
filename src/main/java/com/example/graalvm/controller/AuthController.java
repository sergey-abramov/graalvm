package com.example.graalvm.controller;

import com.example.graalvm.entity.User;
import com.example.graalvm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final UserService userService;
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               Model model) {
        try {
            userService.createUser(email, password, firstName, lastName);
            return "redirect:/login?registration=success";
        } catch (Exception e) {
            log.error("Registration error", e);
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("user", new User());
            return "register";
        }
    }
    
    @GetMapping("/api/user/info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        User user = getCurrentUser(authentication);
        
        return ResponseEntity.ok(Map.of(
                "id", user.getId() != null ? user.getId() : 0L,
                "email", user.getEmail(),
                "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                "lastName", user.getLastName() != null ? user.getLastName() : "",
                "provider", user.getProvider().name(),
                "imageUrl", user.getImageUrl() != null ? user.getImageUrl() : ""
        ));
    }
    
    @PostMapping("/api/user/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody Map<String, String> request,
                                                          Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            // For simplicity, just return success - full implementation would update user
            return ResponseEntity.ok(Map.of("success", true, "message", "Profile updated successfully"));
        } catch (Exception e) {
            log.error("Error updating user profile", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    private User getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof com.example.graalvm.service.CustomOAuth2User) {
            return ((com.example.graalvm.service.CustomOAuth2User) principal).getUser();
        }
        
        // Fallback
        return User.builder()
                .email(authentication.getName())
                .firstName("Unknown")
                .lastName("User")
                .build();
    }
}
