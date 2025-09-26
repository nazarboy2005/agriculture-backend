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
        System.out.println("Checking and fixing user roles...");
        
        // Find all users with ADMIN role that shouldn't be admin
        List<User> adminUsers = userRepository.findByRole(User.Role.ADMIN);
        
        if (!adminUsers.isEmpty()) {
            System.out.println("Found " + adminUsers.size() + " users with ADMIN role");
            
            for (User user : adminUsers) {
                // Only keep admin role for specific admin emails or first user
                boolean shouldBeAdmin = user.getEmail().equals("admin@agriculture.com") || 
                                       user.getEmail().equals("killiyaezov@gmail.com") ||
                                       userRepository.count() == 1;
                
                if (!shouldBeAdmin) {
                    System.out.println("Converting user " + user.getEmail() + " from ADMIN to USER role");
                    user.setRole(User.Role.USER);
                    userRepository.save(user);
                } else {
                    System.out.println("Keeping admin role for user: " + user.getEmail());
                }
            }
        }
        
        // Log all users and their roles
        List<User> allUsers = userRepository.findAll();
        System.out.println("Current users in database:");
        for (User user : allUsers) {
            System.out.println("User: " + user.getEmail() + " - Role: " + user.getRole());
        }
        
        System.out.println("User role check completed");
    }
}
