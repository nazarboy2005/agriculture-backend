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
import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CorsFilter implements Filter {

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    private final List<String> allowedOrigins = Arrays.asList(
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "https://agriculture-frontend-two.vercel.app",
        "https://agriculture-frontend.vercel.app",
        "https://agriculture-frontend-btleirx65.vercel.app"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        log.debug("CORS Filter - Request origin: {}", origin);
        log.debug("CORS Filter - Frontend URL: {}", frontendUrl);

        // Check if origin is allowed
        if (origin != null && (allowedOrigins.contains(origin) || origin.equals(frontendUrl) || origin.endsWith(".vercel.app"))) {
            log.info("CORS Filter - Allowing origin: {}", origin);
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else if (origin != null) {
            log.warn("CORS Filter - Rejecting origin: {}", origin);
            // Don't set CORS headers for disallowed origins
        }

        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("CORS Filter - Handling OPTIONS preflight request");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }
}
