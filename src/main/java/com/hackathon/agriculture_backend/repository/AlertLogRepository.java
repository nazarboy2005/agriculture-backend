package com.hackathon.agriculture_backend.repository;

import com.hackathon.agriculture_backend.model.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {
    
    List<AlertLog> findByFarmerIdOrderByCreatedAtDesc(Long farmerId);
    
    List<AlertLog> findByStatus(String status);
    
    List<AlertLog> findByType(String type);
    
    List<AlertLog> findByFarmerIdAndStatus(Long farmerId, String status);
    
    List<AlertLog> findByCreatedAtBetween(Instant startTime, Instant endTime);
    
    @Query("SELECT al FROM AlertLog al WHERE al.farmer.id = :farmerId AND al.type = :type ORDER BY al.createdAt DESC")
    List<AlertLog> findByFarmerIdAndType(@Param("farmerId") Long farmerId, @Param("type") String type);
    
    @Query("SELECT COUNT(al) FROM AlertLog al WHERE al.status = :status AND al.createdAt BETWEEN :startTime AND :endTime")
    Long countByStatusAndDateRange(@Param("status") String status, 
                                  @Param("startTime") Instant startTime, 
                                  @Param("endTime") Instant endTime);
    
    @Query("SELECT al.type, COUNT(al) FROM AlertLog al WHERE al.createdAt BETWEEN :startTime AND :endTime GROUP BY al.type")
    List<Object[]> findAlertTypeDistributionByDateRange(@Param("startTime") Instant startTime, 
                                                       @Param("endTime") Instant endTime);
    
    @Query("SELECT COUNT(al) FROM AlertLog al WHERE al.farmer.id = :farmerId AND al.status = 'SENT' AND al.createdAt BETWEEN :startTime AND :endTime")
    Long countSuccessfulAlertsByFarmerAndDateRange(@Param("farmerId") Long farmerId,
                                                  @Param("startTime") Instant startTime,
                                                  @Param("endTime") Instant endTime);
}
