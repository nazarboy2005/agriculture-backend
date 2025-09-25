package com.hackathon.agriculture_backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Order(Ordered.HIGHEST_PRECEDENCE - 1) // Run before other filters
@Slf4j
public class RailwayCorsOverrideFilter implements Filter {

    @Value("${app.frontend.url:https://agriculture-frontend-two.vercel.app}")
    private String frontendUrl;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        log.info("Railway CORS Override - Request origin: {}", origin);
        log.info("Railway CORS Override - Frontend URL: {}", frontendUrl);

        // Override any existing CORS headers
        if (origin != null && (
            origin.equals("https://agriculture-frontend-two.vercel.app") ||
            origin.equals("https://agriculture-frontend.vercel.app") ||
            origin.equals("https://agriculture-frontend-btleirx65.vercel.app") ||
            origin.equals(frontendUrl) ||
            origin.startsWith("http://localhost") ||
            origin.startsWith("http://127.0.0.1")
        )) {
            log.info("Railway CORS Override - Setting CORS headers for origin: {}", origin);
            
            // Explicitly set CORS headers to override Railway defaults
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
            
            // Remove any Railway-injected headers
            response.setHeader("Vary", "Origin");
        }

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.info("Railway CORS Override - Handling OPTIONS preflight request");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }
}
