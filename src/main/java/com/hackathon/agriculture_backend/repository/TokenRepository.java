package com.hackathon.agriculture_backend.repository;

import com.hackathon.agriculture_backend.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    
    Optional<Token> findByToken(String token);
    
    Optional<Token> findByTokenAndType(String token, Token.TokenType type);
    
    @Modifying
    @Query("DELETE FROM Token t WHERE t.expiry < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM Token t WHERE t.email = :email AND t.type = :type")
    void deleteByEmailAndType(@Param("email") String email, @Param("type") Token.TokenType type);
}

