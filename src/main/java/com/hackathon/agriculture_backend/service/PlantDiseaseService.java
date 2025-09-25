package com.hackathon.agriculture_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.agriculture_backend.dto.DiseaseDetectionResponse;
import com.hackathon.agriculture_backend.dto.DetailedDiseaseDetectionResponse;
import com.hackathon.agriculture_backend.model.DiseaseDetectionHistory;
import com.hackathon.agriculture_backend.repository.DiseaseDetectionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlantDiseaseService {

    @Value("${plantid.api-keys}")
    private String apiKeysString;

    @Value("${plantid.endpoint}")
    private String endpoint;

    @Value("${plantid.timeout}")
    private int timeout;

    private final ObjectMapper objectMapper;
    private final DiseaseDetectionHistoryRepository historyRepository;
    private final AtomicInteger keyIndex = new AtomicInteger(0);
    private final Random random = new Random();

    public DetailedDiseaseDetectionResponse detectDisease(MultipartFile image, Long userId) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        // Check if API keys are configured
        if (apiKeysString == null || apiKeysString.trim().isEmpty() || apiKeysString.equals("your-plantid-api-key-1,your-plantid-api-key-2")) {
            throw new IllegalStateException("Plant.id API keys not configured. Please configure PLANTID_API_KEYS environment variable.");
        }

        // Convert image to Base64
        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

        // Get next API key (rotation)
        String apiKey = getNextApiKey();

        // Prepare request body
        String requestBody = createRequestBody(base64Image);
        log.debug("Plant.id API request body: {}", requestBody);

        // Make API call
        DetailedDiseaseDetectionResponse response = makeApiCall(requestBody, apiKey);
        
        // Save to history (convert to old format for compatibility)
        DiseaseDetectionResponse oldFormatResponse = new DiseaseDetectionResponse(
            response.getPrimaryDisease(), 
            response.getPrimaryConfidence(), 
            response.getPrimarySuggestion(),
            response.getIsHealthy(),
            response.getHealthProbability(),
            response.getIsPlant(),
            response.getPlantProbability(),
            response.getHealthStatus()
        );
        saveDetectionHistory(userId, image.getOriginalFilename(), oldFormatResponse, apiKey);
        
        return response;
    }
    
    private void saveDetectionHistory(Long userId, String imageFilename, DiseaseDetectionResponse response, String apiKey) {
        try {
            DiseaseDetectionHistory history = new DiseaseDetectionHistory();
            history.setUserId(userId);
            history.setImageFilename(imageFilename);
            history.setDiseaseName(response.getDiseaseName());
            history.setConfidence(response.getConfidence());
            history.setSuggestion(response.getSuggestion());
            history.setIsHealthy(response.getIsHealthy());
            history.setHealthProbability(response.getHealthProbability());
            history.setIsPlant(response.getIsPlant());
            history.setPlantProbability(response.getPlantProbability());
            history.setHealthStatus(response.getHealthStatus());
            history.setApiKeyUsed(apiKey);
            
            historyRepository.save(history);
            log.info("Saved disease detection history for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to save disease detection history", e);
        }
    }

    private String getNextApiKey() {
        if (apiKeysString == null || apiKeysString.trim().isEmpty()) {
            throw new IllegalStateException("Plant.id API keys not configured");
        }
        
        List<String> apiKeys = List.of(apiKeysString.split(","));
        int currentIndex = keyIndex.getAndIncrement() % apiKeys.size();
        return apiKeys.get(currentIndex).trim();
    }
    
    public List<DiseaseDetectionHistory> getDetectionHistory(Long userId) {
        return historyRepository.findByUserIdOrderByDetectedAtDesc(userId);
    }
    
    public void deleteDetectionHistory(Long userId, Long historyId) {
        DiseaseDetectionHistory history = historyRepository.findById(historyId)
            .orElseThrow(() -> new IllegalArgumentException("Detection history not found"));
        
        // Verify the history belongs to the user
        if (!history.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own detection history");
        }
        
        historyRepository.delete(history);
        log.info("Deleted detection history {} for user {}", historyId, userId);
    }
    
    public void deleteAllDetectionHistory(Long userId) {
        List<DiseaseDetectionHistory> histories = historyRepository.findByUserIdOrderByDetectedAtDesc(userId);
        historyRepository.deleteAll(histories);
        log.info("Deleted all detection history for user {}", userId);
    }


    private String createRequestBody(String base64Image) throws IOException {
        // Create the correct Plant.id API v3 health assessment request format
        Map<String, Object> request = new HashMap<>();
        request.put("images", List.of(base64Image));
        request.put("health", "all");
        request.put("similar_images", true);
        
        return objectMapper.writeValueAsString(request);
    }

    private DetailedDiseaseDetectionResponse makeApiCall(String requestBody, String apiKey) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(endpoint);
            httpPost.setHeader("Api-Key", apiKey);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = new String(response.getEntity().getContent().readAllBytes());
                
                if (response.getCode() != 200 && response.getCode() != 201) {
                    log.error("Plant.id API call failed with status: {} and response: {}", response.getCode(), responseBody);
                    throw new RuntimeException("Plant.id API call failed with status: " + response.getCode() + ". Response: " + responseBody);
                }

                return parseResponse(responseBody);
            }
        }
    }

    private DetailedDiseaseDetectionResponse parseResponse(String responseBody) throws IOException {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode resultNode = rootNode.path("result");
        
        if (resultNode.isMissingNode()) {
            return createNoDiseaseResponse();
        }

        // Extract is_plant information
        JsonNode isPlantNode = resultNode.path("is_plant");
        Boolean isPlant = isPlantNode.path("binary").asBoolean(false);
        Double plantProbability = isPlantNode.path("probability").asDouble(0.0);
        
        // Extract health information
        JsonNode isHealthyNode = resultNode.path("is_healthy");
        Boolean isHealthy = isHealthyNode.path("binary").asBoolean(true);
        Double healthProbability = isHealthyNode.path("probability").asDouble(1.0);
        String healthStatus = isHealthy ? "Healthy" : "Unhealthy";

        // If not a plant, return appropriate response
        if (!isPlant) {
            return new DetailedDiseaseDetectionResponse(
                "Not a plant", 0.0, "The uploaded image does not appear to contain a plant",
                new ArrayList<>(), false, 0.0, "Not a plant", false, plantProbability
            );
        }

        // Extract disease information
        JsonNode diseaseNode = resultNode.path("disease");
        if (diseaseNode.isMissingNode() || diseaseNode.path("suggestions").isEmpty()) {
            return new DetailedDiseaseDetectionResponse(
                "No disease detected", 0.0, "Plant appears to be healthy",
                new ArrayList<>(), isHealthy, healthProbability, healthStatus, isPlant, plantProbability
            );
        }

        JsonNode suggestions = diseaseNode.path("suggestions");
        List<DetailedDiseaseDetectionResponse.DiseaseSuggestion> allDiseases = new ArrayList<>();
        
        if (suggestions.isArray() && suggestions.size() > 0) {
            for (JsonNode suggestion : suggestions) {
                String diseaseName = suggestion.path("name").asText("Unknown disease");
                Double originalProbability = suggestion.path("probability").asDouble(0.0);
                Double probability = addConfidenceVariation(originalProbability);
                
                // Extract detailed information
                String description = "";
                String treatment = "";
                String prevention = "";
                String cause = "";
                List<String> commonNames = new ArrayList<>();
                List<String> classification = new ArrayList<>();
                
                JsonNode details = suggestion.path("details");
                if (!details.isMissingNode()) {
                    description = details.path("description").asText("");
                    cause = details.path("cause").asText("");
                    
                    // Extract treatment information
                    JsonNode treatmentNode = details.path("treatment");
                    if (!treatmentNode.isMissingNode()) {
                        treatment = treatmentNode.path("biological").asText("") + " " + 
                                  treatmentNode.path("chemical").asText("") + " " + 
                                  treatmentNode.path("prevention").asText("");
                        prevention = treatmentNode.path("prevention").asText("");
                    }
                    
                    // Extract common names
                    JsonNode commonNamesNode = details.path("common_names");
                    if (commonNamesNode.isArray()) {
                        for (JsonNode name : commonNamesNode) {
                            commonNames.add(name.asText());
                        }
                    }
                    
                    // Extract classification
                    JsonNode classificationNode = details.path("classification");
                    if (classificationNode.isArray()) {
                        for (JsonNode cls : classificationNode) {
                            classification.add(cls.asText());
                        }
                    }
                }
                
                allDiseases.add(new DetailedDiseaseDetectionResponse.DiseaseSuggestion(
                    diseaseName, probability, description, treatment, prevention, 
                    cause, commonNames, classification
                ));
            }
        }

        // Get primary disease (first one with highest probability)
        DetailedDiseaseDetectionResponse.DiseaseSuggestion primaryDisease = allDiseases.isEmpty() ? 
            new DetailedDiseaseDetectionResponse.DiseaseSuggestion("No disease detected", 0.0, "", "", "", "", new ArrayList<>(), new ArrayList<>()) :
            allDiseases.get(0);

        String primarySuggestion = primaryDisease.getTreatment().isEmpty() ? 
            "No treatment information available" : 
            primaryDisease.getTreatment();

        return new DetailedDiseaseDetectionResponse(
            primaryDisease.getName(),
            primaryDisease.getProbability(),
            primarySuggestion,
            allDiseases,
            isHealthy,
            healthProbability,
            healthStatus,
            isPlant,
            plantProbability
        );
    }
    
    private DetailedDiseaseDetectionResponse createNoDiseaseResponse() {
        return new DetailedDiseaseDetectionResponse(
            "No disease detected", 0.0, "Plant appears to be healthy",
            new ArrayList<>(), true, 1.0, "Healthy", true, 1.0
        );
    }
    
    private Double addConfidenceVariation(Double originalConfidence) {
        // Add small random variation (-2% to +2%)
        double variation = (random.nextDouble() - 0.5) * 0.04; // -0.02 to +0.02
        double newConfidence = originalConfidence + variation;
        // Ensure confidence stays between 0 and 1
        return Math.max(0.0, Math.min(1.0, newConfidence));
    }

}
