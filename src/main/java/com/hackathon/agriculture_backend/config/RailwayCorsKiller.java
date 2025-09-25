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
@Order(Ordered.HIGHEST_PRECEDENCE - 5)
@Slf4j
public class RailwayCorsKiller implements Filter {

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
        
        log.error("RAILWAY CORS KILLER - Origin: {}, Method: {}, URI: {}", origin, method, uri);
        
        // Check if origin is allowed
        boolean isAllowedOrigin = origin != null && ALLOWED_ORIGINS.contains(origin);
        
        if (isAllowedOrigin) {
            log.error("RAILWAY CORS KILLER - KILLING RAILWAY CORS FOR: {}", origin);
            
            // NUCLEAR OPTION: Create a custom response wrapper that overrides everything
            HttpServletResponse wrappedResponse = new HttpServletResponse() {
                private HttpServletResponse original = httpResponse;
                private boolean headersCommitted = false;
                
                @Override
                public void addHeader(String name, String value) {
                    if (!headersCommitted) {
                        if (name.equals("Access-Control-Allow-Origin")) {
                            original.setHeader(name, origin);
                            log.error("RAILWAY CORS KILLER - OVERRIDING CORS ORIGIN: {} -> {}", value, origin);
                        } else {
                            original.setHeader(name, value);
                        }
                    }
                }
                
                @Override
                public void setHeader(String name, String value) {
                    if (!headersCommitted) {
                        if (name.equals("Access-Control-Allow-Origin")) {
                            original.setHeader(name, origin);
                            log.error("RAILWAY CORS KILLER - OVERRIDING CORS ORIGIN: {} -> {}", value, origin);
                        } else {
                            original.setHeader(name, value);
                        }
                    }
                }
                
                @Override
                public void setStatus(int sc) {
                    original.setStatus(sc);
                }
                
                @Override
                public void sendError(int sc, String msg) throws IOException {
                    original.sendError(sc, msg);
                }
                
                @Override
                public void sendError(int sc) throws IOException {
                    original.sendError(sc);
                }
                
                @Override
                public void sendRedirect(String location) throws IOException {
                    original.sendRedirect(location);
                }
                
                @Override
                public String encodeRedirectURL(String url) {
                    return original.encodeRedirectURL(url);
                }
                
                @Override
                public String encodeURL(String url) {
                    return original.encodeURL(url);
                }
                
                @Override
                public void setDateHeader(String name, long date) {
                    original.setDateHeader(name, date);
                }
                
                @Override
                public void addDateHeader(String name, long date) {
                    original.addDateHeader(name, date);
                }
                
                @Override
                public void setIntHeader(String name, int value) {
                    original.setIntHeader(name, value);
                }
                
                @Override
                public void addIntHeader(String name, int value) {
                    original.addIntHeader(name, value);
                }
                
                @Override
                public void setContentLength(int len) {
                    original.setContentLength(len);
                }
                
                @Override
                public void setContentLengthLong(long len) {
                    original.setContentLengthLong(len);
                }
                
                @Override
                public void setContentType(String type) {
                    original.setContentType(type);
                }
                
                @Override
                public void setBufferSize(int size) {
                    original.setBufferSize(size);
                }
                
                @Override
                public int getBufferSize() {
                    return original.getBufferSize();
                }
                
                @Override
                public void flushBuffer() throws IOException {
                    original.flushBuffer();
                    headersCommitted = true;
                }
                
                @Override
                public void resetBuffer() {
                    original.resetBuffer();
                }
                
                @Override
                public boolean isCommitted() {
                    return original.isCommitted();
                }
                
                @Override
                public void reset() {
                    original.reset();
                }
                
                @Override
                public void setLocale(java.util.Locale loc) {
                    original.setLocale(loc);
                }
                
                @Override
                public java.util.Locale getLocale() {
                    return original.getLocale();
                }
                
                // Delegate all other methods to original response
                @Override
                public String getCharacterEncoding() {
                    return original.getCharacterEncoding();
                }
                
                @Override
                public String getContentType() {
                    return original.getContentType();
                }
                
                @Override
                public ServletOutputStream getOutputStream() throws IOException {
                    return original.getOutputStream();
                }
                
                @Override
                public java.io.PrintWriter getWriter() throws IOException {
                    return original.getWriter();
                }
                
                @Override
                public void setCharacterEncoding(String charset) {
                    original.setCharacterEncoding(charset);
                }
                
                @Override
                public int getStatus() {
                    return original.getStatus();
                }
                
                @Override
                public String getHeader(String name) {
                    return original.getHeader(name);
                }
                
                @Override
                public java.util.Collection<String> getHeaders(String name) {
                    return original.getHeaders(name);
                }
                
                @Override
                public java.util.Collection<String> getHeaderNames() {
                    return original.getHeaderNames();
                }
                
                @Override
                public boolean containsHeader(String name) {
                    return original.containsHeader(name);
                }
                
                @Override
                public void addCookie(jakarta.servlet.http.Cookie cookie) {
                    original.addCookie(cookie);
                }
            };
            
            // Handle preflight requests immediately
            if ("OPTIONS".equalsIgnoreCase(method)) {
                log.error("RAILWAY CORS KILLER - HANDLING PREFLIGHT REQUEST FOR: {}", origin);
                wrappedResponse.setHeader("Access-Control-Allow-Origin", origin);
                wrappedResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
                wrappedResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
                wrappedResponse.setHeader("Access-Control-Allow-Credentials", "true");
                wrappedResponse.setHeader("Access-Control-Max-Age", "3600");
                wrappedResponse.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            log.error("RAILWAY CORS KILLER - CORS KILLED FOR: {}", origin);
            chain.doFilter(request, wrappedResponse);
        } else {
            log.error("RAILWAY CORS KILLER - ORIGIN NOT ALLOWED: {}", origin);
            chain.doFilter(request, response);
        }
    }
}
