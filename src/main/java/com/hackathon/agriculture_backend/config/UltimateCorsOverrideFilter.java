package com.hackathon.agriculture_backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE - 1)
@Slf4j
public class UltimateCorsOverrideFilter implements Filter {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "https://agriculture-frontend-two.vercel.app",
        "https://agriculture-frontend.vercel.app",
        "https://agriculture-frontend-btleirx65.vercel.app",
        "http://localhost:3000",
        "http://127.0.0.1:3000"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String origin = httpRequest.getHeader("Origin");
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        
        log.error("ULTIMATE CORS OVERRIDE - Origin: {}, Method: {}, URI: {}", origin, method, uri);
        
        // Check if origin is allowed
        boolean isAllowedOrigin = origin != null && ALLOWED_ORIGINS.contains(origin);
        
        if (isAllowedOrigin) {
            log.error("ULTIMATE CORS OVERRIDE - ALLOWED ORIGIN DETECTED: {}", origin);
            
            // Completely override any existing headers
            httpResponse.reset();
            
            // Set CORS headers
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type, Access-Control-Allow-Origin");
            httpResponse.setHeader("Vary", "Origin");
            
            // Handle preflight requests
            if ("OPTIONS".equalsIgnoreCase(method)) {
                log.error("ULTIMATE CORS OVERRIDE - HANDLING PREFLIGHT REQUEST FOR: {}", origin);
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            log.error("ULTIMATE CORS OVERRIDE - CORS HEADERS SET FOR: {}", origin);
        } else {
            log.error("ULTIMATE CORS OVERRIDE - ORIGIN NOT ALLOWED: {}", origin);
        }
        
        chain.doFilter(request, response);
    }
}
