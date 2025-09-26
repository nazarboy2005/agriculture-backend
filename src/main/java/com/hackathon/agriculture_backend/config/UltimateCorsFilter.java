package com.hackathon.agriculture_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class UltimateCorsFilter {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "https://agriculture-frontend-two.vercel.app",
        "https://agriculture-frontend.vercel.app",
        "https://agriculture-frontend-btleirx65.vercel.app"
    );

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public OncePerRequestFilter ultimateCorsOverrideFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                
                String origin = request.getHeader("Origin");
                String method = request.getMethod();
                String requestURI = request.getRequestURI();
                
                // ULTIMATE LOGGING
                System.out.println("=== ULTIMATE CORS FILTER ===");
                System.out.println("Origin: " + origin);
                System.out.println("Method: " + method);
                System.out.println("URI: " + requestURI);
                System.out.println("Request URL: " + request.getRequestURL());
                
                // ULTIMATE CORS HEADER OVERRIDE - ALLOW ALL ORIGINS
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
                response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, Origin, X-Requested-With, Access-Control-Request-Method, Access-Control-Request-Headers, Cache-Control, Pragma");
                response.setHeader("Access-Control-Max-Age", "86400");
                response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
                
                // REMOVE RAILWAY'S CORS HEADERS
                response.setHeader("Vary", "Origin");
                
                System.out.println("ULTIMATE CORS: Headers set for ALL origins (including: " + origin + ")");
                
                // Handle preflight requests
                if ("OPTIONS".equals(method)) {
                    System.out.println("ULTIMATE CORS: Handling OPTIONS preflight");
                    response.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
                
                // Continue with the request
                filterChain.doFilter(request, response);
                
                // Log final response headers
                System.out.println("ULTIMATE CORS: Final response headers:");
                response.getHeaderNames().forEach(headerName -> {
                    System.out.println("  " + headerName + ": " + response.getHeader(headerName));
                });
                System.out.println("=== END ULTIMATE CORS FILTER ===");
            }
        };
    }
}
