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
@Order(Ordered.HIGHEST_PRECEDENCE - 10) // Even higher priority
@Slf4j
public class NuclearCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        log.error("NUCLEAR CORS FILTER - Request origin: {}", origin);
        log.error("NUCLEAR CORS FILTER - Request method: {}", request.getMethod());
        log.error("NUCLEAR CORS FILTER - Request URL: {}", request.getRequestURL());

        // NUCLEAR OPTION: Force CORS headers for ALL requests
        if (origin != null) {
            log.error("NUCLEAR CORS FILTER - DETECTED ORIGIN: {}", origin);
            
            // Completely override any existing headers
            response.reset();
            
            // Force set CORS headers
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
            response.setHeader("Vary", "Origin");
            
            log.error("NUCLEAR CORS FILTER - FORCED CORS HEADERS FOR: {}", origin);
        }

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.error("NUCLEAR CORS FILTER - HANDLING OPTIONS REQUEST");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
        
        // Post-process: Force headers again after response
        if (origin != null) {
            log.error("NUCLEAR CORS FILTER - POST-PROCESSING: Re-forcing headers for {}", origin);
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
    }
}
