package com.hackathon.agriculture_backend.repository;

import com.hackathon.agriculture_backend.model.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    @Query("SELECT c FROM Chat c WHERE c.farmer.id = :farmerId ORDER BY c.createdAt DESC")
    List<Chat> findByFarmerIdOrderByCreatedAtDesc(@Param("farmerId") Long farmerId);
    
    @Query("SELECT c FROM Chat c WHERE c.farmer.id = :farmerId ORDER BY c.createdAt DESC")
    Page<Chat> findByFarmerIdOrderByCreatedAtDesc(@Param("farmerId") Long farmerId, Pageable pageable);
    
    @Query("SELECT c FROM Chat c WHERE c.farmer.id = :farmerId AND c.messageType = :messageType ORDER BY c.createdAt DESC")
    List<Chat> findByFarmerIdAndMessageTypeOrderByCreatedAtDesc(@Param("farmerId") Long farmerId, @Param("messageType") Chat.MessageType messageType);
    
    @Query("SELECT c FROM Chat c WHERE c.farmer.id = :farmerId AND c.createdAt >= :startDate AND c.createdAt <= :endDate ORDER BY c.createdAt DESC")
    List<Chat> findByFarmerIdAndDateRange(@Param("farmerId") Long farmerId, 
                                        @Param("startDate") Instant startDate, 
                                        @Param("endDate") Instant endDate);
    
    @Query("SELECT c FROM Chat c WHERE c.farmer.id = :farmerId AND (LOWER(c.userMessage) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.aiResponse) LIKE LOWER(CONCAT('%', :query, '%'))) ORDER BY c.createdAt DESC")
    List<Chat> findByFarmerIdAndSearchQuery(@Param("farmerId") Long farmerId, @Param("query") String query);
    
    @Query("SELECT c.messageType, COUNT(c) FROM Chat c WHERE c.farmer.id = :farmerId GROUP BY c.messageType")
    List<Object[]> getMessageTypeStatsByFarmerId(@Param("farmerId") Long farmerId);
    
    @Query("SELECT AVG(CASE WHEN c.isHelpful = true THEN 1.0 ELSE 0.0 END) FROM Chat c WHERE c.farmer.id = :farmerId AND c.isHelpful IS NOT NULL")
    Double getAverageHelpfulnessByFarmerId(@Param("farmerId") Long farmerId);
    
    @Query("SELECT COUNT(c) FROM Chat c WHERE c.farmer.id = :farmerId")
    Long countByFarmerId(@Param("farmerId") Long farmerId);
    
    @Query("SELECT COUNT(c) FROM Chat c WHERE c.farmer.id = :farmerId AND c.messageType = :messageType")
    Long countByFarmerIdAndMessageType(@Param("farmerId") Long farmerId, @Param("messageType") Chat.MessageType messageType);
    
    @Query("SELECT c FROM Chat c WHERE c.farmer.id = :farmerId ORDER BY c.createdAt DESC LIMIT :limit")
    List<Chat> findRecentChatsByFarmerId(@Param("farmerId") Long farmerId, @Param("limit") int limit);
}
