package com.hackathon.agriculture_backend.repository;

import com.hackathon.agriculture_backend.model.FarmerZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmerZoneRepository extends JpaRepository<FarmerZone, Long> {
    
    List<FarmerZone> findByFarmerId(Long farmerId);
    
    List<FarmerZone> findByFarmerIdOrderByCreatedAtDesc(Long farmerId);
    
    void deleteByFarmerIdAndId(Long farmerId, Long id);
}

