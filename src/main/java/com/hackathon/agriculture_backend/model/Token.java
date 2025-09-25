package com.hackathon.agriculture_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(nullable = false)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType type;
    
    @Column(nullable = false)
    private LocalDateTime expiry;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime usedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum TokenType {
        EMAIL_CONFIRMATION,
        PASSWORD_RESET
    }
    
    public boolean isExpired() {
        return expiry.isBefore(LocalDateTime.now());
    }
    
    public boolean isUsed() {
        return usedAt != null;
    }
}

