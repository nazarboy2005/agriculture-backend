package com.hackathon.agriculture_backend.dto;

import com.hackathon.agriculture_backend.model.FarmerZone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerZoneDto {
    
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String description;
    private Long farmerId;
    private Instant createdAt;
    private Instant updatedAt;
    
    public static FarmerZoneDto fromEntity(FarmerZone zone) {
        return new FarmerZoneDto(
            zone.getId(),
            zone.getName(),
            zone.getLatitude(),
            zone.getLongitude(),
            zone.getDescription(),
            zone.getFarmer().getId(),
            zone.getCreatedAt(),
            zone.getUpdatedAt()
        );
    }
}
