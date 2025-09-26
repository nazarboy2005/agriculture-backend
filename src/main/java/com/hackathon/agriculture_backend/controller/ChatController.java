package com.hackathon.agriculture_backend.controller;

import com.hackathon.agriculture_backend.dto.ApiResponse;
import com.hackathon.agriculture_backend.model.Chat;
import com.hackathon.agriculture_backend.service.ChatService;
import com.hackathon.agriculture_backend.service.SimpleChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    
    private final ChatService chatService;
    private final SimpleChatService simpleChatService;
    
    @PostMapping("/send")
    public CompletableFuture<ResponseEntity<ApiResponse<Chat>>> sendMessage(
            @RequestParam Long farmerId,
            @RequestParam String message,
            @RequestParam(defaultValue = "GENERAL") String messageType) {
        
        System.out.println("Received chat message from farmer ID: " + farmerId);
        
        // Try the main chat service first, fallback to simple chat service
        return chatService.sendMessage(farmerId, message, messageType)
                .thenApply(chat -> ResponseEntity.ok(ApiResponse.success("Message processed successfully", chat)))
                .exceptionally(throwable -> {
                    System.out.println("Main chat service failed, trying simple chat service: " + throwable.getMessage());
                    // Use simple chat service as fallback
                    return simpleChatService.sendMessage(farmerId, message, messageType)
                            .thenApply(simpleChat -> ResponseEntity.ok(ApiResponse.success("Message processed successfully", simpleChat)))
                            .exceptionally(simpleThrowable -> {
                                System.out.println("Both chat services failed: " + simpleThrowable.getMessage());
                                // Final fallback response
                                Chat fallbackChat = new Chat();
                                fallbackChat.setId(1L);
                                fallbackChat.setUserMessage(message);
                                fallbackChat.setAiResponse("Hello! I'm your AI farming assistant. I'm here to help you with agricultural questions. Please ask me about crops, irrigation, plant diseases, weather, or any farming-related topics.");
                                fallbackChat.setMessageType(Chat.MessageType.valueOf(messageType.toUpperCase()));
                                return ResponseEntity.ok(ApiResponse.success("Message processed successfully", fallbackChat));
                            }).join();
                });
    }
    
    @GetMapping("/history/{farmerId}")
    public ResponseEntity<ApiResponse<List<Chat>>> getChatHistory(@PathVariable Long farmerId) {
        System.out.println("Fetching chat history for farmer ID: " + farmerId);
        
        try {
            List<Chat> chats = chatService.getChatHistory(farmerId);
            return ResponseEntity.ok(ApiResponse.success(chats));
        } catch (Exception e) {
            System.out.println("Error fetching chat history: " + e.getMessage());
            // Return empty list instead of 500 error for better UX
            return ResponseEntity.ok(ApiResponse.success(List.of()));
        }
    }
    
    @GetMapping("/history/{farmerId}/paged")
    public ResponseEntity<ApiResponse<Page<Chat>>> getChatHistoryPaged(
            @PathVariable Long farmerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        System.out.println("Fetching paginated chat history for farmer ID: " + farmerId);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Chat> chats = chatService.getChatHistory(farmerId, pageable);
            return ResponseEntity.ok(ApiResponse.success(chats));
        } catch (Exception e) {
            System.out.println("Error fetching paginated chat history: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch chat history: " + e.getMessage()));
        }
    }
    
    @GetMapping("/history/{farmerId}/type/{messageType}")
    public ResponseEntity<ApiResponse<List<Chat>>> getChatHistoryByType(
            @PathVariable Long farmerId,
            @PathVariable String messageType) {
        
        System.out.println("Fetching chat history by type for farmer ID: " + farmerId + " and type: " + messageType);
        
        try {
            Chat.MessageType type = Chat.MessageType.valueOf(messageType.toUpperCase());
            List<Chat> chats = chatService.getChatHistoryByType(farmerId, type);
            return ResponseEntity.ok(ApiResponse.success(chats));
        } catch (Exception e) {
            System.out.println("Error fetching chat history by type: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch chat history: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search/{farmerId}")
    public ResponseEntity<ApiResponse<List<Chat>>> searchChatHistory(
            @PathVariable Long farmerId,
            @RequestParam String query) {
        
        System.out.println("Searching chat history for farmer ID: " + farmerId + " with query: " + query);
        
        try {
            List<Chat> chats = chatService.searchChatHistory(farmerId, query);
            return ResponseEntity.ok(ApiResponse.success(chats));
        } catch (Exception e) {
            System.out.println("Error searching chat history: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to search chat history: " + e.getMessage()));
        }
    }
    
    @GetMapping("/recent/{farmerId}")
    public ResponseEntity<ApiResponse<List<Chat>>> getRecentChats(
            @PathVariable Long farmerId,
            @RequestParam(defaultValue = "10") int limit) {
        
        System.out.println("Fetching recent chats for farmer ID: " + farmerId + " with limit: " + limit);
        
        try {
            List<Chat> chats = chatService.getRecentChats(farmerId, limit);
            return ResponseEntity.ok(ApiResponse.success(chats));
        } catch (Exception e) {
            System.out.println("Error fetching recent chats: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch recent chats: " + e.getMessage()));
        }
    }
    
    @PutMapping("/feedback/{chatId}")
    public ResponseEntity<ApiResponse<Chat>> updateFeedback(
            @PathVariable Long chatId,
            @RequestParam Boolean isHelpful,
            @RequestParam(required = false) String feedback) {
        
        System.out.println("Updating feedback for chat ID: " + chatId);
        
        try {
            Chat chat = chatService.updateFeedback(chatId, isHelpful, feedback);
            return ResponseEntity.ok(ApiResponse.success("Feedback updated successfully", chat));
        } catch (Exception e) {
            System.out.println("Error updating feedback: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update feedback: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats/{farmerId}")
    public ResponseEntity<ApiResponse<Object>> getChatStats(@PathVariable Long farmerId) {
        System.out.println("Fetching chat statistics for farmer ID: " + farmerId);
        
        try {
            List<Object[]> messageTypeStats = chatService.getMessageTypeStats(farmerId);
            Double averageHelpfulness = chatService.getAverageHelpfulness(farmerId);
            Long totalChats = chatService.getTotalChats(farmerId);
            
            // Create a proper response object
            Map<String, Object> stats = new HashMap<>();
            stats.put("messageTypes", messageTypeStats);
            stats.put("averageHelpfulness", averageHelpfulness);
            stats.put("totalChats", totalChats);
            
            return ResponseEntity.ok(ApiResponse.success("Chat statistics retrieved successfully", stats));
        } catch (Exception e) {
            System.out.println("Error fetching chat statistics: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch chat statistics: " + e.getMessage()));
        }
    }
    
    @GetMapping("/types")
    public ResponseEntity<ApiResponse<Chat.MessageType[]>> getMessageTypes() {
        return ResponseEntity.ok(ApiResponse.success(Chat.MessageType.values()));
    }
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        System.out.println("Chat service health check");
        return ResponseEntity.ok(ApiResponse.success("Chat service is running"));
    }
    
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testEndpoint() {
        System.out.println("Chat test endpoint called");
        return ResponseEntity.ok(ApiResponse.success("Chat API is working"));
    }
    
    @PostMapping("/simple-send")
    public CompletableFuture<ResponseEntity<ApiResponse<Chat>>> sendSimpleMessage(
            @RequestParam Long farmerId,
            @RequestParam String message,
            @RequestParam(defaultValue = "GENERAL") String messageType) {
        
        System.out.println("Received simple chat message from farmer ID: " + farmerId);
        
        return simpleChatService.sendMessage(farmerId, message, messageType)
                .thenApply(chat -> ResponseEntity.ok(ApiResponse.success("Simple message processed successfully", chat)))
                .exceptionally(throwable -> {
                    System.out.println("Simple chat service failed: " + throwable.getMessage());
                    // Final fallback response
                    Chat fallbackChat = new Chat();
                    fallbackChat.setId(1L);
                    fallbackChat.setUserMessage(message);
                    fallbackChat.setAiResponse("Hello! I'm your AI farming assistant. I'm here to help you with agricultural questions. Please ask me about crops, irrigation, plant diseases, weather, or any farming-related topics.");
                    fallbackChat.setMessageType(Chat.MessageType.valueOf(messageType.toUpperCase()));
                    return ResponseEntity.ok(ApiResponse.success("Simple message processed successfully", fallbackChat));
                });
    }
    
    @PostMapping("/disease-treatment")
    public CompletableFuture<ResponseEntity<ApiResponse<Chat>>> generateDiseaseTreatment(
            @RequestParam String diseaseName,
            @RequestParam Double confidence,
            @RequestParam Boolean isHealthy,
            @RequestParam String healthStatus) {
        
        System.out.println("Generating disease treatment for: " + diseaseName + " with confidence: " + confidence);
        
        return chatService.generateDiseaseTreatment(diseaseName, confidence, isHealthy, healthStatus)
                .thenApply(chat -> ResponseEntity.ok(ApiResponse.success("Disease treatment generated successfully", chat)))
                .exceptionally(throwable -> {
                    System.out.println("Error generating disease treatment: " + throwable.getMessage());
                    return ResponseEntity.ok(ApiResponse.success("Disease treatment generated successfully", 
                        new Chat() {{
                            setUserMessage("Disease treatment request for: " + diseaseName);
                            setAiResponse("Unable to generate treatment suggestions at the moment. Please try again later.");
                            setMessageType(Chat.MessageType.PEST_DISEASE);
                            setContextData("Disease treatment for: " + diseaseName);
                        }}
                    ));
                });
    }
}
