package com.hackathon.agriculture_backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/cors-override")
@Slf4j
public class CorsOverrideController {

    @CrossOrigin(origins = {
        "https://agriculture-frontend-two.vercel.app",
        "https://agriculture-frontend.vercel.app", 
        "https://agriculture-frontend-btleirx65.vercel.app",
        "http://localhost:3000",
        "http://127.0.0.1:3000"
    })
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
    public ResponseEntity<String> handleCorsOverride(
            HttpServletRequest request, 
            HttpServletResponse response) {
        
        String origin = request.getHeader("Origin");
        log.error("CORS OVERRIDE CONTROLLER - Origin: {}", origin);
        log.error("CORS OVERRIDE CONTROLLER - Method: {}", request.getMethod());
        
        // Force CORS headers
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
            response.setHeader("Vary", "Origin");
            
            log.error("CORS OVERRIDE CONTROLLER - SET HEADERS FOR: {}", origin);
        }
        
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.error("CORS OVERRIDE CONTROLLER - OPTIONS REQUEST HANDLED");
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.ok("CORS Override Active");
    }
}
