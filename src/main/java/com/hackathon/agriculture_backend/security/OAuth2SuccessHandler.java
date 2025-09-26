package com.hackathon.agriculture_backend.security;

import com.hackathon.agriculture_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();
            
            // Extract user information from Google OAuth2 response
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String picture = (String) attributes.get("picture");
            
            log.info("OAuth2 login successful for user: {}", email);
            
            // Create a simple user object for JWT generation
            // You might want to save this user to your database here
            String username = email != null ? email : "oauth2_user";
            
            // Generate JWT token
            String token = jwtUtil.generateToken(username);
            
            // Redirect to frontend with token
            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/callback")
                    .queryParam("token", token)
                    .queryParam("success", "true")
                    .build().toUriString();
            
            log.info("Redirecting to: {}", targetUrl);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
            
        } catch (Exception e) {
            log.error("Error in OAuth2 success handler: {}", e.getMessage(), e);
            
            // Redirect to frontend with error
            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/login")
                    .queryParam("error", "oauth2_error")
                    .build().toUriString();
            
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}
