package com.example.graalvm.service;

import com.example.graalvm.entity.AuthProvider;
import com.example.graalvm.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserService userService;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw new OAuth2AuthenticationException("Error processing OAuth2 user: " + ex.getMessage());
        }
    }
    
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());
        
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = getEmail(attributes);
        String firstName = getFirstName(attributes, registrationId);
        String lastName = getLastName(attributes, registrationId);
        String providerId = getProviderId(attributes, registrationId);
        String imageUrl = getImageUrl(attributes, registrationId);
        
        if (email == null || email.trim().isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }
        
        User user = userService.createOAuthUser(email, firstName, lastName, providerId, provider, imageUrl);
        
        return new CustomOAuth2User(user, oauth2User.getAttributes());
    }
    
    private String getEmail(Map<String, Object> attributes) {
        return (String) attributes.get("email");
    }
    
    private String getFirstName(Map<String, Object> attributes, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("given_name");
        }
        return (String) attributes.get("first_name");
    }
    
    private String getLastName(Map<String, Object> attributes, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("family_name");
        }
        return (String) attributes.get("last_name");
    }
    
    private String getProviderId(Map<String, Object> attributes, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("sub");
        }
        return (String) attributes.get("id");
    }
    
    private String getImageUrl(Map<String, Object> attributes, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("picture");
        }
        return (String) attributes.get("avatar_url");
    }
}
