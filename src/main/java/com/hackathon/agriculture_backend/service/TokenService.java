package com.hackathon.agriculture_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    
    private final Map<String, TokenData> tokenStore = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();
    
    @Value("${app.token.email-confirmation.expiry:24}")
    private int emailConfirmationExpiryHours;
    
    @Value("${app.token.password-reset.expiry:1}")
    private int passwordResetExpiryHours;
    
    public String generateEmailConfirmationToken(String email) {
        String token = generateSecureToken();
        LocalDateTime expiry = LocalDateTime.now().plusHours(emailConfirmationExpiryHours);
        
        tokenStore.put(token, new TokenData(email, TokenType.EMAIL_CONFIRMATION, expiry));
        log.info("Generated email confirmation token for: {}", email);
        
        return token;
    }
    
    public String generatePasswordResetToken(String email) {
        String token = generateSecureToken();
        LocalDateTime expiry = LocalDateTime.now().plusHours(passwordResetExpiryHours);
        
        tokenStore.put(token, new TokenData(email, TokenType.PASSWORD_RESET, expiry));
        log.info("Generated password reset token for: {}", email);
        
        return token;
    }
    
    public boolean validateToken(String token, TokenType expectedType) {
        TokenData tokenData = tokenStore.get(token);
        
        if (tokenData == null) {
            log.warn("Token not found: {}", token);
            return false;
        }
        
        if (tokenData.getType() != expectedType) {
            log.warn("Token type mismatch. Expected: {}, Actual: {}", expectedType, tokenData.getType());
            return false;
        }
        
        if (tokenData.getExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Token expired: {}", token);
            tokenStore.remove(token);
            return false;
        }
        
        return true;
    }
    
    public String getEmailFromToken(String token) {
        TokenData tokenData = tokenStore.get(token);
        return tokenData != null ? tokenData.getEmail() : null;
    }
    
    public void invalidateToken(String token) {
        tokenStore.remove(token);
        log.info("Token invalidated: {}", token);
    }
    
    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    public enum TokenType {
        EMAIL_CONFIRMATION,
        PASSWORD_RESET
    }
    
    private static class TokenData {
        private final String email;
        private final TokenType type;
        private final LocalDateTime expiry;
        
        public TokenData(String email, TokenType type, LocalDateTime expiry) {
            this.email = email;
            this.type = type;
            this.expiry = expiry;
        }
        
        public String getEmail() {
            return email;
        }
        
        public TokenType getType() {
            return type;
        }
        
        public LocalDateTime getExpiry() {
            return expiry;
        }
    }
}
