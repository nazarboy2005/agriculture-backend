package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.dto.FarmerDto;
import com.hackathon.agriculture_backend.model.Farmer;
import com.hackathon.agriculture_backend.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FarmerService {
    
    private final FarmerRepository farmerRepository;
    
    public FarmerDto createFarmer(FarmerDto farmerDto) {
        log.info("Creating farmer: {}", farmerDto.getName());
        
        // Check if farmer with same phone already exists
        if (farmerRepository.findByPhone(farmerDto.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Farmer with phone number " + farmerDto.getPhone() + " already exists");
        }
        
        Farmer farmer = convertToEntity(farmerDto);
        Farmer savedFarmer = farmerRepository.save(farmer);
        log.info("Farmer created successfully with ID: {}", savedFarmer.getId());
        
        return convertToDto(savedFarmer);
    }
    
    public Optional<FarmerDto> getFarmerById(Long id) {
        log.info("Fetching farmer with ID: {}", id);
        return farmerRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public Optional<FarmerDto> getFarmerByPhone(String phone) {
        log.info("Fetching farmer with phone: {}", phone);
        return farmerRepository.findByPhone(phone)
                .map(this::convertToDto);
    }
    
    public List<FarmerDto> getAllFarmers() {
        log.info("Fetching all farmers");
        return farmerRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<FarmerDto> getFarmersWithSmsOptIn() {
        log.info("Fetching farmers with SMS opt-in");
        return farmerRepository.findBySmsOptInTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<FarmerDto> getFarmersByLocation(String locationName) {
        log.info("Fetching farmers by location: {}", locationName);
        return farmerRepository.findByLocationName(locationName)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<FarmerDto> getFarmersByCrop(String preferredCrop) {
        log.info("Fetching farmers by crop: {}", preferredCrop);
        return farmerRepository.findByPreferredCrop(preferredCrop)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public FarmerDto updateFarmer(Long id, FarmerDto farmerDto) {
        log.info("Updating farmer with ID: {}", id);
        
        Farmer existingFarmer = farmerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found with ID: " + id));
        
        // Check if phone number is being changed and if it already exists
        if (!existingFarmer.getPhone().equals(farmerDto.getPhone())) {
            if (farmerRepository.findByPhone(farmerDto.getPhone()).isPresent()) {
                throw new IllegalArgumentException("Farmer with phone number " + farmerDto.getPhone() + " already exists");
            }
        }
        
        updateFarmerFromDto(existingFarmer, farmerDto);
        Farmer updatedFarmer = farmerRepository.save(existingFarmer);
        log.info("Farmer updated successfully with ID: {}", updatedFarmer.getId());
        
        return convertToDto(updatedFarmer);
    }
    
    public void deleteFarmer(Long id) {
        log.info("Deleting farmer with ID: {}", id);
        
        if (!farmerRepository.existsById(id)) {
            throw new IllegalArgumentException("Farmer not found with ID: " + id);
        }
        
        farmerRepository.deleteById(id);
        log.info("Farmer deleted successfully with ID: {}", id);
    }
    
    public Long getTotalFarmersCount() {
        return farmerRepository.count();
    }
    
    public Long getFarmersWithSmsOptInCount() {
        return farmerRepository.countBySmsOptInTrue();
    }
    
    private Farmer convertToEntity(FarmerDto dto) {
        Farmer farmer = new Farmer();
        farmer.setName(dto.getName());
        farmer.setPhone(dto.getPhone());
        farmer.setLocationName(dto.getLocationName());
        farmer.setLatitude(dto.getLatitude());
        farmer.setLongitude(dto.getLongitude());
        farmer.setPreferredCrop(dto.getPreferredCrop());
        farmer.setSmsOptIn(dto.getSmsOptIn());
        return farmer;
    }
    
    private FarmerDto convertToDto(Farmer farmer) {
        FarmerDto dto = new FarmerDto();
        dto.setId(farmer.getId());
        dto.setName(farmer.getName());
        dto.setPhone(farmer.getPhone());
        dto.setLocationName(farmer.getLocationName());
        dto.setLatitude(farmer.getLatitude());
        dto.setLongitude(farmer.getLongitude());
        dto.setPreferredCrop(farmer.getPreferredCrop());
        dto.setSmsOptIn(farmer.getSmsOptIn());
        dto.setCreatedAt(farmer.getCreatedAt());
        dto.setUpdatedAt(farmer.getUpdatedAt());
        return dto;
    }
    
    private void updateFarmerFromDto(Farmer farmer, FarmerDto dto) {
        farmer.setName(dto.getName());
        farmer.setPhone(dto.getPhone());
        farmer.setLocationName(dto.getLocationName());
        farmer.setLatitude(dto.getLatitude());
        farmer.setLongitude(dto.getLongitude());
        farmer.setPreferredCrop(dto.getPreferredCrop());
        farmer.setSmsOptIn(dto.getSmsOptIn());
    }
}



