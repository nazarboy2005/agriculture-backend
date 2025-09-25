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
@Order(Ordered.HIGHEST_PRECEDENCE - 4)
@Slf4j
public class SimpleCorsFixFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String origin = httpRequest.getHeader("Origin");
        String method = httpRequest.getMethod();
        
        log.error("SIMPLE CORS FIX - Origin: {}, Method: {}", origin, method);
        
        // Always set CORS headers for Vercel origins
        if (origin != null && origin.contains("vercel.app")) {
            log.error("SIMPLE CORS FIX - FIXING CORS FOR VERCEL: {}", origin);
            
            // Force the correct CORS headers
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type, Access-Control-Allow-Origin");
            httpResponse.setHeader("Vary", "Origin");
            
            // Handle preflight requests
            if ("OPTIONS".equalsIgnoreCase(method)) {
                log.error("SIMPLE CORS FIX - HANDLING PREFLIGHT FOR: {}", origin);
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            log.error("SIMPLE CORS FIX - CORS FIXED FOR: {}", origin);
        }
        
        chain.doFilter(request, response);
    }
}
