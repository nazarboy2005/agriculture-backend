package com.hackathon.agriculture_backend.repository;

import com.hackathon.agriculture_backend.model.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {
    
    Optional<Farmer> findByPhone(String phone);
    
    List<Farmer> findBySmsOptInTrue();
    
    List<Farmer> findByLocationName(String locationName);
    
    List<Farmer> findByPreferredCrop(String preferredCrop);
    
    @Query("SELECT f FROM Farmer f WHERE f.latitude BETWEEN :minLat AND :maxLat AND f.longitude BETWEEN :minLon AND :maxLon")
    List<Farmer> findByLocationWithinBounds(@Param("minLat") Double minLat, 
                                          @Param("maxLat") Double maxLat,
                                          @Param("minLon") Double minLon, 
                                          @Param("maxLon") Double maxLon);
    
    @Query("SELECT COUNT(f) FROM Farmer f WHERE f.smsOptIn = true")
    Long countBySmsOptInTrue();
    
    @Query("SELECT f.preferredCrop, COUNT(f) FROM Farmer f GROUP BY f.preferredCrop ORDER BY COUNT(f) DESC")
    List<Object[]> findCropDistribution();
}


