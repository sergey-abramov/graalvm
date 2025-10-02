package com.example.graalvm.service;

import com.example.graalvm.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    
    @Getter
    private final User user;
    private final Map<String, Object> attributes;
    
    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }
    
    @Override
    public String getName() {
        return user.getEmail();
    }

    public String getEmail() {
        return user.getEmail();
    }
    
    public String getFirstName() {
        return user.getFirstName();
    }
    
    public String getLastName() {
        return user.getLastName();
    }
    
    public String getImageUrl() {
        return user.getImageUrl();
    }
}
