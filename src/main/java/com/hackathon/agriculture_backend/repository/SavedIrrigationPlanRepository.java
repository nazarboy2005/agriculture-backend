package com.hackathon.agriculture_backend.repository;

import com.hackathon.agriculture_backend.model.SavedIrrigationPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedIrrigationPlanRepository extends JpaRepository<SavedIrrigationPlan, Long> {
    
    List<SavedIrrigationPlan> findByFarmerIdOrderByCreatedAtDesc(Long farmerId);
    
    Optional<SavedIrrigationPlan> findByFarmerIdAndId(Long farmerId, Long id);
    
    Optional<SavedIrrigationPlan> findByFarmerIdAndIsDefaultTrue(Long farmerId);
    
    @Query("SELECT s FROM SavedIrrigationPlan s WHERE s.farmer.id = :farmerId AND s.planName = :planName")
    Optional<SavedIrrigationPlan> findByFarmerIdAndPlanName(@Param("farmerId") Long farmerId, @Param("planName") String planName);
    
    void deleteByFarmerIdAndId(Long farmerId, Long id);
}
