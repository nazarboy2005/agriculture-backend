package com.hackathon.agriculture_backend.repository;

import com.hackathon.agriculture_backend.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    
    Optional<Crop> findByName(String name);
    
    List<Crop> findByIsActiveTrue();
    
    List<Crop> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT c FROM Crop c WHERE c.isActive = true ORDER BY c.name")
    List<Crop> findAllActiveOrderByName();
}
