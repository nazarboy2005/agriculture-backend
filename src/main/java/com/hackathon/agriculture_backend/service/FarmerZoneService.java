package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.dto.FarmerZoneDto;
import com.hackathon.agriculture_backend.model.Farmer;
import com.hackathon.agriculture_backend.model.FarmerZone;
import com.hackathon.agriculture_backend.repository.FarmerZoneRepository;
import com.hackathon.agriculture_backend.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FarmerZoneService {
    
    private final FarmerZoneRepository farmerZoneRepository;
    private final FarmerRepository farmerRepository;
    
    public List<FarmerZoneDto> getZonesByFarmerId(Long farmerId) {
        log.info("Fetching zones for farmer ID: {}", farmerId);
        return farmerZoneRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId)
                .stream()
                .map(FarmerZoneDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public FarmerZoneDto createZone(Long farmerId, FarmerZoneDto zoneDto) {
        log.info("Creating zone for farmer ID: {} with name: {}", farmerId, zoneDto.getName());
        
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found with ID: " + farmerId));
        
        FarmerZone zone = new FarmerZone();
        zone.setName(zoneDto.getName());
        zone.setLatitude(zoneDto.getLatitude());
        zone.setLongitude(zoneDto.getLongitude());
        zone.setDescription(zoneDto.getDescription());
        zone.setFarmer(farmer);
        
        FarmerZone savedZone = farmerZoneRepository.save(zone);
        log.info("Zone created successfully with ID: {}", savedZone.getId());
        
        return FarmerZoneDto.fromEntity(savedZone);
    }
    
    public FarmerZoneDto updateZone(Long farmerId, Long zoneId, FarmerZoneDto zoneDto) {
        log.info("Updating zone ID: {} for farmer ID: {}", zoneId, farmerId);
        
        FarmerZone zone = farmerZoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found with ID: " + zoneId));
        
        if (!zone.getFarmer().getId().equals(farmerId)) {
            throw new RuntimeException("Zone does not belong to farmer ID: " + farmerId);
        }
        
        zone.setName(zoneDto.getName());
        zone.setLatitude(zoneDto.getLatitude());
        zone.setLongitude(zoneDto.getLongitude());
        zone.setDescription(zoneDto.getDescription());
        
        FarmerZone updatedZone = farmerZoneRepository.save(zone);
        log.info("Zone updated successfully");
        
        return FarmerZoneDto.fromEntity(updatedZone);
    }
    
    public void deleteZone(Long farmerId, Long zoneId) {
        log.info("Deleting zone ID: {} for farmer ID: {}", zoneId, farmerId);
        
        FarmerZone zone = farmerZoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found with ID: " + zoneId));
        
        if (!zone.getFarmer().getId().equals(farmerId)) {
            throw new RuntimeException("Zone does not belong to farmer ID: " + farmerId);
        }
        
        farmerZoneRepository.delete(zone);
        log.info("Zone deleted successfully");
    }
    
    public FarmerZoneDto getZoneById(Long farmerId, Long zoneId) {
        log.info("Fetching zone ID: {} for farmer ID: {}", zoneId, farmerId);
        
        FarmerZone zone = farmerZoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found with ID: " + zoneId));
        
        if (!zone.getFarmer().getId().equals(farmerId)) {
            throw new RuntimeException("Zone does not belong to farmer ID: " + farmerId);
        }
        
        return FarmerZoneDto.fromEntity(zone);
    }
}
