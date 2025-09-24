package com.hackathon.agriculture_backend.repository;

import com.hackathon.agriculture_backend.model.IrrigationRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IrrigationRecommendationRepository extends JpaRepository<IrrigationRecommendation, Long> {
    
    List<IrrigationRecommendation> findByFarmerIdOrderByDateDesc(Long farmerId);
    
    List<IrrigationRecommendation> findByFarmerIdAndDateBetween(Long farmerId, LocalDate startDate, LocalDate endDate);
    
    List<IrrigationRecommendation> findByDate(LocalDate date);
    
    List<IrrigationRecommendation> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<IrrigationRecommendation> findByRecommendation(String recommendation);
    
    List<IrrigationRecommendation> findByLocationName(String locationName);
    
    @Query("SELECT ir FROM IrrigationRecommendation ir WHERE ir.farmer.id = :farmerId ORDER BY ir.date DESC")
    List<IrrigationRecommendation> findLatestByFarmerId(@Param("farmerId") Long farmerId);
    
    @Query("SELECT ir FROM IrrigationRecommendation ir WHERE ir.farmer.id = :farmerId AND ir.date = :date")
    List<IrrigationRecommendation> findByFarmerIdAndDate(@Param("farmerId") Long farmerId, @Param("date") LocalDate date);
    
    @Query("SELECT SUM(ir.waterSavedLiters) FROM IrrigationRecommendation ir WHERE ir.farmer.id = :farmerId AND ir.date BETWEEN :startDate AND :endDate")
    Double calculateWaterSavedByFarmerAndDateRange(@Param("farmerId") Long farmerId, 
                                                 @Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(ir.waterSavedLiters) FROM IrrigationRecommendation ir WHERE ir.date BETWEEN :startDate AND :endDate")
    Double calculateTotalWaterSavedByDateRange(@Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);
    
    @Query("SELECT ir.recommendation, COUNT(ir) FROM IrrigationRecommendation ir WHERE ir.date BETWEEN :startDate AND :endDate GROUP BY ir.recommendation")
    List<Object[]> findRecommendationDistributionByDateRange(@Param("startDate") LocalDate startDate, 
                                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(DISTINCT ir.farmer.id) FROM IrrigationRecommendation ir WHERE ir.date BETWEEN :startDate AND :endDate")
    Long countActiveFarmersByDateRange(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
}
