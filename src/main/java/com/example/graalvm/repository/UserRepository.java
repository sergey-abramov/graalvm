package com.example.graalvm.repository;

import com.example.graalvm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByProviderIdAndProvider(String providerId, com.example.graalvm.entity.AuthProvider provider);
    
    boolean existsByEmail(String email);
}
