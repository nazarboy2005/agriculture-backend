package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.dto.SavedIrrigationPlanDto;
import com.hackathon.agriculture_backend.model.Farmer;
import com.hackathon.agriculture_backend.model.SavedIrrigationPlan;
import com.hackathon.agriculture_backend.repository.FarmerRepository;
import com.hackathon.agriculture_backend.repository.SavedIrrigationPlanRepository;
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
public class SavedIrrigationPlanService {
    
    private final SavedIrrigationPlanRepository savedIrrigationPlanRepository;
    private final FarmerRepository farmerRepository;
    
    public SavedIrrigationPlanDto savePlan(Long farmerId, SavedIrrigationPlanDto planDto) {
        log.info("Saving irrigation plan for farmer: {} with name: {}", farmerId, planDto.getPlanName());
        
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found with id: " + farmerId));
        
        // Check if plan name already exists for this farmer
        Optional<SavedIrrigationPlan> existingPlan = savedIrrigationPlanRepository
                .findByFarmerIdAndPlanName(farmerId, planDto.getPlanName());
        
        if (existingPlan.isPresent()) {
            throw new RuntimeException("Plan with name '" + planDto.getPlanName() + "' already exists");
        }
        
        SavedIrrigationPlan plan = new SavedIrrigationPlan();
        plan.setFarmer(farmer);
        plan.setPlanName(planDto.getPlanName());
        plan.setLocationLat(planDto.getLocationLat());
        plan.setLocationLng(planDto.getLocationLng());
        plan.setCropType(planDto.getCropType());
        plan.setArea(planDto.getArea());
        plan.setIrrigationType(planDto.getIrrigationType());
        plan.setIrrigationRate(planDto.getIrrigationRate());
        plan.setEmittersPerM2(planDto.getEmittersPerM2());
        plan.setSoilType(planDto.getSoilType());
        plan.setWaterBudget(planDto.getWaterBudget());
        plan.setIsDefault(planDto.getIsDefault() != null ? planDto.getIsDefault() : false);
        
        // If this is set as default, unset other defaults
        if (plan.getIsDefault()) {
            unsetOtherDefaults(farmerId);
        }
        
        SavedIrrigationPlan savedPlan = savedIrrigationPlanRepository.save(plan);
        log.info("Irrigation plan saved successfully with ID: {}", savedPlan.getId());
        
        return convertToDto(savedPlan);
    }
    
    public List<SavedIrrigationPlanDto> getPlansByFarmerId(Long farmerId) {
        log.info("Fetching saved irrigation plans for farmer: {}", farmerId);
        
        List<SavedIrrigationPlan> plans = savedIrrigationPlanRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId);
        return plans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public SavedIrrigationPlanDto getPlanById(Long farmerId, Long planId) {
        log.info("Fetching irrigation plan: {} for farmer: {}", planId, farmerId);
        
        SavedIrrigationPlan plan = savedIrrigationPlanRepository.findByFarmerIdAndId(farmerId, planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + planId));
        
        return convertToDto(plan);
    }
    
    public SavedIrrigationPlanDto updatePlan(Long farmerId, Long planId, SavedIrrigationPlanDto planDto) {
        log.info("Updating irrigation plan: {} for farmer: {}", planId, farmerId);
        
        SavedIrrigationPlan plan = savedIrrigationPlanRepository.findByFarmerIdAndId(farmerId, planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + planId));
        
        // Check if new name conflicts with existing plans (excluding current plan)
        if (!plan.getPlanName().equals(planDto.getPlanName())) {
            Optional<SavedIrrigationPlan> existingPlan = savedIrrigationPlanRepository
                    .findByFarmerIdAndPlanName(farmerId, planDto.getPlanName());
            if (existingPlan.isPresent() && !existingPlan.get().getId().equals(planId)) {
                throw new RuntimeException("Plan with name '" + planDto.getPlanName() + "' already exists");
            }
        }
        
        plan.setPlanName(planDto.getPlanName());
        plan.setLocationLat(planDto.getLocationLat());
        plan.setLocationLng(planDto.getLocationLng());
        plan.setCropType(planDto.getCropType());
        plan.setArea(planDto.getArea());
        plan.setIrrigationType(planDto.getIrrigationType());
        plan.setIrrigationRate(planDto.getIrrigationRate());
        plan.setEmittersPerM2(planDto.getEmittersPerM2());
        plan.setSoilType(planDto.getSoilType());
        plan.setWaterBudget(planDto.getWaterBudget());
        
        // Handle default setting
        if (planDto.getIsDefault() != null && planDto.getIsDefault()) {
            unsetOtherDefaults(farmerId);
            plan.setIsDefault(true);
        }
        
        SavedIrrigationPlan updatedPlan = savedIrrigationPlanRepository.save(plan);
        log.info("Irrigation plan updated successfully");
        
        return convertToDto(updatedPlan);
    }
    
    @Transactional
    public void deletePlan(Long farmerId, Long planId) {
        log.info("Deleting irrigation plan: {} for farmer: {}", planId, farmerId);
        
        SavedIrrigationPlan plan = savedIrrigationPlanRepository.findByFarmerIdAndId(farmerId, planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + planId));
        
        savedIrrigationPlanRepository.delete(plan);
        log.info("Irrigation plan deleted successfully");
    }
    
    public SavedIrrigationPlanDto setAsDefault(Long farmerId, Long planId) {
        log.info("Setting irrigation plan: {} as default for farmer: {}", planId, farmerId);
        
        SavedIrrigationPlan plan = savedIrrigationPlanRepository.findByFarmerIdAndId(farmerId, planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + planId));
        
        // Unset other defaults
        unsetOtherDefaults(farmerId);
        
        // Set this plan as default
        plan.setIsDefault(true);
        SavedIrrigationPlan updatedPlan = savedIrrigationPlanRepository.save(plan);
        log.info("Plan set as default successfully");
        
        return convertToDto(updatedPlan);
    }
    
    private void unsetOtherDefaults(Long farmerId) {
        List<SavedIrrigationPlan> defaultPlans = savedIrrigationPlanRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId)
                .stream()
                .filter(SavedIrrigationPlan::getIsDefault)
                .collect(Collectors.toList());
        
        for (SavedIrrigationPlan defaultPlan : defaultPlans) {
            defaultPlan.setIsDefault(false);
            savedIrrigationPlanRepository.save(defaultPlan);
        }
    }
    
    private SavedIrrigationPlanDto convertToDto(SavedIrrigationPlan plan) {
        SavedIrrigationPlanDto dto = new SavedIrrigationPlanDto();
        dto.setId(plan.getId());
        dto.setFarmerId(plan.getFarmer().getId());
        dto.setPlanName(plan.getPlanName());
        dto.setLocationLat(plan.getLocationLat());
        dto.setLocationLng(plan.getLocationLng());
        dto.setCropType(plan.getCropType());
        dto.setArea(plan.getArea());
        dto.setIrrigationType(plan.getIrrigationType());
        dto.setIrrigationRate(plan.getIrrigationRate());
        dto.setEmittersPerM2(plan.getEmittersPerM2());
        dto.setSoilType(plan.getSoilType());
        dto.setWaterBudget(plan.getWaterBudget());
        dto.setIsDefault(plan.getIsDefault());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());
        return dto;
    }
}
