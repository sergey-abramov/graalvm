package com.example.graalvm.config;

import com.example.graalvm.entity.AuthProvider;
import com.example.graalvm.entity.User;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(RuntimeHintsConfig.MyRuntimeHints.class)
public class RuntimeHintsConfig {
    
    static class MyRuntimeHints implements RuntimeHintsRegistrar {
        
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Register entities for reflection
            hints.reflection()
                    .registerType(User.class, MemberCategory.values())
                    .registerType(AuthProvider.class, MemberCategory.values());
            
            // Register Thymeleaf templates
            hints.resources()
                    .registerPattern("templates/*.html")
                    .registerPattern("static/**")
                    .registerPattern("META-INF/resources/**");
            
            // Register database schema
            hints.resources()
                    .registerPattern("db/migration/*.sql")
                    .registerPattern("schema.sql")
                    .registerPattern("data.sql");
            
            // Register Spring Security classes that might need reflection
            hints.reflection()
                    .registerType(org.springframework.security.core.userdetails.UserDetails.class, MemberCategory.values())
                    .registerType(org.springframework.security.oauth2.core.user.OAuth2User.class, MemberCategory.values());
            
            // Register Lambda-specific classes
            hints.reflection()
                    .registerType(com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent.class, MemberCategory.values())
                    .registerType(com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent.class, MemberCategory.values());
        }
    }
}
