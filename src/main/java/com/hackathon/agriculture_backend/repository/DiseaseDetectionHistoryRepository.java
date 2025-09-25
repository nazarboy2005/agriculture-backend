package com.hackathon.agriculture_backend.repository;

import com.hackathon.agriculture_backend.model.DiseaseDetectionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseDetectionHistoryRepository extends JpaRepository<DiseaseDetectionHistory, Long> {
    
    List<DiseaseDetectionHistory> findByUserIdOrderByDetectedAtDesc(Long userId);
    
    @Query("SELECT d FROM DiseaseDetectionHistory d WHERE d.userId = :userId ORDER BY d.detectedAt DESC")
    List<DiseaseDetectionHistory> findRecentDetectionsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(d) FROM DiseaseDetectionHistory d WHERE d.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
