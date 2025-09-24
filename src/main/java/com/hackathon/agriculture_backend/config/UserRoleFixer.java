package com.hackathon.agriculture_backend.config;

import com.hackathon.agriculture_backend.model.User;
import com.hackathon.agriculture_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRoleFixer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Checking and fixing user roles...");
        
        // Find all users with ADMIN role that shouldn't be admin
        List<User> adminUsers = userRepository.findByRole(User.Role.ADMIN);
        
        if (!adminUsers.isEmpty()) {
            log.warn("Found {} users with ADMIN role", adminUsers.size());
            
            for (User user : adminUsers) {
                // Only keep admin role for specific admin emails or first user
                boolean shouldBeAdmin = user.getEmail().equals("admin@agriculture.com") || 
                                       user.getEmail().equals("killiyaezov@gmail.com") ||
                                       userRepository.count() == 1;
                
                if (!shouldBeAdmin) {
                    log.info("Converting user {} from ADMIN to USER role", user.getEmail());
                    user.setRole(User.Role.USER);
                    userRepository.save(user);
                } else {
                    log.info("Keeping admin role for user: {}", user.getEmail());
                }
            }
        }
        
        // Log all users and their roles
        List<User> allUsers = userRepository.findAll();
        log.info("Current users in database:");
        for (User user : allUsers) {
            log.info("User: {} - Role: {}", user.getEmail(), user.getRole());
        }
        
        log.info("User role check completed");
    }
}
