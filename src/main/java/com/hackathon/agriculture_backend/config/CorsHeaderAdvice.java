package com.hackathon.agriculture_backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@Slf4j
public class CorsHeaderAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // Apply to all responses
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, ServerHttpResponse response) {
        
        String origin = request.getHeaders().getFirst("Origin");
        log.error("CORS HEADER ADVICE - Origin: {}", origin);
        log.error("CORS HEADER ADVICE - Request URL: {}", request.getURI());
        
        if (origin != null) {
            log.error("CORS HEADER ADVICE - FORCING CORS HEADERS FOR: {}", origin);
            
            // Force CORS headers
            response.getHeaders().set("Access-Control-Allow-Origin", origin);
            response.getHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.getHeaders().set("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin");
            response.getHeaders().set("Access-Control-Allow-Credentials", "true");
            response.getHeaders().set("Access-Control-Max-Age", "3600");
            response.getHeaders().set("Access-Control-Expose-Headers", "Authorization, Content-Type");
            response.getHeaders().set("Vary", "Origin");
            
            log.error("CORS HEADER ADVICE - HEADERS SET FOR: {}", origin);
        }
        
        return body;
    }
}
