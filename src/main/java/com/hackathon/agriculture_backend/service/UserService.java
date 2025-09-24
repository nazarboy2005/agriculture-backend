package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.model.User;
import com.hackathon.agriculture_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    
    public User findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with Google ID: " + googleId));
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public Optional<User> findByEmailOptional(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByGoogleIdOptional(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByGoogleId(String googleId) {
        return userRepository.existsByGoogleId(googleId);
    }
}
