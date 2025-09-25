package com.hackathon.agriculture_backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RailwayCorsOverrideFilter implements Filter {

    private static final String FRONTEND_URL = "https://agriculture-frontend-two.vercel.app";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String origin = httpRequest.getHeader("Origin");
        log.error("RAILWAY CORS OVERRIDE - Request from origin: {}", origin);
        log.error("RAILWAY CORS OVERRIDE - Request method: {}", httpRequest.getMethod());
        log.error("RAILWAY CORS OVERRIDE - Request URI: {}", httpRequest.getRequestURI());
        
        // Force CORS headers to override Railway's platform-level CORS
        if (origin != null && (origin.equals(FRONTEND_URL) || origin.contains("vercel.app"))) {
            log.error("RAILWAY CORS OVERRIDE - OVERRIDING CORS FOR: {}", origin);
            
            // Remove any existing CORS headers that Railway might have set
            httpResponse.reset();
            
            // Set our own CORS headers
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
            httpResponse.setHeader("Vary", "Origin");
            
            log.error("RAILWAY CORS OVERRIDE - CORS HEADERS SET FOR: {}", origin);
        }
        
        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            log.error("RAILWAY CORS OVERRIDE - HANDLING PREFLIGHT REQUEST");
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        chain.doFilter(request, response);
    }
}
