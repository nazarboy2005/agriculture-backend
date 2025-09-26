package com.hackathon.agriculture_backend.config;

import com.hackathon.agriculture_backend.model.Crop;
import com.hackathon.agriculture_backend.model.Farmer;
import com.hackathon.agriculture_backend.repository.CropRepository;
import com.hackathon.agriculture_backend.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final CropRepository cropRepository;
    private final FarmerRepository farmerRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample data...");
        
        // Initialize default crops if they don't exist
        initializeCrops();
        
        // Initialize default farmers if they don't exist
        initializeFarmers();
        
        log.info("Sample data initialization completed");
    }
    
    private void initializeCrops() {
        if (cropRepository.count() == 0) {
            log.info("Creating default crops...");
            
            // Tomato
            Crop tomato = new Crop();
            tomato.setName("Tomato");
            tomato.setWaterDemandLitersPerHectare(6000.0);
            tomato.setEtThreshold(5.5);
            tomato.setDescription("High water demand crop, sensitive to drought");
            tomato.setIsActive(true);
            cropRepository.save(tomato);
            
            // Wheat
            Crop wheat = new Crop();
            wheat.setName("Wheat");
            wheat.setWaterDemandLitersPerHectare(4000.0);
            wheat.setEtThreshold(4.0);
            wheat.setDescription("Moderate water demand, winter crop");
            wheat.setIsActive(true);
            cropRepository.save(wheat);
            
            // Corn
            Crop corn = new Crop();
            corn.setName("Corn");
            corn.setWaterDemandLitersPerHectare(7000.0);
            corn.setEtThreshold(6.0);
            corn.setDescription("High water demand, summer crop");
            corn.setIsActive(true);
            cropRepository.save(corn);
            
            // Rice
            Crop rice = new Crop();
            rice.setName("Rice");
            rice.setWaterDemandLitersPerHectare(10000.0);
            rice.setEtThreshold(8.0);
            rice.setDescription("Very high water demand, requires flooding");
            rice.setIsActive(true);
            cropRepository.save(rice);
            
            // Lettuce
            Crop lettuce = new Crop();
            lettuce.setName("Lettuce");
            lettuce.setWaterDemandLitersPerHectare(3000.0);
            lettuce.setEtThreshold(3.5);
            lettuce.setDescription("Low water demand, leafy vegetable");
            lettuce.setIsActive(true);
            cropRepository.save(lettuce);
            
            // Potato
            Crop potato = new Crop();
            potato.setName("Potato");
            potato.setWaterDemandLitersPerHectare(5000.0);
            potato.setEtThreshold(4.5);
            potato.setDescription("Moderate water demand, root vegetable");
            potato.setIsActive(true);
            cropRepository.save(potato);
            
            log.info("Created {} default crops", cropRepository.count());
        } else {
            log.info("Crops already exist, skipping initialization");
        }
    }
    
    private void initializeFarmers() {
        if (farmerRepository.count() == 0) {
            log.info("Creating default farmers...");
            
            // Demo Farmer 1
            Farmer farmer1 = new Farmer();
            farmer1.setName("John Doe");
            farmer1.setPhone("+97412345678");
            farmer1.setLocationName("Doha Farm");
            farmer1.setLatitude(25.2854);
            farmer1.setLongitude(51.5310);
            farmer1.setPreferredCrop("Tomato");
            farmer1.setSmsOptIn(true);
            farmerRepository.save(farmer1);
            
            // Demo Farmer 2
            Farmer farmer2 = new Farmer();
            farmer2.setName("Ahmed Al-Rashid");
            farmer2.setPhone("+97487654321");
            farmer2.setLocationName("Al-Wakrah Farm");
            farmer2.setLatitude(25.1711);
            farmer2.setLongitude(51.6034);
            farmer2.setPreferredCrop("Wheat");
            farmer2.setSmsOptIn(true);
            farmerRepository.save(farmer2);
            
            // Demo Farmer 3
            Farmer farmer3 = new Farmer();
            farmer3.setName("Sarah Johnson");
            farmer3.setPhone("+97498765432");
            farmer3.setLocationName("Lusail Farm");
            farmer3.setLatitude(25.4244);
            farmer3.setLongitude(51.5074);
            farmer3.setPreferredCrop("Lettuce");
            farmer3.setSmsOptIn(false);
            farmerRepository.save(farmer3);
            
            log.info("Created {} default farmers", farmerRepository.count());
        } else {
            log.info("Farmers already exist, skipping initialization");
        }
    }
}


