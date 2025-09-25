package com.hackathon.agriculture_backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Highest possible priority
@Slf4j
public class ForceCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        log.info("ForceCorsFilter - Request origin: {}", origin);
        log.info("ForceCorsFilter - Request method: {}", request.getMethod());
        log.info("ForceCorsFilter - Request URL: {}", request.getRequestURL());

        // Force CORS headers for all requests from allowed origins
        if (origin != null && (
            origin.equals("https://agriculture-frontend-two.vercel.app") ||
            origin.equals("https://agriculture-frontend.vercel.app") ||
            origin.equals("https://agriculture-frontend-btleirx65.vercel.app") ||
            origin.startsWith("http://localhost") ||
            origin.startsWith("http://127.0.0.1")
        )) {
            log.info("ForceCorsFilter - FORCING CORS headers for origin: {}", origin);
            
            // Remove any existing CORS headers first
            response.setHeader("Access-Control-Allow-Origin", "");
            response.setHeader("Access-Control-Allow-Methods", "");
            response.setHeader("Access-Control-Allow-Headers", "");
            response.setHeader("Access-Control-Allow-Credentials", "");
            response.setHeader("Access-Control-Max-Age", "");
            
            // Force set the correct CORS headers
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
            
            // Add Vary header to prevent caching issues
            response.setHeader("Vary", "Origin");
            
            log.info("ForceCorsFilter - CORS headers set successfully");
        }

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.info("ForceCorsFilter - Handling OPTIONS preflight request");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
        
        // After the response is processed, force the headers again
        if (origin != null && (
            origin.equals("https://agriculture-frontend-two.vercel.app") ||
            origin.equals("https://agriculture-frontend.vercel.app") ||
            origin.equals("https://agriculture-frontend-btleirx65.vercel.app") ||
            origin.startsWith("http://localhost") ||
            origin.startsWith("http://127.0.0.1")
        )) {
            log.info("ForceCorsFilter - Post-processing: Re-enforcing CORS headers");
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
        }
    }
}
