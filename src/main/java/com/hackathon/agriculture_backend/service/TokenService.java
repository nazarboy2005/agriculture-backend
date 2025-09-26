package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.model.Token;
import com.hackathon.agriculture_backend.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    
    private final TokenRepository tokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    
    @Value("${app.token.email-confirmation.expiry:24}")
    private int emailConfirmationExpiryHours;
    
    @Value("${app.token.password-reset.expiry:1}")
    private int passwordResetExpiryHours;
    
    @Transactional
    public String generateEmailConfirmationToken(String email) {
        // Delete any existing tokens for this email and type
        tokenRepository.deleteByEmailAndType(email, Token.TokenType.EMAIL_CONFIRMATION);
        
        String token = generateSecureToken();
        LocalDateTime expiry = LocalDateTime.now().plusHours(emailConfirmationExpiryHours);
        
        Token tokenEntity = new Token(null, token, email, Token.TokenType.EMAIL_CONFIRMATION, expiry, null, null);
        
        tokenRepository.save(tokenEntity);
        System.out.println("Generated email confirmation token for: " + email);
        
        return token;
    }
    
    @Transactional
    public String generatePasswordResetToken(String email) {
        // Delete any existing tokens for this email and type
        tokenRepository.deleteByEmailAndType(email, Token.TokenType.PASSWORD_RESET);
        
        String token = generateSecureToken();
        LocalDateTime expiry = LocalDateTime.now().plusHours(passwordResetExpiryHours);
        
        Token tokenEntity = new Token(null, token, email, Token.TokenType.PASSWORD_RESET, expiry, null, null);
        
        tokenRepository.save(tokenEntity);
        System.out.println("Generated password reset token for: " + email);
        
        return token;
    }
    
    public boolean validateToken(String token, Token.TokenType expectedType) {
        Optional<Token> tokenOpt = tokenRepository.findByTokenAndType(token, expectedType);
        
        if (tokenOpt.isEmpty()) {
            System.out.println("Token not found: " + token);
            return false;
        }
        
        Token tokenEntity = tokenOpt.get();
        
        if (tokenEntity.isExpired()) {
            System.out.println("Token expired: " + token);
            tokenRepository.delete(tokenEntity);
            return false;
        }
        
        if (tokenEntity.isUsed()) {
            System.out.println("Token already used: " + token);
            return false;
        }
        
        return true;
    }
    
    public String getEmailFromToken(String token) {
        Optional<Token> tokenOpt = tokenRepository.findByToken(token);
        return tokenOpt.map(Token::getEmail).orElse(null);
    }
    
    @Transactional
    public void invalidateToken(String token) {
        Optional<Token> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isPresent()) {
            Token tokenEntity = tokenOpt.get();
            tokenEntity.setUsedAt(LocalDateTime.now());
            tokenRepository.save(tokenEntity);
            System.out.println("Token invalidated: " + token);
        }
    }
    
    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
